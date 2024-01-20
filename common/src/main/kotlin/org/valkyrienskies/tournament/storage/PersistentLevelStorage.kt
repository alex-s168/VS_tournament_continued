package org.valkyrienskies.tournament.storage

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData
import kotlin.reflect.KProperty

abstract class PersistentLevelStorage<T: PersistentLevelStorage<T>>(
    val id: ResourceLocation
): SavedData() {

    var nbtCompound: CompoundTag = CompoundTag()
        private set

    internal fun read(nbt: CompoundTag) {
        nbtCompound = nbt.copy()
    }

    override fun save(compoundTag: CompoundTag): CompoundTag =
        nbtCompound.allKeys.forEach { key ->
            compoundTag.put(key, nbtCompound.get(key)!!)
        }.let {
            compoundTag
        }

    open class NBTDelegate<T, X: PersistentLevelStorage<X>> internal constructor(
        val get: (CompoundTag, String) -> T,
        val set: (CompoundTag, String, T) -> Unit,
    ) {
        operator fun getValue(thisRef: PersistentLevelStorage<X>, property: KProperty<*>): T =
            get(thisRef.nbtCompound, property.name)

        operator fun setValue(thisRef: PersistentLevelStorage<X>, property: KProperty<*>, value: T) =
            set(thisRef.nbtCompound, property.name, value).also {
                thisRef.setDirty()
            }
    }

    open class SerializingNBTDelegate<T, X: PersistentLevelStorage<X>> internal constructor(
        private val serial: NBTSerializer<T>,
    ): NBTDelegate<T, X>(
        get = { nbt, key ->
            if (!nbt.contains(key))
                nbt.put(key, CompoundTag())
            serial.read(nbt.getCompound(key))
        },
        set = { nbt, key, value ->
            serial.write(nbt.getCompound(key), value)
        }
    ), NBTSerializer<T> {
        override fun write(nbt: CompoundTag, t: T) {
            serial.write(nbt, t)
        }

        override fun read(nbt: CompoundTag): T {
            return serial.read(nbt)
        }
    }

    fun <E> nbtDelegate(
        get: (CompoundTag) -> E,
        set: (CompoundTag, E) -> Unit,
    ) = object: SerializingNBTDelegate<E, T>(
        serial = object: NBTSerializer<E> {
            override fun write(nbt: CompoundTag, t: E) {
                set(nbt, t)
            }

            override fun read(nbt: CompoundTag): E {
                return get(nbt)
            }
        }
    ) {}

    fun <E> nbtDelegate(
        serial: NBTSerializer<E>,
    ) = SerializingNBTDelegate<E, T>(serial = serial)

    fun <E> nbtList(
        elemSerial: NBTSerializer<E>,
    ) = nbtDelegate(NBTList.serializer(elemSerial))

    val nbtInt =
        nbtDelegate(NBTInt)

    val nbtLong =
        nbtDelegate(NBTLong)

    val nbtFloat =
        nbtDelegate(NBTFloat)

    val nbtDouble =
        nbtDelegate(NBTDouble)

    val nbtBool =
        nbtDelegate(NBTBool)

    val nbtStr =
        nbtDelegate(NBTString)

    val nbtChunkPos =
        nbtDelegate(NBTChunkPos)
}

fun <T: PersistentLevelStorage<T>> ServerLevel.readStorage(storage: T): T =
    storage.also {
        dataStorage.computeIfAbsent(
            { nbt -> storage.also { storage.read(nbt) } },
            { storage },
            storage.id.toString()
        )
    }
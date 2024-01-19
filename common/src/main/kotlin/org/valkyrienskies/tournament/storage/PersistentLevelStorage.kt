package org.valkyrienskies.tournament.storage

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
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

    fun <E> nbtDelegate(
        get: (CompoundTag, String) -> E,
        set: (CompoundTag, String, E) -> Unit,
    ) = object : NBTDelegate<E, T>(get, set) {}

    fun <E: NBTDelegate<O, T>, O> nbtList(factory: () -> E): NBTDelegate<NBTList<O>, T> =
        nbtDelegate(
            get = { nbt, key ->
                if (!nbt.contains(key))
                    nbt.put(key, CompoundTag().also {
                        it.putInt("size", 0)
                    })

                NBTList<O>(
                    nbt.getCompound(key),
                    { c -> factory().get(c, "value") },
                    { c, e -> factory().also {
                        it.set(c, "value", e)
                    } }
                )
            },
            set = { nbt, key, value ->
                nbt.put(key, value.nbt)
            }
        )

    fun nbtChunkPos(default: ChunkPos = ChunkPos(0, 0)): NBTDelegate<ChunkPos, T> =
        nbtDelegate(
            get = { nbt, key ->
                if (!nbt.contains(key))
                    nbt.put(key, CompoundTag().also {
                        it.putInt("x", default.x)
                        it.putInt("z", default.z)
                    })
                ChunkPos(
                    nbt.getCompound(key).getInt("x"),
                    nbt.getCompound(key).getInt("z")
                )
            },
            set = { nbt, key, value ->
                nbt.put(key, CompoundTag().also {
                    it.putInt("x", value.x)
                    it.putInt("z", value.z)
                })
            }
        )

    fun nbtStr(default: String? = null): NBTDelegate<String, T> =
        nbtDelegate(
            get = { nbt, key ->
                if (!nbt.contains(key) && default != null)
                    nbt.putString(key, default)
                nbt.getString(key)
            },
            set = { nbt, key, value ->
                nbt.putString(key, value)
            }
        )

    fun nbtInt(default: Int? = null): NBTDelegate<Int, T> =
        nbtDelegate(
            get = { nbt, key ->
                if (!nbt.contains(key) && default != null)
                    nbt.putInt(key, default)
                nbt.getInt(key)
            },
            set = { nbt, key, value ->
                nbt.putInt(key, value)
            }
        )

    fun nbtLong(default: Long? = null): NBTDelegate<Long, T> =
        nbtDelegate(
            get = { nbt, key ->
                if (!nbt.contains(key) && default != null)
                    nbt.putLong(key, default)
                nbt.getLong(key)
            },
            set = { nbt, key, value ->
                nbt.putLong(key, value)
            }
        )

    fun nbtFloat(default: Float? = null): NBTDelegate<Float, T> =
        nbtDelegate(
            get = { nbt, key ->
                if (!nbt.contains(key) && default != null)
                    nbt.putFloat(key, default)
                nbt.getFloat(key)
            },
            set = { nbt, key, value ->
                nbt.putFloat(key, value)
            }
        )

    fun nbtDouble(default: Double? = null): NBTDelegate<Double, T> =
        nbtDelegate(
            get = { nbt, key ->
                if (!nbt.contains(key) && default != null)
                    nbt.putDouble(key, default)
                nbt.getDouble(key)
            },
            set = { nbt, key, value ->
                nbt.putDouble(key, value)
            }
        )

    fun nbtBool(default: Boolean? = null): NBTDelegate<Boolean, T> =
        nbtDelegate(
            get = { nbt, key ->
                if (!nbt.contains(key) && default != null)
                    nbt.putBoolean(key, default)
                nbt.getBoolean(key)
            },
            set = { nbt, key, value ->
                nbt.putBoolean(key, value)
            }
        )
}

fun <T: PersistentLevelStorage<T>> ServerLevel.readStorage(storage: T): T =
    storage.also {
        dataStorage.computeIfAbsent(
            { nbt -> storage.also { storage.read(nbt) } },
            { storage },
            storage.id.toString()
        )
    }
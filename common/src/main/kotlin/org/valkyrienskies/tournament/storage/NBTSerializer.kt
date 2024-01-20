package org.valkyrienskies.tournament.storage

import net.minecraft.nbt.CompoundTag

interface NBTSerializer<T> {
    fun write(nbt: CompoundTag, t: T)

    fun read(nbt: CompoundTag): T

    companion object {
        fun <T> new(write: (CompoundTag, T) -> Unit, read: (CompoundTag) -> T): NBTSerializer<T> =
            object : NBTSerializer<T> {
                override fun write(nbt: CompoundTag, t: T) {
                    write(nbt, t)
                }

                override fun read(nbt: CompoundTag): T =
                    read(nbt)
            }
    }
}
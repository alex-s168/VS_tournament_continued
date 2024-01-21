package org.valkyrienskies.tournament.storage

import net.minecraft.nbt.CompoundTag

class NBTList<T>(
    val nbt: CompoundTag,
    private val serializer: NBTSerializer<T>,
): AbstractMutableList<T>() {
    override val size: Int
        get() = nbt.getInt("size")

    override fun get(index: Int): T =
        serializer.read(nbt.getCompound(index.toString()))

    override fun removeAt(index: Int): T {
        val t = get(index)
        for (i in index until size - 1) {
            nbt.put(index.toString(), nbt.getCompound((index + 1).toString()))
        }
        nbt.remove((size - 1).toString())
        nbt.putInt("size", size - 1)
        return t
    }

    override fun set(index: Int, element: T): T =
        serializer.write(nbt.getCompound(index.toString()), element).let {
            element
        }

    override fun add(index: Int, element: T) {
        serializer.write(nbt.getCompound(index.toString()), element)
        if (index == size)
            nbt.putInt("size", size + 1)
    }

    override fun clear() {
        for (i in 0 until size) {
            nbt.remove(i.toString())
        }
        nbt.putInt("size", 0)
    }

    private class Serializer<T>(
        private val elemSerializer: NBTSerializer<T>,
    ): NBTSerializer<MutableList<T>> {
        override fun write(nbt: CompoundTag, t: MutableList<T>) {
            nbt.putInt("size", t.size)
            for (i in t.indices) {
                elemSerializer.write(nbt.getCompound(i.toString()), t[i])
            }
        }

        override fun read(nbt: CompoundTag): MutableList<T> =
            NBTList(nbt, elemSerializer)
    }

    companion object {
        fun <T> serializer(elemSerializer: NBTSerializer<T>): NBTSerializer<MutableList<T>> =
            Serializer(elemSerializer)
    }
}
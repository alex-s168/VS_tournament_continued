package org.valkyrienskies.tournament.storage

import net.minecraft.nbt.CompoundTag

class NBTList<T>(
    val nbt: CompoundTag,
    val reader: (CompoundTag) -> T,
    val writer: (CompoundTag, T) -> Unit,
): AbstractMutableList<T>() {
    override val size: Int
        get() = nbt.getInt("size")

    override fun get(index: Int): T =
        reader(nbt.getCompound(index.toString()))

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
        writer(nbt.getCompound(index.toString()), element).let {
            element
        }

    override fun add(index: Int, element: T) {
        writer(nbt.getCompound(index.toString()), element)
        if (index == size)
            nbt.putInt("size", size + 1)
    }

}
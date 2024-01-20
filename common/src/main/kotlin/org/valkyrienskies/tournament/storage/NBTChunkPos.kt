package org.valkyrienskies.tournament.storage

import net.minecraft.world.level.ChunkPos

val NBTChunkPos = NBTSerializer.new(
    write = { nbt, i ->
        nbt.putInt("x", i.x)
        nbt.putInt("z", i.z)
    },
    read = { nbt ->
        ChunkPos(nbt.getInt("x"), nbt.getInt("z"))
    }
)
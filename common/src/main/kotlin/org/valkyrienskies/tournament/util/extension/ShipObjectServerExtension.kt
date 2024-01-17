package org.valkyrienskies.tournament.util.extension

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import org.valkyrienskies.core.api.ships.ServerShip

fun ServerShip.delete(level: ServerLevel) {
    val bounds = shipAABB
        ?: return

    for (x in bounds.minX()..bounds.maxX()) {
        for (y in bounds.minY()..bounds.maxY()) {
            for (z in bounds.minZ()..bounds.maxZ()) {
                level.removeBlockEntity(BlockPos(x, y, z))
                level.setBlock(BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 50)
            }
        }
    }
}
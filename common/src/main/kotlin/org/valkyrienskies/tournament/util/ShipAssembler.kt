package org.valkyrienskies.tournament.util

import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet

object ShipAssembler {

    fun findStructure(level: ServerLevel, pos: BlockPos, blacklist: Set<String>) : DenseBlockPosSet {
        val set = level.blockGroup(pos, shouldCancel = { it > 2000 }) {
            !it.isAir && level.registryAccess().registry(Registries.BLOCK).get().getKey(it.block).toString() !in blacklist
        }

        return set.toVsSlow()
    }

}

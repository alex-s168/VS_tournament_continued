package org.valkyrienskies.tournament.util

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.core.impl.datastructures.DenseBlockPosSet
import org.valkyrienskies.mod.common.util.toJOML
import java.util.concurrent.atomic.AtomicInteger

object StructureFinder {

    private fun findStructureLoop(level: ServerLevel, pos: BlockPos, blacklist: Set<String>, set : DenseBlockPosSet, checked : MutableSet<BlockPos>, amount : AtomicInteger) {
        if(amount.get() > 2000)
            return

        if(checked.contains(pos))
            return

        checked.add(pos)

        if(blacklist.contains(level.getBlockState(pos).block.builtInRegistryHolder().key().toString()))
            return

        if(level.getBlockState(pos).isAir)
            return

        set.add(pos.toJOML())

        amount.getAndIncrement()

        findStructureLoop(level, BlockPos(pos.x+1, pos.y, pos.z), blacklist, set, checked, amount)
        findStructureLoop(level, BlockPos(pos.x, pos.y+1, pos.z), blacklist, set, checked, amount)
        findStructureLoop(level, BlockPos(pos.x, pos.y, pos.z+1), blacklist, set, checked, amount)

        findStructureLoop(level, BlockPos(pos.x-1, pos.y, pos.z), blacklist, set, checked, amount)
        findStructureLoop(level, BlockPos(pos.x, pos.y-1, pos.z), blacklist, set, checked, amount)
        findStructureLoop(level, BlockPos(pos.x, pos.y, pos.z-1), blacklist, set, checked, amount)
    }

    fun findStructure(level : ServerLevel, pos : BlockPos, blacklist : Set<String>) : DenseBlockPosSet {
        val set = DenseBlockPosSet()

        findStructureLoop(level, pos, blacklist, set, mutableSetOf(), AtomicInteger(0))

        return set
    }

}
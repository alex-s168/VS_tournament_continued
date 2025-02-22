package org.valkyrienskies.tournament.blocks.explosive

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.FireBlock
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.neighborBlocks
import org.valkyrienskies.tournament.util.getHeat

abstract class AbstractReactiveExplosiveBlock: AbstractExplosiveBlock() {

    override fun wasExploded(level: Level, pos: BlockPos, explosion: Explosion) {
        if (level is ServerLevel)
            ignite(level, pos)
    }

    override fun explodeUsingFlint(level: ServerLevel, pos: BlockPos) =
        true

    private fun checkNeighbors(level: ServerLevel, pos: BlockPos,) {
        val exp = pos.neighborBlocks()
            .flatMap { it.neighborBlocks() + it }
            .any {
                val bs = level.getBlockState(it)
                bs.block is FireBlock || bs.getHeat() > 0
            }

        if (exp) {
            ignite(level, pos)
        }
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)
        if (level is ServerLevel)
            checkNeighbors(level, pos)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        if (level is ServerLevel)
            checkNeighbors(level, pos)
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)
    }

}
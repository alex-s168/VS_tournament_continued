package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import org.valkyrienskies.tournament.blockentity.TargeterBlockEntity

class TargeterBlock : BaseEntityBlock(
    Properties.of(Material.STONE)
        .sound(SoundType.STONE).strength(1.0f, 2.0f)
) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = TargeterBlockEntity(pos, state)


    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.POWER)
        super.createBlockStateDefinition(builder)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level?,
        state: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {

        return BlockEntityTicker { levelB: Level, posB: BlockPos, stateB: BlockState, t: T ->
            TargeterBlockEntity.tick(
                levelB,
                posB,
                stateB,
                t
            )
        }
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)

        if (level as? ServerLevel == null) return

        val signal = level.getBestNeighborSignal(pos)
        level.setBlock(pos, state.setValue(BlockStateProperties.POWER, signal), 2)
    }

}
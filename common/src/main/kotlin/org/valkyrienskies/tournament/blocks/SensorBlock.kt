package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.tournament.blockentity.SensorBlockEntity
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes

class SensorBlock: BaseEntityBlock(
    Properties.of()
        .mapColor(MapColor.STONE)
        .sound(SoundType.STONE)
        .strength(1.0f, 2.0f)
), RedstoneConnectingBlock {

    companion object {
        private val SHAPE =
            RotShapes.box(0.0, 0.0, 1.0, 16.0, 16.0, 16.0)

        private val DIR_SHAPE =
            DirectionalShape.north(SHAPE)
    }

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(BlockStateProperties.POWER, 0)
        )
    }

    override fun getRenderShape(blockState: BlockState): RenderShape =
        RenderShape.MODEL

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape =
        DIR_SHAPE[state.getValue(FACING)]

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(BlockStateProperties.POWER)
        super.createBlockStateDefinition(builder)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        SensorBlockEntity(pos, state)

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        var dir = ctx.nearestLookingDirection.opposite

        if(ctx.player != null && ctx.player!!.isShiftKeyDown)
            dir = dir.opposite
        return defaultBlockState()
            .setValue(FACING, dir)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        val facing = state.getValue(FACING)
        val back = pos.relative(facing.opposite)
        level.neighborChanged(back, this, pos)
        level.updateNeighborsAtExceptFromFacing(back, this, facing)
    }

    override fun isSignalSource(state: BlockState): Boolean =
        true

    override fun getDirectSignal(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int =
        getSignal(state, level, pos, direction)

    override fun getSignal(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        direction: Direction
    ): Int =
        if (direction == state.getValue(FACING))
            state.getValue(BlockStateProperties.POWER)
        else
            0

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> =
        BlockEntityTicker { levelB: Level, posB: BlockPos, stateB: BlockState, t: T ->
            SensorBlockEntity.tick(
                levelB,
                posB,
                stateB,
                t
            )
        }

    override fun canConnectTo(state: BlockState, direction: Direction): Boolean =
        direction == state.getValue(FACING)

}
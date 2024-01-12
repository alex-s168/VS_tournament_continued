package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.ship.SpinnerShipControl
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes

class SpinnerBlock: DirectionalBlock(
    Properties.of(Material.STONE)
        .sound(SoundType.STONE)
        .strength(1.0f, 2.0f)
) {

    val SHAPE = RotShapes.cube()

    val SPINNER_SHAPE = DirectionalShape.north(SHAPE)

    init {
        registerDefaultState(defaultBlockState()
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
        SPINNER_SHAPE[state.getValue(BlockStateProperties.FACING)]

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(BlockStateProperties.POWER)
        super.createBlockStateDefinition(builder)
    }


    private fun getShipControl(level: Level, pos: BlockPos): SpinnerShipControl? =
        ((level.getShipObjectManagingPos(pos)
            ?: level.getShipManagingPos(pos))
                as? ServerShip)?.let { SpinnerShipControl.getOrCreate(it) }


    private fun enableSpinner(level: ServerLevel, pos: BlockPos, state: BlockState) {
        getShipControl(level, pos)?.let {
            it.stopSpinner(pos.toJOML())
            it.addSpinner(
                pos.toJOML(),
                state.getValue(FACING).normal.toJOMLD()
                    .mul(state.getValue(BlockStateProperties.POWER).toDouble())
            )
        }
    }

    private fun disableSpinner(level: ServerLevel, pos: BlockPos) {
        getShipControl(level, pos)?.stopSpinner(pos.toJOML())
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level !is ServerLevel) return

        val signal = level.getBestNeighborSignal(pos)
        if (state.getValue(BlockStateProperties.POWER) != signal) {
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWER, signal))
            return
        }

        if (signal > 0) {
            enableSpinner(level, pos, state)
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level !is ServerLevel) return

        disableSpinner(level, pos)

        super.onRemove(state, level, pos, newState, isMoving)
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

        if (level !is ServerLevel) return

        val signal = level.getBestNeighborSignal(pos)
        val prev = state.getValue(BlockStateProperties.POWER)

        if (signal == prev) return

        disableSpinner(level, pos)

        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWER, signal))
    }

    override fun getStateForPlacement(
        ctx: BlockPlaceContext
    ): BlockState = defaultBlockState()
            .setValue(FACING, ctx.nearestLookingDirection.opposite)

    override fun getBlockSupportShape(
        state: BlockState,
        reader: BlockGetter,
        pos: BlockPos
    ): VoxelShape = RotShapes.cube().makeMcShape()
}
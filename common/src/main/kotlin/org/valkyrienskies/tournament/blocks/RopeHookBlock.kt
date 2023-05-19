package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.tournament.TournamentDebugHelper
import org.valkyrienskies.tournament.api.annotation.WhoCaresAboutDoingItProperly
import org.valkyrienskies.tournament.api.block.DirectionalBaseEntityBlock
import org.valkyrienskies.tournament.api.debug.DebugLine
import org.valkyrienskies.tournament.api.helper.Helper3d
import org.valkyrienskies.tournament.blockentity.RopeHookBlockEntity
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes
import java.awt.Color
import java.lang.Exception
import java.util.*
import kotlin.math.absoluteValue

class RopeHookBlock : DirectionalBaseEntityBlock(
    Properties.of(Material.STONE)
        .sound(SoundType.STONE).strength(1.0f, 2.0f)
) {

    val SHAPE = RotShapes.box(0.25, 0.0, 0.25, 15.75, 16.0, 15.75)
    val ROPEATTACH_SHAPE = DirectionalShape.north(SHAPE)

    init {
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.POWER, 0))
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = RopeHookBlockEntity(pos, state)

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: Random) {
        super.animateTick(state, level, pos, random)
        val be = level.getBlockEntity(pos) as RopeHookBlockEntity
        if (be.otherPos != null && !be.isSecondary && TournamentDebugHelper.exists(be.debugID) ) {
            if (be.maxLen == 0.0) {
                be.maxLen = (Helper3d.MaybeShipToWorldspace(level, be.otherPos!!)
                    .dist(Helper3d.MaybeShipToWorldspace(level, be.mainPos!!))).absoluteValue
            }

            val p1 = Helper3d.MaybeShipToWorldspace(level, Helper3d.VecBlockMid(be.mainPos!!))
            val p2 = Helper3d.MaybeShipToWorldspace(level, Helper3d.VecBlockMid(be.otherPos!!))

            Helper3d.drawQuadraticParticleCurve(p1, p2, be.maxLen, 5.0, level, ParticleTypes.CLOUD)

            if (!TournamentDebugHelper.exists(be.debugID)) {
                TournamentDebugHelper.list()[be.debugID] = DebugLine(be.mainPos!!, be.otherPos!!, Color.RED)
            }
        }
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return ROPEATTACH_SHAPE[state.getValue(BlockStateProperties.FACING)]
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(BlockStateProperties.POWER)
        super.createBlockStateDefinition(builder)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val signal = level.getBestNeighborSignal(pos)
        level.setBlock(pos, state.setValue(BlockStateProperties.POWER, signal), 2)

        if(signal > 0)
            killk(level, pos)
    }

    fun killk(level: ServerLevel, pos: BlockPos) {
        val be = level.getBlockEntity(pos) as RopeHookBlockEntity
        if(be.isSecondary) {
            val t = be.conPos?.let { level.getBlockState(it) }
            try {
                val pbe = (be.conPos?.let { level.getBlockEntity(it) } as RopeHookBlockEntity)
                pbe.ropeId?.let { level.shipObjectWorld.removeConstraint( it ) }
                TournamentDebugHelper.removeObject(pbe.debugID)
                pbe.otherPos = null
                pbe.ropeId = 0
                pbe.debugID = -1
                level.sendBlockUpdated(pbe.blockPos, pbe.blockState, pbe.blockState, Block.UPDATE_ALL_IMMEDIATE)
            } catch (ignored : Exception) {}
            be.conPos?.let { level.removeBlock(it, false) }
            be.conPos?.let { level.setBlock(it, t!!, 2) }
        }
        TournamentDebugHelper.removeObject(be.debugID)
        be.ropeId?.let { level.shipObjectWorld.removeConstraint(it) }
        be.otherPos = null
        be.ropeId = 0
        be.debugID = -1
        level.sendBlockUpdated(be.blockPos, be.blockState, be.blockState, Block.UPDATE_ALL_IMMEDIATE)
    }

    @WhoCaresAboutDoingItProperly
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level.isClientSide) return
        level as ServerLevel

        killk(level, pos)

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

        if (level as? ServerLevel == null) return

        val signal = level.getBestNeighborSignal(pos)
        level.setBlock(pos, state.setValue(BlockStateProperties.POWER, signal), 2)

        if(signal > 0)
            killk(level, pos)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return defaultBlockState()
            .setValue(FACING, ctx.nearestLookingDirection.opposite)
    }
}
package org.valkyrienskies.tournament.blocks

import de.m_marvin.univec.impl.Vec3d
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.tournament.TournamentDebugHelper
import org.valkyrienskies.tournament.api.helper.Helper3d
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes
import java.util.*
import kotlin.math.absoluteValue

class RopeHookBlock : DirectionalBlock(
    Properties.of(Material.STONE)
        .sound(SoundType.STONE).strength(1.0f, 2.0f)
) {

    val SHAPE = RotShapes.box(0.25, 0.0, 0.25, 15.75, 16.0, 15.75)
    val ROPEATTACH_SHAPE = DirectionalShape.north(SHAPE)

    private var ropeId: ConstraintId? = null
    private var MainPos:Vec3d? = null
    private var OtherPos: Vec3d? = null

    private var maxLen: Double = 0.0

    init {
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.POWER, 0))
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: Random) {
        super.animateTick(state, level, pos, random)
        if (OtherPos != null) {
            if(maxLen == 0.0){
                maxLen = (Helper3d.MaybeShipToWorldspace(level, OtherPos!!).dist(Helper3d.MaybeShipToWorldspace(level, MainPos!!))).absoluteValue
            }

            val p1 = Helper3d.VecBlockMid(Helper3d.MaybeShipToWorldspace(level, MainPos!!))
            val p2 = Helper3d.VecBlockMid(Helper3d.MaybeShipToWorldspace(level, OtherPos!!))

            Helper3d.drawQuadraticParticleCurve(p1, p2, maxLen, 5.0, level, ParticleTypes.CLOUD)

            TournamentDebugHelper.updateIDDebugLine(ropeId!!+1, p1, p2, 45)
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

    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        state.setValue(BlockStateProperties.POWER, 0)

        TournamentDebugHelper.removeIDDebugLine(ropeId!!+1)

        // delets any existing ropes
        ropeId?.let { level.shipObjectWorld.removeConstraint(it) }
        ropeId = null
        OtherPos = null
        MainPos = null
        maxLen = 0.0
    }

    // sets the rope for deletion purposes
    fun SetRopeId(rope: ConstraintId, main:Vec3d?, other:Vec3d?) {
        println("Block>> " + rope)

        ropeId = rope
        OtherPos = other
        MainPos = main
        maxLen = 0.0

        TournamentDebugHelper.addTickedIDDebugLine(main!!, other!!, 20, ropeId!!+1)
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

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return defaultBlockState()
            .setValue(FACING, ctx.nearestLookingDirection.opposite)
    }
}
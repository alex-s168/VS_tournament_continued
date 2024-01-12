package org.valkyrienskies.tournament.blocks

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
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
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.mod.common.assembly.createNewShipWithBlocks
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.isChunkInShipyard
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.TournamentTriggers
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes
import org.valkyrienskies.tournament.util.ShipAssembler

class ShipAssemblerBlock : DirectionalBlock (
    Properties.of(Material.STONE)
        .sound(SoundType.STONE).strength(1.0f, 2.0f)
) {

    val SHAPE = RotShapes.cube()

    val Shipifier_SHAPE = DirectionalShape.south(SHAPE)

    init {
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.POWER, 0))
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shipifier_SHAPE[state.getValue(BlockStateProperties.FACING)]
    }

    private fun asm(
        state: BlockState,
        level: ServerLevel,
        pos: BlockPos,
        player: ServerPlayer?
    ) : Boolean {
        val blacklist = TournamentConfig.SERVER.blockBlacklist

        if (level.isChunkInShipyard(pos.x shr 4, pos.z shr 4)) {
            return false
        } else if (!state.isAir) {
            val structure = ShipAssembler.findStructure(level, pos, blacklist)
            println(structure.size)
            val shipData = createNewShipWithBlocks(pos, structure, level)

            player?.let {
                TournamentTriggers.SHIP_ASSEMBLY_TRIGGER.trigger(player, structure.size)
            }

            return true
        }
        return false
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)
        if (level as? ServerLevel == null) return

        val signal = level.getBestNeighborSignal(pos)
        if (signal > 0) {
            asm(state, level, pos, null)
        }
    }

    override fun use(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
        if (level as? ServerLevel == null) return InteractionResult.PASS
        if(level.getShipManagingPos(pos) != null) return InteractionResult.PASS

        if(asm(state, level, pos, player as? ServerPlayer)) {
            player.sendMessage(TextComponent("Assembled ship!"), Util.NIL_UUID)
        } else {
            player.sendMessage(TextComponent("That chunk is already part of a ship!"), Util.NIL_UUID)
        }

        return InteractionResult.SUCCESS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(BlockStateProperties.POWER)
        super.createBlockStateDefinition(builder)
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
        if (signal > 0) {
            asm(state, level, pos, null)
        }
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return defaultBlockState()
            .setValue(FACING, ctx.nearestLookingDirection)
    }

}
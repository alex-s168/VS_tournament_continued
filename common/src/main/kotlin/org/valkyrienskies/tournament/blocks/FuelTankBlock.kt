package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.WorldlyContainerHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.blockentity.FuelTankBlockEntity
import org.valkyrienskies.tournament.util.TitleType
import org.valkyrienskies.tournament.util.block.SlabBaseEntityBlock
import org.valkyrienskies.tournament.util.block.WithExRenderInfo
import org.valkyrienskies.tournament.util.sendTitle
import java.util.BitSet

private fun useCommon(
    state: BlockState,
    level: Level,
    pos: BlockPos,
    player: Player,
    hand: InteractionHand,
    hit: BlockHitResult
): InteractionResult {
    if (level !is ServerLevel)
        return InteractionResult.FAIL

    val be = level.getBlockEntity(pos)
            as? FuelTankBlockEntity
        ?: return InteractionResult.FAIL

    val stack = player.getItemInHand(hand)

    var result = InteractionResult.FAIL

    val canStore = be.canStoreCount(stack)
    if (canStore > 0) {
        be.forceStore(stack, canStore)
        stack.shrink(canStore)
        result = InteractionResult.SUCCESS
    }

    be.ship { mngr ->
        level.sendTitle(
            player,
            TitleType.ACTION_BAR_TEXT,
            TranslatableComponent(
                "misc.vs_tournament.fuel.level",
                mngr.fuelCount,
                mngr.fuelCap
            )
        )
    }

    return result
}

class FuelTankBlockFull(
    val transparent: Boolean
): BaseEntityBlock(
    Properties
        .of(Material.METAL)
        .noOcclusion()
), WorldlyContainerHolder, WithExRenderInfo {

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)
        if (level.isClientSide) return
        val be = level.getBlockEntity(pos) as? FuelTankBlockEntity? ?: return

        be.onAdded()

        updateNeighborTransparent(level, pos)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level.isClientSide) return
        val be = level.getBlockEntity(pos) as? FuelTankBlockEntity? ?: return

        be.onRemoved()

        super.onRemove(state, level, pos, newState, isMoving)
    }

    override fun use(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult)
        = useCommon(state, level, pos, player, hand, hit)

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        FuelTankBlockEntity(pos, state, 1.0f) {
            if (transparent) { TournamentBlockEntities.FUEL_TANK_FULL_TRANSPARENT.get() }
            else { TournamentBlockEntities.FUEL_TANK_FULL_SOLID.get() }
        }

    override fun getContainer(state: BlockState, level: LevelAccessor, pos: BlockPos): WorldlyContainer =
        (level.getBlockEntity(pos) as FuelTankBlockEntity).getContainer()

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = FuelTankBlockEntity.ticker as BlockEntityTicker<T>

    override fun getRenderShape(blockState: BlockState) =
        if (transparent) RenderShape.INVISIBLE else RenderShape.MODEL

    override fun skipRendering(state: BlockState, adjacentBlockState: BlockState, direction: Direction): Boolean {
        if (transparent) return false
        return super.skipRendering(state, adjacentBlockState, direction)
    }

    override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos): Float {
        if (transparent) return 1.0f
        return super.getShadeBrightness(state, level, pos)
    }

    override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean {
        if (transparent) return true
        return super.propagatesSkylightDown(state, level, pos)
    }

    fun updateNeighborTransparent(level: Level, pos: BlockPos) {
        val be = level.getBlockEntity(pos) as? FuelTankBlockEntity ?: return

        if (transparent) {
            val bitSet = BitSet(6)

            Direction.entries.forEachIndexed { index, direction ->
                val p = pos.relative(direction)
                val bs = level.getBlockState(p).block
                if (bs is FuelTankBlockFull && bs.transparent) {
                    bitSet[index] = true
                }
            }

            be.neighborsTransparent = bitSet
            be.update()
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

        updateNeighborTransparent(level, pos)
    }

    override fun getFaceRenderType(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        face: Direction
    ): WithExRenderInfo.FaceRenderType {
        if (!transparent) {
            return WithExRenderInfo.FaceRenderType.NORMAL
        }

        val be = level.getBlockEntity(pos) as? FuelTankBlockEntity
            ?: return WithExRenderInfo.FaceRenderType.NORMAL

        if (be.isNeighborTransparent(face.opposite)) {
            return WithExRenderInfo.FaceRenderType.FORCE_NOT_RENDER
        }

        return WithExRenderInfo.FaceRenderType.FORCE_RENDER
    }

}

class FuelTankBlockHalf: SlabBaseEntityBlock(
    Properties.of(Material.METAL)
), WorldlyContainerHolder {

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)
        if (level.isClientSide) return
        val be = level.getBlockEntity(pos) as? FuelTankBlockEntity? ?: return

        be.onAdded()
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level.isClientSide) return
        val be = level.getBlockEntity(pos) as? FuelTankBlockEntity? ?: return

        be.onRemoved()

        super.onRemove(state, level, pos, newState, isMoving)
    }

    override fun use(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult)
            = useCommon(state, level, pos, player, hand, hit)

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        val isDouble = state.getValue(TYPE) == SlabType.DOUBLE
        val capF = if (isDouble) 1.0f else 0.5f
        return FuelTankBlockEntity(pos, state, capF) {
            TournamentBlockEntities.FUEL_TANK_HALF_SOLID.get()
        }
    }

    override fun getContainer(state: BlockState, level: LevelAccessor, pos: BlockPos): WorldlyContainer =
        (level.getBlockEntity(pos) as FuelTankBlockEntity).getContainer()

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = FuelTankBlockEntity.ticker as BlockEntityTicker<T>

    override fun getRenderShape(blockState: BlockState) =
        RenderShape.MODEL

}
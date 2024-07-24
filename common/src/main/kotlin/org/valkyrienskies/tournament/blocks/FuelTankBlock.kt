package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.WorldlyContainerHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.blockentity.FuelTankBlockEntity
import org.valkyrienskies.tournament.util.TitleType
import org.valkyrienskies.tournament.util.block.SlabBaseEntityBlock
import org.valkyrienskies.tournament.util.sendTitle

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

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        FuelTankBlockEntity(pos, state, 1.0f) {
            TournamentBlockEntities.FUEL_TANK_HALF_SOLID.get()
        }

    override fun getContainer(state: BlockState, level: LevelAccessor, pos: BlockPos): WorldlyContainer =
        (level.getBlockEntity(pos) as FuelTankBlockEntity).getContainer()

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = FuelTankBlockEntity.ticker as BlockEntityTicker<T>

}
package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.WorldlyContainerHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.tournament.blockentity.FuelContainerBlockEntity
import org.valkyrienskies.tournament.storage.ShipFuelStorage
import org.valkyrienskies.tournament.util.getThrusterFuelValue
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class FuelContainerBlock: BaseEntityBlock(
    Properties.of(Material.METAL)
        .sound(SoundType.METAL).strength(1.0f, 2.0f)
), WorldlyContainerHolder {

    private var coolDown = 0

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        if (level.isClientSide) return

        val be = level.getBlockEntity(pos) as? FuelContainerBlockEntity ?: return
        val ship = level.getShipManagingPos(pos)
        ship?.let {
            ship as ServerShip
            ShipFuelStorage.ships.getOrPut(ship) { CopyOnWriteArrayList() }.add(be.amount)
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level.isClientSide) return

        val be = level.getBlockEntity(pos) as? FuelContainerBlockEntity ?: return
        val ship = level.getShipManagingPos(pos)
        ship?.let {
            ship as ServerShip
            ShipFuelStorage.ships[ship]?.remove(be.amount)
        }
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.FAIL
        if (coolDown > 0) {
            coolDown --
            return InteractionResult.FAIL
        }

        val be = level.getBlockEntity(pos) as? FuelContainerBlockEntity ?: return InteractionResult.FAIL

        val stack = player.getItemInHand(hand)

        stack.getThrusterFuelValue()?.let { fuel ->
            if (be.amount.get() + fuel <= be.cap) {
                be.amount.set(be.amount.get() + fuel)

                stack.shrink(1)

                be.sendUpdate()

                coolDown = 1
                return InteractionResult.SUCCESS
            }

            coolDown = 1
            return InteractionResult.FAIL
        }

        player.sendMessage(TranslatableComponent(
            "chat.vs_tournament.fuel_container.amount",
            be.amount.toString(),
            be.cap.toString()
        ), UUID(0, 0))

        coolDown = 1
        return InteractionResult.PASS
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity
        = FuelContainerBlockEntity(pos, state, 1000)

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = BlockEntityTicker { levelB: Level, posB: BlockPos, stateB: BlockState, t: T ->
        FuelContainerBlockEntity.tick(
            levelB,
            posB,
            stateB,
            t
        )
    }

    override fun getContainer(state: BlockState, level: LevelAccessor, pos: BlockPos): WorldlyContainer =
        (level.getBlockEntity(pos) as FuelContainerBlockEntity).getContainer()

}
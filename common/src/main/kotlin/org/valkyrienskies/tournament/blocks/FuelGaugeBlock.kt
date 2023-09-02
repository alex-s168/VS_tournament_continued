package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.tournament.storage.ShipFuelStorage
import java.util.UUID

class FuelGaugeBlock: Block(
    Properties.of(Material.METAL)
) {

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide)
            return InteractionResult.PASS

        val ship = level.getShipManagingPos(pos)

        if (ship == null) {
            player.sendMessage(
                TranslatableComponent("chat.vs_tournament.fuel_gauge.not_on_ship"),
                UUID(0, 0)
            )

            return InteractionResult.SUCCESS
        }

        val fuel = ShipFuelStorage.getFuel(ship as ServerShip)
        player.sendMessage(
            TranslatableComponent("chat.vs_tournament.fuel_gauge.amount", fuel.toString()),
            UUID(0, 0)
        )

        return InteractionResult.SUCCESS
    }

}
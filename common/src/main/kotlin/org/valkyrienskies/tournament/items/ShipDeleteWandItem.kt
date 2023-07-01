package org.valkyrienskies.tournament.items

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.tournament.TournamentItems
import org.valkyrienskies.tournament.api.extension.delete

class ShipDeleteWandItem : Item(
        Properties().stacksTo(1).tab(TournamentItems.TAB)
){

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player
        val blockPosition = context.clickedPos

        if(context.level.isClientSide || player == null) {
            return InteractionResult.PASS
        }

        val level = context.level
        if(level !is ServerLevel){
            return InteractionResult.PASS
        }

        val ship = level.getShipObjectManagingPos(blockPosition) ?: return InteractionResult.PASS

        ship.delete(level)

        return super.useOn(context)
    }
}
package org.valkyrienskies.tournament.items

import net.minecraft.Util
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.tournament.TournamentItems
import org.valkyrienskies.tournament.util.extension.delete

class ShipDeleteWandItem:Item(
        Properties()
            .stacksTo(1)
            .tab(TournamentItems.TAB)
) {

    override fun getRarity(stack: ItemStack) =
        Rarity.EPIC

    override fun useOn(context: UseOnContext): InteractionResult {
        if(context.level.isClientSide || context.player == null) {
            return InteractionResult.PASS
        }

        val level = context.level
        if(level !is ServerLevel){
            return InteractionResult.PASS
        }

        val ship = level.getShipObjectManagingPos(context.clickedPos)
            ?: return InteractionResult.PASS

        ship.delete(level)

        context.player?.sendMessage(TranslatableComponent("chat.vs_tournament.delete_wand.deleted"), Util.NIL_UUID)

        return super.useOn(context)
    }
}
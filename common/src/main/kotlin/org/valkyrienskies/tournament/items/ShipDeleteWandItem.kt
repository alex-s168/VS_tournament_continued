package org.valkyrienskies.tournament.items

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.tournament.util.extension.delete

class ShipDeleteWandItem : Item(
    Properties().stacksTo(1)
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

        context.player?.sendSystemMessage(Component.translatable("chat.vs_tournament.delete_wand.deleted"))

        return super.useOn(context)
    }
}
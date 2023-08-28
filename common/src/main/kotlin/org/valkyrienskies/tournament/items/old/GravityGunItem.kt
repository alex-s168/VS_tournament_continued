package org.valkyrienskies.tournament.items.old

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.valkyrienskies.tournament.TournamentItems
import org.valkyrienskies.tournament.items.GiftBagItem


class GravityGunItem : Item(
    Properties()
        .stacksTo(1)
        .tab(TournamentItems.TAB)
        .rarity(Rarity.RARE)
) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = GiftBagItem.of(
            "vs_tournament:gifts/grab_gun",
            "tooltip.vs_tournament.gift.grab_gun",
            Rarity.RARE
        )

        if (!player.inventory.add(stack)) {
            player.drop(stack, false)
        }

        return InteractionResultHolder.consume(player.getItemInHand(usedHand))
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        tooltipComponents.add(TranslatableComponent("tooltip.vs_tournament.old_item"))
    }

}
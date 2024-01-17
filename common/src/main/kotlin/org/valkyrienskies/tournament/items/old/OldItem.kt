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
import org.valkyrienskies.tournament.items.GiftBagItem

class OldItem(
    val name: String
): Item(
    Properties()
        .stacksTo(1)
        .rarity(Rarity.RARE)
) {

    override fun getDescriptionId(): String =
        "item.vs_tournament.old_item"

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val thisStack = player.getItemInHand(usedHand)

        val stack = GiftBagItem.of(
            "vs_tournament:gifts/$name",
            "tooltip.vs_tournament.gift.$name",
            Rarity.RARE
        )
        stack.count = thisStack.count

        if (!player.inventory.add(stack))
            player.drop(stack, false)

        thisStack.count = 0
        return InteractionResultHolder.consume(thisStack)
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
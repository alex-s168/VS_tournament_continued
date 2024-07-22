package org.valkyrienskies.tournament

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.valkyrienskies.core.impl.util.events.EventEmitterImpl

object TournamentEvents {
    val itemHoverText = EventEmitterImpl<ItemHoverText>()

    data class ItemHoverText(
        val stack: ItemStack,
        val level: Level?,
        val tooltipComponents: MutableList<Component>,
        val isAdvanced: TooltipFlag
    )
}
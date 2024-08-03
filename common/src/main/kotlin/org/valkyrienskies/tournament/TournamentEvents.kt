package org.valkyrienskies.tournament

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.BiomeGenerationSettings
import org.valkyrienskies.core.impl.util.events.EventEmitterImpl

object TournamentEvents {
    val itemHoverText = EventEmitterImpl<ItemHoverText>()
    val clientTick = EventEmitterImpl<Unit>()

    object WorldGenFeatures {
        val defaultOres = EventEmitterImpl<BiomeGenerationSettings.Builder>()
    }

    data class ItemHoverText(
        val stack: ItemStack,
        val level: Level?,
        val tooltipComponents: MutableList<Component>,
        val isAdvanced: TooltipFlag
    )
}
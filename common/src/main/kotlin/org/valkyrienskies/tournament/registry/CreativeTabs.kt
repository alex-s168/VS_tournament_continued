package org.valkyrienskies.tournament.registry

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.tournament.services.TournamentPlatformHelper

class CreativeTabs {
    companion object {
        fun create(id: ResourceLocation, stack: () -> ItemStack): CreativeModeTab {
            return TournamentPlatformHelper
                .get()
                .createCreativeTab(id, stack)
        }
    }
}
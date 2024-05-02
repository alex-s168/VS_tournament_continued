package org.valkyrienskies.tournament.registry

import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.tournament.TournamentBlocks
import org.valkyrienskies.tournament.TournamentItems

object CreativeTabs {
    fun create(): CreativeModeTab {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.vs_tournament.main_tab"))
            .icon { ItemStack(TournamentBlocks.SHIP_ASSEMBLER.get().asItem()) }
            .displayItems { _, output ->
                TournamentItems.ITEMS.forEach {
                    output.accept(it.get())
                }
            }
            .build()
    }
}
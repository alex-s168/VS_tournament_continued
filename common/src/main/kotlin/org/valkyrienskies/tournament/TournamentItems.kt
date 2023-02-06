package org.valkyrienskies.Tournament

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.Tournament.registry.CreativeTabs
import org.valkyrienskies.Tournament.registry.DeferredRegister

@Suppress("unused")
object TournamentItems {
    private val ITEMS = DeferredRegister.create(TournamentMod.MOD_ID, Registry.ITEM_REGISTRY)
    val TAB: CreativeModeTab = CreativeTabs.create(
        ResourceLocation(
            TournamentMod.MOD_ID,
            "Tournament_tab"
        )
    ) { ItemStack(TournamentBlocks.OAK_SHIP_HELM.get()) }

    fun register() {
        TournamentBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}

package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks
import org.valkyrienskies.tournament.items.*
import org.valkyrienskies.tournament.registry.CreativeTabs
import org.valkyrienskies.tournament.registry.DeferredRegister

@Suppress("unused")
object TournamentItems {
    private val ITEMS = DeferredRegister.create(TournamentMod.MOD_ID, Registry.ITEM_REGISTRY)

    val ROPE                    = ITEMS.register("rope", ::RopeItem)
    val TOOL_PULSEGUN           = ITEMS.register("pulse_gun", ::PulseGunItem)
    val TOOL_DELETEWAND         = ITEMS.register("delete_wand", ::ShipDeleteWandItem)
    val TOOL_GRABGUN            = ITEMS.register("grab_gun", ::GrabGunItem)
    val UPGRADE_THRUSTER        = ITEMS.register("upgrade_thruster", ::ThrusterUpgradeItem)

    val TAB: CreativeModeTab = CreativeTabs.create(
        ResourceLocation(
            TournamentMod.MOD_ID,
            "tournament_tab"
        )
    ) { ItemStack(Blocks.IRON_BLOCK) }

    fun register() {
        TournamentBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}

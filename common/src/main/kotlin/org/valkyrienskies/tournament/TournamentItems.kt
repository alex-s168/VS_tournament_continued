package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import org.valkyrienskies.tournament.items.*
import org.valkyrienskies.tournament.registry.DeferredRegister
import org.valkyrienskies.tournament.registry.RegistrySupplier

@Suppress("unused")
object TournamentItems {
    private val ITEMS = DeferredRegister.create(TournamentMod.MOD_ID, Registry.ITEM_REGISTRY)

    lateinit var ROPE              :  RegistrySupplier<RopeItem>
    lateinit var TOOL_PULSEGUN     :  RegistrySupplier<PulseGunItem>
    lateinit var TOOL_DELETEWAND   :  RegistrySupplier<ShipDeleteWandItem>
    lateinit var UPGRADE_THRUSTER  :  RegistrySupplier<Item>

    lateinit var TAB: CreativeModeTab

    fun register() {
        ROPE                    = ITEMS.register("rope", ::RopeItem)
        TOOL_PULSEGUN           = ITEMS.register("pulse_gun", ::PulseGunItem)
        TOOL_DELETEWAND         = ITEMS.register("delete_wand", ::ShipDeleteWandItem)
        UPGRADE_THRUSTER        = ITEMS.register("upgrade_thruster") {
            Item(Item.Properties().stacksTo(16).tab(TAB))
        }

        TournamentBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

}

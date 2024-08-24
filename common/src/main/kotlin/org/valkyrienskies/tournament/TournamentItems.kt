package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import org.valkyrienskies.tournament.items.*
import org.valkyrienskies.tournament.items.old.OldItem
import org.valkyrienskies.tournament.items.old.UpdateItem
import org.valkyrienskies.tournament.registry.DeferredRegister
import org.valkyrienskies.tournament.registry.RegistrySupplier


@Suppress("unused")
object TournamentItems {
    private val ITEMS = DeferredRegister.create(TournamentMod.MOD_ID, Registry.ITEM_REGISTRY)
    val fuelItems = mutableListOf<Pair<String, FuelType>>()

    lateinit var ROPE              :  RegistrySupplier<RopeItem>
    lateinit var TOOL_PULSEGUN     :  RegistrySupplier<PulseGunItem>
    lateinit var TOOL_DELETEWAND   :  RegistrySupplier<ShipDeleteWandItem>
    lateinit var UPGRADE_THRUSTER  :  RegistrySupplier<Item>
    lateinit var GIFT_BAG          :  RegistrySupplier<GiftBagItem>

    lateinit var TAB: CreativeModeTab

    fun register() {
        ROPE                    = ITEMS.register("rope", ::RopeItem)
        TOOL_PULSEGUN           = ITEMS.register("pulse_gun", ::PulseGunItem)
        TOOL_DELETEWAND         = ITEMS.register("delete_wand", ::ShipDeleteWandItem)
        UPGRADE_THRUSTER        = ITEMS.register("upgrade_thruster") {
            Item(Properties().stacksTo(16).tab(TAB))
        }
        GIFT_BAG                = ITEMS.register("gift_bag", ::GiftBagItem)
        ITEMS.register("iron_cube") {
            Item(Properties().stacksTo(64).tab(TAB))
        }
        ITEMS.register("coal_dust") {
            Item(Properties().stacksTo(64).tab(TAB))
        }
        ITEMS.register("hammer") {
            Item(Properties().stacksTo(8).tab(TAB))
        }

        ITEMS.register("raw_phynite") {
            Item(Properties().stacksTo(64).tab(TAB))
        }

        ITEMS.register("ingot_phynite") {
            Item(Properties().stacksTo(64).tab(TAB))
        }

        ITEMS.register("physics_shard") {
            Item(Properties().stacksTo(64).tab(TAB))
        }

        fuelItems.forEach { ITEMS.register(it.first) { Item(Properties().stacksTo(64).tab(TAB)) } }

        // old:
        ITEMS.register("grab_gun") { OldItem("grab_gun") }
        ITEMS.register("delete_gun") { UpdateItem(TOOL_DELETEWAND.get()) }


        TournamentBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

}

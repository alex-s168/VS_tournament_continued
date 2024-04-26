package org.valkyrienskies.tournament

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import org.valkyrienskies.tournament.items.GiftBagItem
import org.valkyrienskies.tournament.items.PulseGunItem
import org.valkyrienskies.tournament.items.RopeItem
import org.valkyrienskies.tournament.items.ShipDeleteWandItem
import org.valkyrienskies.tournament.items.old.OldItem
import org.valkyrienskies.tournament.items.old.UpdateItem
import org.valkyrienskies.tournament.registry.DeferredRegister
import org.valkyrienskies.tournament.registry.RegistrySupplier

@Suppress("unused")
object TournamentItems {
    val ITEMS = DeferredRegister.create(TournamentMod.MOD_ID, Registries.ITEM)

    lateinit var ROPE              :  RegistrySupplier<RopeItem>
    lateinit var TOOL_PULSEGUN     :  RegistrySupplier<PulseGunItem>
    lateinit var TOOL_DELETEWAND   : RegistrySupplier<ShipDeleteWandItem>
    lateinit var UPGRADE_THRUSTER  :  RegistrySupplier<Item>
    lateinit var GIFT_BAG          :  RegistrySupplier<GiftBagItem>

    val TAB: ResourceKey<CreativeModeTab> =
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation(TournamentMod.MOD_ID, "main_tab"))

    fun register() {
        ROPE                    = ITEMS.register("rope", ::RopeItem)
        TOOL_PULSEGUN           = ITEMS.register("pulse_gun", ::PulseGunItem)
        TOOL_DELETEWAND         = ITEMS.register("delete_wand", ::ShipDeleteWandItem)
        UPGRADE_THRUSTER        = ITEMS.register("upgrade_thruster") {
            Item(Item.Properties().stacksTo(16))
        }
        GIFT_BAG                = ITEMS.register("gift_bag", ::GiftBagItem)
        ITEMS.register("iron_cube") {
            Item(Item.Properties().stacksTo(64))
        }
        ITEMS.register("coal_dust") {
            Item(Item.Properties().stacksTo(64))
        }

        // old:
        ITEMS.register("grab_gun") { OldItem("grab_gun") }
        ITEMS.register("delete_gun") { UpdateItem(TOOL_DELETEWAND.get()) }


        TournamentBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

}

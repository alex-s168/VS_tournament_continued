package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.*
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.crafting.Ingredient
import org.valkyrienskies.tournament.items.GiftBagItem
import org.valkyrienskies.tournament.items.PulseGunItem
import org.valkyrienskies.tournament.items.RopeItem
import org.valkyrienskies.tournament.items.ShipDeleteWandItem
import org.valkyrienskies.tournament.items.old.OldItem
import org.valkyrienskies.tournament.items.old.UpdateItem
import org.valkyrienskies.tournament.registry.DeferredRegister
import org.valkyrienskies.tournament.registry.RegistrySupplier
import org.valkyrienskies.tournament.util.ArmorMaterialD
import org.valkyrienskies.tournament.util.TierD


@Suppress("unused")
object TournamentItems {
    private val ITEMS = DeferredRegister.create(TournamentMod.MOD_ID, Registry.ITEM_REGISTRY)
    val fuelItems = mutableListOf<Pair<String, FuelType>>()

    lateinit var ROPE              :  RegistrySupplier<RopeItem>
    lateinit var TOOL_PULSEGUN     :  RegistrySupplier<PulseGunItem>
    lateinit var TOOL_DELETEWAND   :  RegistrySupplier<ShipDeleteWandItem>
    lateinit var UPGRADE_THRUSTER  :  RegistrySupplier<Item>
    lateinit var GIFT_BAG          :  RegistrySupplier<GiftBagItem>
    lateinit var INGOT_PHYNITE     :  RegistrySupplier<Item>
    lateinit var PHYGOLD_SWORD     :  RegistrySupplier<SwordItem>
    lateinit var STEEL_INGOT       :  RegistrySupplier<Item>
    lateinit var WOOL_SHEET        :  RegistrySupplier<Item>
    lateinit var UNSTABLE_LAPIS    :  RegistrySupplier<Item>

    lateinit var TAB: CreativeModeTab

    lateinit var TIER_PHYGOLD      : Tier
    lateinit var TIER_STEEL        : Tier
    lateinit var ARMOR_STEEL       : ArmorMaterial

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

        INGOT_PHYNITE = ITEMS.register("ingot_phynite") {
            Item(Properties().stacksTo(64).tab(TAB))
        }

        ITEMS.register("physics_shard") {
            Item(Properties().stacksTo(64).tab(TAB))
        }

        TIER_PHYGOLD = TierD(3, 1561, 8.0f, 3.5f, 20, Ingredient.of(INGOT_PHYNITE.get()))

        PHYGOLD_SWORD = ITEMS.register("phygold_sword") {
            SwordItem(TIER_PHYGOLD, 3, -2.4f, Properties().tab(TAB))
        }

        STEEL_INGOT = ITEMS.register("steel_ingot") {
            Item(Properties().stacksTo(64).tab(TAB))
        }

        ARMOR_STEEL = ArmorMaterialD(
            "steel",
            15,
            listOf(3, 6, 7, 3),
            7,
            SoundEvents.ARMOR_EQUIP_IRON,
            0f,
            0f
        ) { Ingredient.of(STEEL_INGOT.get()) }

        ITEMS.register("steel_helmet") {
            ArmorItem(ARMOR_STEEL, EquipmentSlot.HEAD, Properties().tab(TAB))
        }

        ITEMS.register("steel_chestplate") {
            ArmorItem(ARMOR_STEEL, EquipmentSlot.CHEST, Properties().tab(TAB))
        }

        ITEMS.register("steel_leggings") {
            ArmorItem(ARMOR_STEEL, EquipmentSlot.LEGS, Properties().tab(TAB))
        }

        ITEMS.register("steel_boots") {
            ArmorItem(ARMOR_STEEL, EquipmentSlot.FEET, Properties().tab(TAB))
        }

        TIER_STEEL = TierD(3, 1561, 8.0f, 3.2f, 7, Ingredient.of(STEEL_INGOT.get()))

        ITEMS.register("steel_axe") {
            AxeItem(TIER_STEEL, 7.0F, -3.1F, Properties().tab(TAB))
        }

        ITEMS.register("steel_pickaxe") {
            PickaxeItem(TIER_STEEL, 1, -2.8F, Properties().tab(TAB))
        }

        ITEMS.register("steel_shovel") {
            ShovelItem(TIER_STEEL, 1.5f, -3.0F, Properties().tab(TAB))
        }

        ITEMS.register("steel_sword") {
            SwordItem(TIER_STEEL, 3, -2.4f, Properties().tab(TAB))
        }

        ITEMS.register("steel_gear") {
            Item(Properties().stacksTo(64).tab(TAB))
        }

        WOOL_SHEET = ITEMS.register("wool_sheet") {
            Item(Properties().stacksTo(64).tab(TAB))
        }

        ITEMS.register("wool_wing") {
            Item(Properties().stacksTo(64).tab(TAB))
        }

        ITEMS.register("iron_tip") {
            Item(Properties().stacksTo(64).tab(TAB))
        }

        UNSTABLE_LAPIS = ITEMS.register("unstable_lapis") {
            Item(Properties().stacksTo(64).tab(TAB))
        }

        ITEMS.register("unstable_powder") {
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

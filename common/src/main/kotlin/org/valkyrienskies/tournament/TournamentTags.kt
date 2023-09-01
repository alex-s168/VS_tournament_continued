package org.valkyrienskies.tournament
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object TournamentTags {

    val THRUSTER_FUEL_POOR: TagKey<Item> = TagKey.create(
        Registry.ITEM_REGISTRY,
        ResourceLocation(TournamentMod.MOD_ID, "thruster_fuel_poor")
    )

    val THRUSTER_FUEL_GOOD: TagKey<Item> = TagKey.create(
        Registry.ITEM_REGISTRY,
        ResourceLocation(TournamentMod.MOD_ID, "thruster_fuel_good")
    )

    val THRUSTER_FUEL_RICH: TagKey<Item> = TagKey.create(
        Registry.ITEM_REGISTRY,
        ResourceLocation(TournamentMod.MOD_ID, "thruster_fuel_rich")
    )

}
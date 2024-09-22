package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType

object Recipes {

    private fun <T: Recipe<*>> register(name: String) =
        Registry.register(Registry.RECIPE_TYPE, ResourceLocation(TournamentMod.MOD_ID, name), object : RecipeType<T?> {
            override fun toString(): String {
                return "vs_tournament:$name"
            }
        }) as RecipeType<*>

}
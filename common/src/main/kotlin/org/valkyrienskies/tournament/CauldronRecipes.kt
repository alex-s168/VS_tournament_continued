package org.valkyrienskies.tournament

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object CauldronRecipes {
    data class Env(var heat: Int)

    fun MutableMap<Item, Int>.craft(need: Map<Item, Int>): Boolean {
        if (need.all { this[it.key]?.let { v -> v >= it.value } == true }) {
            need.forEach { (k, v) ->
                this.compute(k) { _, old -> old!! - v }
            }
            return true
        }
        return false
    }

    @JvmStatic
    fun craftOnce(main: ItemStack, sources: MutableMap<Item, Int>, output: (ItemStack) -> Unit, env: Env): Boolean {
        if (main.item == Items.GOLDEN_SWORD && env.heat >= 6 && sources.craft(mapOf(TournamentItems.INGOT_PHYNITE.get() to 4))) {
            main.count --
            output(ItemStack(TournamentItems.PHYGOLD_SWORD.get(), 1))
            return true
        }

        if (main.item == Items.IRON_INGOT && env.heat >= 8 && sources.craft(mapOf(Items.COAL to 2))) {
            main.count --
            output(ItemStack(TournamentItems.STEEL_INGOT.get(), 1))
            return true
        }

        return false
    }
}
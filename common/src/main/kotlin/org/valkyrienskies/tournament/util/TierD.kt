package org.valkyrienskies.tournament.util

import net.minecraft.world.item.Tier
import net.minecraft.world.item.crafting.Ingredient

data class TierD(
    private val level: Int,
    private val uses: Int,
    private val speed: Float,
    private val attackDamageBonus: Float,
    private val enchantmentValue: Int,
    private val repairIngredient: Ingredient,
): Tier {
    override fun getUses(): Int =
        uses

    override fun getSpeed(): Float =
        speed

    override fun getAttackDamageBonus(): Float =
        attackDamageBonus

    override fun getLevel(): Int =
        level

    override fun getEnchantmentValue(): Int =
        enchantmentValue

    override fun getRepairIngredient(): Ingredient =
        repairIngredient
}
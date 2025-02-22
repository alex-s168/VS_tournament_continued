package org.valkyrienskies.tournament.util

import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.crafting.Ingredient

data class ArmorMaterialD(
    private val name: String,
    private val durabilityMultiplier: Int,
    private val slotProtections: List<Int>,
    private val enchantmentValue: Int,
    private val soundEvent: SoundEvent,
    private val toughness: Float,
    private val knockbackResistance: Float,
    private val repairProv: () -> Ingredient,
): ArmorMaterial {
    init {
        require(slotProtections.size == 4)
    }

    private val HEALTH_PER_SLOT: IntArray = intArrayOf(13, 15, 16, 11)

    override fun getDurabilityForSlot(slot: EquipmentSlot): Int =
        HEALTH_PER_SLOT[slot.index] * durabilityMultiplier

    override fun getDefenseForSlot(slot: EquipmentSlot): Int =
        slotProtections[slot.index]

    override fun getEnchantmentValue(): Int =
        enchantmentValue

    override fun getEquipSound(): SoundEvent =
        soundEvent

    private val repair by lazy(repairProv)

    override fun getRepairIngredient(): Ingredient =
        repair

    override fun getName(): String =
        name

    override fun getToughness(): Float =
        toughness

    override fun getKnockbackResistance(): Float =
        knockbackResistance

}

package org.valkyrienskies.tournament.items.old

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class UpdateItem(
    val new: Item
): Item(Properties()) {

    override fun getDescriptionId(): String =
        "item.vs_tournament.old_item"

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (level !is ServerLevel) return
        if (entity !is Player) return

        val am = stack.count
        stack.shrink(am)

        entity.inventory.add(ItemStack(new, am))
    }

}
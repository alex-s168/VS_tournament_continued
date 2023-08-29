package org.valkyrienskies.tournament.items.old

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class UpdateItem(val new: Item) : Item(
    Properties().tab(CreativeModeTab.TAB_SEARCH)
) {
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (entity as? Player == null) return
        if (level as? ServerLevel == null) return

        val am = stack.count
        stack.shrink(am)

        (entity).inventory.add(ItemStack(new, am))
    }
}
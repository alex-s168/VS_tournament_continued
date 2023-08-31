package org.valkyrienskies.tournament.blocks

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.storage.loot.LootContext

class OldBlock(
    val new: Block
): Block (
    Properties.of(Material.MOSS)
) {

    override fun getDrops(state: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        return mutableListOf(ItemStack(new))
    }

    override fun getDescriptionId(): String
        = "description.vs_tournament.old_block"

}
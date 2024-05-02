package org.valkyrienskies.tournament.blocks

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.storage.loot.LootParams

class OldBlock(
    val new: Block
): Block (
    Properties.of()
        .mapColor(MapColor.DIRT)
) {

    override fun getDrops(state: BlockState, builder: LootParams.Builder): MutableList<ItemStack> {
        return mutableListOf(ItemStack(new))
    }

    override fun getDescriptionId(): String
        = "description.vs_tournament.old_block"
}
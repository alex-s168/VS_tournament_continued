package org.valkyrienskies.tournament.blocks


import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.Material

open class FloaterBlock : Block(
    Properties.of(Material.WOOD)
        .sound(SoundType.WOOD).strength(1.0f, 2.0f)
) {

}
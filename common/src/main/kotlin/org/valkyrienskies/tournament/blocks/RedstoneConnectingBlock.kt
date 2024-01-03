package org.valkyrienskies.tournament.blocks

import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState

interface RedstoneConnectingBlock {
    fun canConnectTo(state: BlockState, direction: Direction): Boolean
}
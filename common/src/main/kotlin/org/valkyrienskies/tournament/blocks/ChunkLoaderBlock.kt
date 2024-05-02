package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.tournament.blockentity.ChunkLoaderBlockEntity
import org.valkyrienskies.tournament.util.block.GlassBaseEntityBlock

class ChunkLoaderBlock: GlassBaseEntityBlock(
    Properties.of()
        .mapColor(MapColor.METAL)
) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        ChunkLoaderBlockEntity(pos, state)

    @Suppress("UNCHECKED_CAST")
    override fun <T: BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> =
        ChunkLoaderBlockEntity.ticker as BlockEntityTicker<T>

    @Deprecated("Deprecated in Java")
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level is ServerLevel) {
            val be = level.getBlockEntity(pos) as? ChunkLoaderBlockEntity
                ?: return

            be.ticket?.dispose()
        }

        super.onRemove(state, level, pos, newState, isMoving)
    }
}
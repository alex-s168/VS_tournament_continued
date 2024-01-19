package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3d
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.chunk.ChunkLoader
import org.valkyrienskies.tournament.chunk.ChunkLoaderManager
import org.valkyrienskies.tournament.chunk.ChunkLoadingTicket

class ChunkLoaderBlockEntity(pos: BlockPos, state: BlockState):
    BlockEntity(TournamentBlockEntities.CHUNK_LOADER.get(), pos, state),
    ChunkLoader
{

    internal var ticket: ChunkLoadingTicket? = null

    fun tick(level: ServerLevel) {
        if (ticket == null) {
            val manager = ChunkLoaderManager.getFor(level)
            ticket = manager.allocate(this, 200)
        }
    }

    private fun getCurrPos() =
        level
            ?.getShipObjectManagingPos(blockPos)
            ?.shipToWorld
            ?.transformPosition(blockPos.toJOMLD())

    override fun getCurrentChunk(): ChunkPos =
        getCurrPos()
            ?.let {
                ChunkPos(it.x.toInt() shr 4, it.z.toInt() shr 4)
            } ?: ChunkPos(blockPos.x shr 4, blockPos.z shr 4)

    override fun getFutureChunk(): ChunkPos =
        level
            ?.getShipObjectManagingPos(blockPos)
            ?.velocity
            ?.mul(3.0, Vector3d())
            ?.add(getCurrPos())
            ?.let {
                ChunkPos(it.x.toInt() shr 4, it.z.toInt() shr 4)
            } ?: ChunkPos(blockPos.x shr 4, blockPos.z shr 4)

    companion object {
        val ticker = BlockEntityTicker<ChunkLoaderBlockEntity> { level, _, _, be ->
            if(level !is ServerLevel)
                return@BlockEntityTicker

            be.tick(level)
        }
    }
}
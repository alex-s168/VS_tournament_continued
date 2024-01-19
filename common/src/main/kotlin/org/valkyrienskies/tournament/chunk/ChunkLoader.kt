package org.valkyrienskies.tournament.chunk

import net.minecraft.world.level.ChunkPos

/**
 * An element that can load chunks.
 * To make it load chunks, get the ChunkLoaderManager of the level and request a ChunkLoadingTicket.
 * When it doesn't need to load chunks anymore, simply call `dispose` on the ChunkLoadingTicket.
 */
interface ChunkLoader {
    /**
     * The chunk where it is currently
     */
    fun getCurrentChunk(): ChunkPos

    /**
     * The predicted chunk where it will be in the next (few) tick(s)
     */
    fun getFutureChunk(): ChunkPos
}
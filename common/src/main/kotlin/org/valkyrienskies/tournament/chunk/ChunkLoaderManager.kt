package org.valkyrienskies.tournament.chunk

import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.util.extension.itTake
import java.util.function.Consumer

class ChunkLoaderManager private constructor(
    val level: ServerLevel
) {
    internal val tickets = mutableListOf<ChunkLoadingTicket>()

    private var sorted = true

    fun tick(
        amountOfTickets: Int,
        amountOfChunksPerTicket: Int,
        loader: Consumer<ChunkPos>
    ) {
        if (!sorted) {
            tickets.sortBy {
                it.priority
            }
            sorted = true
        }
        tickets.itTake(amountOfTickets).forEach { ticket ->
            val start = ticket.loader.getCurrentChunk().toJOML()
            val end = ticket.loader.getFutureChunk().toJOML()

            val dist = start.gridDistance(end)
        }
    }

    fun allocate(loader: ChunkLoader, priority: Int) =
        ChunkLoadingTicket(this, loader, priority).also {
            tickets += it
            if (tickets.size > 1) {
                sorted = false
            }
        }

    companion object {
        internal val map =
            HashMap<ResourceKey<Level>, ChunkLoaderManager>()

        fun getFor(level: ServerLevel): ChunkLoaderManager {
            val dim = level.dimension()
            if (dim in map)
                return map[dim]!!

            val lm = ChunkLoaderManager(level)
            map[dim] = lm
            return lm
        }
    }
}
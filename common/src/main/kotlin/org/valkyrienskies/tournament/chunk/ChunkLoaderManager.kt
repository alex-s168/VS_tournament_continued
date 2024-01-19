package org.valkyrienskies.tournament.chunk

import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ChunkHolder
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.ChunkStatus
import org.apache.commons.lang3.mutable.MutableInt
import org.joml.Vector2i
import org.joml.primitives.Rectanglei
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.TickScheduler
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.util.extension.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

// TODO: make multiple blocks of tickets that cycle trough every tick so that not too many chunks are tried to be loaded at once
class ChunkLoaderManager private constructor(
    val level: ServerLevel
) {
    internal val tickets = mutableListOf<ChunkLoadingTicket>()

    private var sorted = true

    fun tick(
        amountOfTickets: Int? = null,
        amountOfChunksPerTicket: Int,
        amountOfActualChunksPerTicket: Int,
        loader: Consumer<ChunkPos>
    ) {
        if (!sorted) {
            tickets.sortBy {
                it.priority
            }
            sorted = true
        }

        val am = amountOfTickets ?: tickets.size

        if (tickets.size > am) {
            throw Exception("[Tournament] Too many chunk-loading tickets in dimension ${level.dimension()}: ${tickets.size} > $am!")
        }

        tickets.itTake(am).forEach { ticket ->
            val mid = ticket.loader.getCurrentChunk().toJOML()
            val top = ticket.loader.getFutureChunk().toJOML()
            val bottom = top.sub(mid, Vector2i()).negate().add(mid)

            val box = Rectanglei(
                top,
                bottom
            ).fix()

            val t = box.area().toFloat() / amountOfChunksPerTicket

            box .scaleFrom(factor = t, center = mid)
                .values()
                .sortedBy { it.distanceSquared(mid) }
                .take(amountOfActualChunksPerTicket)
                .map { ChunkPos(it.x, it.y) }
                .filter { !processing.containsKey(it) }
                .forEach(loader::accept)
        }
    }

    fun allocate(loader: ChunkLoader, priority: Int): ChunkLoadingTicket {
        val i = tickets.indexOfFirst { it.loader == loader }
        if (i != -1) {
            val ticket = tickets[i]
            ticket.priority = priority
            return ticket
        }
        return ChunkLoadingTicket(this, loader, priority).also {
            tickets += it
            if (tickets.size > 1) {
                sorted = false
            }
        }
    }

    private val processing = ConcurrentHashMap<ChunkPos, MutableInt>(
        TournamentConfig.SERVER.chunksPerTicket * 5
    )

    companion object {
        private val map =
            HashMap<ResourceKey<Level>, ChunkLoaderManager>()

        fun getFor(level: ServerLevel): ChunkLoaderManager {
            val dim = level.dimension()
            if (dim in map)
                return map[dim]!!

            val lm = ChunkLoaderManager(level)
            map[dim] = lm

            if (!tickTaskSet) {
                tickTaskSet = true
                TickScheduler.serverTickPerm(tickTask)
            }

            return lm
        }

        private var tickTaskSet = false

        private val tickTask: (MinecraftServer) -> Unit = { server ->
            val cpt = TournamentConfig.SERVER.chunkTicketsPerTick.let {
                if (it == -1) null else it
            }
            server.allLevels.forEach { level ->
                val manager = getFor(level)
                manager.processing.forEach { (k, v) ->
                    val t = v.getAndAdd(1)
                    if (t >= TournamentConfig.SERVER.chunkLoadTimeout * 2) {
                        throw Exception("[Tournament] Chunk loading timed out: still not finished after $t ticks ($k)!")
                    }
                    if (t >= TournamentConfig.SERVER.chunkLoadTimeout) {
                        println("[Tournament] Chunk loading taking longer than expected: took $t ticks to load chunk at $k!")
                    }
                }
                manager.tick(
                    amountOfTickets = cpt,
                    amountOfChunksPerTicket = TournamentConfig.SERVER.chunksPerTicket * 10,
                    amountOfActualChunksPerTicket = TournamentConfig.SERVER.chunksPerTicket,
                    loader = { pos ->
                        manager.processing[pos] = MutableInt(0)
                        level.chunkSource.chunkMap.schedule(
                            ChunkHolder(pos, 0, level, level.lightEngine, null, null),
                            ChunkStatus.EMPTY               // load chunks
                        ).thenRun {
                            manager.processing.remove(pos)
                        }
                    }
                )
            }
        }
    }
}
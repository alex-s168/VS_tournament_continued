package org.valkyrienskies.tournament

import net.minecraft.server.MinecraftServer
import org.valkyrienskies.tournament.util.extension.with
import java.util.concurrent.ConcurrentHashMap

object TickScheduler {
    private val serverTickTemp = ConcurrentHashMap.newKeySet<Ticking>()
    private val serverTickPerm = ConcurrentHashMap.newKeySet<Ticking>()

    fun serverTickTemp(f: (MinecraftServer) -> Unit): Ticking {
        val t = Ticking(f, false)
        serverTickTemp += t
        return t
    }

    fun serverTickPerm(f: (MinecraftServer) -> Unit): Ticking {
        val t = Ticking(f, true)
        serverTickPerm += t
        return t
    }

    fun tickServer(server: MinecraftServer) {
        val toRemove = mutableSetOf<Ticking>()
        serverTickTemp.with(serverTickPerm).forEach { t ->
            if (!t.active) {
                toRemove += t
                return@forEach
            }
            t.f(server)
        }
        serverTickTemp.clear()
        serverTickPerm.removeAll(toRemove)
    }

    data class Ticking(
        val f: (MinecraftServer) -> Unit,
        val active: Boolean
    )
}
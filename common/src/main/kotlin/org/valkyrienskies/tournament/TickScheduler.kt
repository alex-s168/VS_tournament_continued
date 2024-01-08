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
        serverTickTemp.with(serverTickPerm).forEach { (f, active) ->
            if (!active) return@forEach
            f(server)
        }
        serverTickTemp.clear()
    }

    data class Ticking(
        val f: (MinecraftServer) -> Unit,
        val active: Boolean
    )
}
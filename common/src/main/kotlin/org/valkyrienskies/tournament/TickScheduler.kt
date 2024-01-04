package org.valkyrienskies.tournament

import net.minecraft.server.MinecraftServer
import java.util.concurrent.ConcurrentHashMap

object TickScheduler {
    private val serverTickTemp = ConcurrentHashMap.newKeySet<(MinecraftServer) -> Unit>()
    private val serverTickPerm = ConcurrentHashMap.newKeySet<(MinecraftServer) -> Unit>()

    fun serverTickTemp(f: (MinecraftServer) -> Unit) {
        serverTickTemp.add(f)
    }

    fun serverTickPerm(f: (MinecraftServer) -> Unit) {
        serverTickPerm.add(f)
    }

    fun tickServer(server: MinecraftServer) {
        serverTickTemp.forEach { it(server) }
        serverTickTemp.clear()
        serverTickPerm.forEach { it(server) }
    }
}
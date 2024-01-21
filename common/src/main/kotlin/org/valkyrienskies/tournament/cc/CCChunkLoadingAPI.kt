package org.valkyrienskies.tournament.cc

import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.shared.computer.core.ServerComputer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import org.joml.Vector2i
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.chunk.ChunkLoader
import org.valkyrienskies.tournament.chunk.ChunkLoaderManager
import org.valkyrienskies.tournament.chunk.ChunkLoadingTicket
import org.valkyrienskies.tournament.util.extension.toChunkPos

class CCChunkLoadingAPI(
    private val computer: ServerComputer,
    private val level: ServerLevel
): ILuaAPI, ChunkLoader {
    override fun getNames() =
        arrayOf("vst_chunks")

    private var ticket: ChunkLoadingTicket? = null

    private var frontOff: Vector2i? = null

    private val manager by lazy {
        ChunkLoaderManager.getFor(level)
    }

    private fun Map<*, *>.toVec2(): Vector2i {
        val x = this["x"] as Double?
            ?: throw LuaException("Missing field 'x' in frontOff")

        val z = this["z"] as Double?
            ?: throw LuaException("Missing field 'z' in frontOff")

        return Vector2i(x.toInt(), z.toInt())
    }

    @LuaFunction
    fun start(priority: Double, frontOff: Map<*, *>) {
        if (ticket != null)
            throw LuaException("Chunk loading already started!")

        this.frontOff = frontOff.toVec2()
        ticket = manager.allocate(this, priority.toInt())
    }

    @LuaFunction
    fun stop() {
        if (ticket == null)
            throw LuaException("Chunk loading not started!")

        ticket!!.dispose()
        ticket = null
    }

    @LuaFunction
    fun help() =
        """
        |vst_chunks.start(priority: number, chunkVec: {x: number, z: number})
        |vst_chunks.stop()
        |
        |All the chunks in the rectangle of the computer's position and the computer's position + chunkVec will be loaded.
        |The higher the priority, the more likely it is that the chunks will be loaded.
        """.trimMargin()

    private fun getCurrPos() =
        level
            .getShipObjectManagingPos(computer.position)
            ?.shipToWorld
            ?.transformPosition(computer.position.toJOMLD())
            ?: computer.position.toJOMLD()

    override fun getCurrentChunk(): ChunkPos =
        getCurrPos().toChunkPos()

    override fun getFutureChunk(): ChunkPos =
        getCurrPos().toChunkPos(frontOff!!)
}
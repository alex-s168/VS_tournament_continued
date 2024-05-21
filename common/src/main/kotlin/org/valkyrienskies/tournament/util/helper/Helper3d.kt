package org.valkyrienskies.tournament.util.helper

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.tournament.util.extension.toBlock
import kotlin.math.absoluteValue

object Helper3d {

    fun getShipRenderPosition(level: ClientLevel, pos: Vector3d): Vector3d =
        level.getShipObjectManagingPos(pos)?.renderTransform?.shipToWorld?.transformPosition(pos)
            ?: pos

    fun getShipRenderPosition(level: Level, pos: Vector3d): Vector3d =
        when (level) {
            is ServerLevel -> convertShipToWorldSpace(level, pos)
            is ClientLevel -> getShipRenderPosition(level, pos)
            else -> pos
        }

    fun convertShipToWorldSpace(level: Level, pos: Vector3d): Vector3d =
        level.getShipObjectManagingPos(pos) ?.shipToWorld ?.transformPosition(pos)
            ?: pos

    fun convertShipToWorldSpace(level: Level, pos: BlockPos): Vector3d =
        convertShipToWorldSpace(level, pos.toJOMLD())

    fun convertShipToWorldSpace(level: Level, pos: Vec3): Vector3d =
        convertShipToWorldSpace(level, pos.toJOML())

    fun drawParticleLine(a: Vector3d, b: Vector3d, level: Level, particle: ParticleOptions) {
        val le = a.distance(b) * 3
        for (i in 1..le.toInt()) {
            val pos = a.lerp(b, i / le)
            level.addParticle(particle, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0)
        }
    }

    fun drawQuadraticParticleCurve(a : Vector3d, c : Vector3d, length: Double, segments:Double, level: Level, particle: ParticleOptions) {
        val lengthAC = (a.sub(c).length() * segments).absoluteValue
        val lengthTOT = length * segments
        val b = c.sub(c.sub(a).div(2.0))

        if(lengthAC < lengthTOT) {
            b.y -= lengthTOT - lengthAC
        }

        for (i in 1..lengthAC.toInt()) {
            val t = i / lengthAC

            val d : Vector3d = a.lerp(b, t)
            val e : Vector3d = b.lerp(c, t)
            val x : Vector3d = d.lerp(e, t)

            level.addParticle(particle, x.x, x.y, x.z, 0.0, 0.0, 0.0)
        }
    }

}
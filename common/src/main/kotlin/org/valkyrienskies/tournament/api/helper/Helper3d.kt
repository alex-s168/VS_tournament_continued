package org.valkyrienskies.tournament.api.helper

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.tournament.api.extension.toBlock
import kotlin.math.absoluteValue

object Helper3d {

    fun convertShipToWorldSpace(level: Level, pos: BlockPos) : Vector3d {
        val s = level.getShipManagingPos(pos)
        return if (s == null) {
            pos.toJOMLD()
        } else {
            s.shipToWorld.transformPosition(pos.toJOMLD())
        }
    }

    fun convertShipToWorldSpace(level: Level, vec: Vector3d) : Vector3d {
        return convertShipToWorldSpace(level, vec.toBlock())
    }

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
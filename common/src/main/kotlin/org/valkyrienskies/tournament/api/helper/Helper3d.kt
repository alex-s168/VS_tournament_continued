package org.valkyrienskies.tournament.api.helper

import de.m_marvin.univec.impl.Vec3d
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.world.level.Level
import org.valkyrienskies.mod.common.getShipManagingPos
import kotlin.math.absoluteValue

object Helper3d {

    fun VecToPosition(vec: Vec3d): BlockPos {
        return BlockPos(vec.x,vec.y,vec.z)
    }

    fun VecBlockMid(vec: Vec3d): Vec3d {
        return vec.add(0.25,0.25,0.25)
    }

    fun PositionToVec(pos: BlockPos): Vec3d {
        var vec = Vec3d()
        vec.x = pos.x.toDouble()
        vec.y = pos.y.toDouble()
        vec.z = pos.z.toDouble()
        return vec
    }

    fun MaybeShipToWorldspace(level: Level, pos: BlockPos): Vec3d {
        val s = level.getShipManagingPos(pos)
        if (s == null) {
            return PositionToVec(pos)
        } else {
            return Vec3d(s.shipToWorld.transformPosition(PositionToVec(pos).conv()))
        }
    }

    fun MaybeShipToWorldspace(level: Level, vec: Vec3d): Vec3d {
        return MaybeShipToWorldspace(level, VecToPosition(vec))
    }

    fun drawParticleLine(a: Vec3d, b: Vec3d, level: Level, particle: ParticleOptions) {
        val le = a.dist(b) * 3
        for (i in 1..le.toInt()) {
            val pos = a.lerp(b, i / le)
            level.addParticle(particle, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0)
        }
    }

    fun drawQuadraticParticleCurve(A: Vec3d, C: Vec3d, length: Double, segments:Double , level: Level, particle: ParticleOptions) {
        val lengthAC:Double = (A.sub(C).length() * segments).absoluteValue
        val lengthTOT:Double = length * segments
        var B:Vec3d = C.sub(C.sub(A).div(2.0))
        if(lengthAC < lengthTOT){ B.y -= lengthTOT - lengthAC }


        for (i in 1..lengthAC.toInt()) {
            val t = i / lengthAC

            val D:Vec3d = A.lerp(B, t)
            val E:Vec3d = B.lerp(C, t)
            val X:Vec3d = D.lerp(E, t)

            level.addParticle(particle, X.x, X.y, X.z, 0.0, 0.0, 0.0)
        }
    }

}
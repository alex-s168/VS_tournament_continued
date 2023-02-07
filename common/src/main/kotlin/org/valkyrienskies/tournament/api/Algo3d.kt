package org.valkyrienskies.tournament.api

import de.m_marvin.univec.impl.Vec3d
import kotlin.math.absoluteValue

object Algo3d {

    fun cone(pos: Vec3d, radius: Double, height: Double) : List<Vec3d> {
        var result = ArrayList<Vec3d>()

        var y = pos.y
        for (i in 0..height.absoluteValue.toInt()) {
            result.addAll( Helper2d.vec2listTo3(Algo2d.filledCircleDirty(Helper2d.vec3to2(pos), radius / i.toDouble())!!, y) )

            if (height < 0) {y -= 1}
            else {y += 1}
        }

        return result
    }

    fun sphereLikeShape(pos: Vec3d, radius: Double, height: Double) : List<Vec3d> {
        var result = ArrayList<Vec3d>()

        var y = pos.y
        for (i in 0..height.absoluteValue.toInt()) {
            result.addAll( Helper2d.vec2listTo3(Algo2d.filledCircleDirty(Helper2d.vec3to2(pos), i.toDouble() / y + (pos.y / 2))!! , y) )

            if (height < 0) {y -= 1}
            else {y += 1}
        }

        return result
    }

}
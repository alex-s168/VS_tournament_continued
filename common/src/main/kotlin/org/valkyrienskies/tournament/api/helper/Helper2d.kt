package org.valkyrienskies.tournament.api.helper

import de.m_marvin.univec.impl.Vec2d
import de.m_marvin.univec.impl.Vec3d

object Helper2d {

    //todo: PR to univec for doing that (if not already implemented)

    fun vec2to3(vec: Vec2d, y: Double) : Vec3d {
        return Vec3d(vec.x, y, vec.y)
    }

    fun vec3to2(vec: Vec3d) : Vec2d {
        return Vec2d(vec.x, vec.z)
    }

    fun vec2listTo3(l : List<Vec2d>, y : Double) : List<Vec3d> {
        var newlist = ArrayList<Vec3d>()
        l.forEach {
            newlist.add(vec2to3(it, y))
        }
        return newlist
    }

}
package org.valkyrienskies.tournament.util.helper

import org.joml.Vector2d
import org.joml.Vector3d
import org.valkyrienskies.tournament.util.extension.to3d

object Helper2d {

    fun vec2listTo3(list : List<Vector2d>) : List<Vector3d> {
        val nl = ArrayList<Vector3d>()

        list.forEach {
            nl.add(it.to3d())
        }

        return nl
    }

    fun vec2listTo3(list : List<Vector2d>, y : Double) : List<Vector3d> {
        val nl = ArrayList<Vector3d>()

        list.forEach {
            nl.add(Vector3d(it.x, y, it.y))
        }

        return nl
    }

}
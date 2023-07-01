package org.valkyrienskies.tournament.api.algo

import org.joml.Vector3d
import org.valkyrienskies.tournament.api.extension.to2d
import org.valkyrienskies.tournament.api.helper.Helper2d
import kotlin.math.absoluteValue

object Algo3d {

    fun cone(pos: Vector3d, radius: Double, height: Double) : List<Vector3d> {
        val result = ArrayList<Vector3d>()

        var y = pos.y
        for (i in 0..height.absoluteValue.toInt()) {
            result.addAll(
                Helper2d.vec2listTo3(
                    Algo2d.filledCircleDirty(pos.to2d(), radius / i.toDouble()),
                    y
                )
            )

            if (height < 0)
                y -= 1
            else
                y += 1
        }

        return result
    }

    fun sphereLikeShape(pos: Vector3d, radius: Double, height: Double) : List<Vector3d> {
        val result = ArrayList<Vector3d>()

        var y = pos.y
        for (i in 0..height.absoluteValue.toInt()) {
            result.addAll(
                Helper2d.vec2listTo3(
                    Algo2d.filledCircleDirty(
                        pos.to2d(),
                        i.toDouble() / y + (pos.y / 2)
                    ),
                    y
                )
            )

            if (height < 0) {y -= 1}
            else {y += 1}
        }

        return result
    }

}
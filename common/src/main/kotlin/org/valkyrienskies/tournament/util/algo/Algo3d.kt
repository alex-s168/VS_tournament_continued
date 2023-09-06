package org.valkyrienskies.tournament.util.algo

import org.joml.Vector2d
import org.joml.Vector3d
import org.valkyrienskies.tournament.util.extension.getRange
import org.valkyrienskies.tournament.util.extension.to2d
import org.valkyrienskies.tournament.util.helper.Helper2d
import kotlin.math.absoluteValue
import kotlin.math.sign

object Algo3d {

    fun cone(pos: Vector3d, radius: Double, height: Double) =
        0.getRange(height.toInt()).map { y ->
            Helper2d.vec2listTo3(Algo2d.filledCircleDirty(
                pos.to2d(),
                (radius * -height.sign) - (radius * (y.toDouble() / height))
            ), pos.y + y)
        }.reduce { a, b -> a + b }

    fun sphere(pos: Vector3d, radius: Double) : List<Vector3d> {
        val result = ArrayList<Vector3d>()

        val x = pos.add(radius, 0.0, 0.0)

        for (i in 0..179) {
            val y = x.rotateAxis(i.toDouble(), 1.0, 0.0, 0.0)
            val r = y.x()
            val h = y.y()
            result.addAll(
                Helper2d.vec2listTo3(
                    Algo2d.filledCircleDirty(
                        Vector2d(pos.x, pos.y),
                        r
                    ),
                    h
                )
            )
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
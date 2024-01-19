package org.valkyrienskies.tournament.util.math

import org.joml.Vector2i
import org.joml.primitives.Rectanglei

data class AxisExpandableBox2i(
    val mid: Vector2i,
    val expansionLeft: Double,
    val expansionRight: Double,
    val expansionUp: Double,
    val expansionDown: Double
) {
    var value: Double = 0.0

    private val aabbO = Rectanglei()

    val aabb: Rectanglei get() =
        aabbO.also {
            it.minX = (mid.x - value * expansionLeft).toInt()
            it.maxX = (mid.x + value * expansionRight).toInt()
            it.minY = (mid.y - value * expansionDown).toInt()
            it.maxY = (mid.y + value * expansionUp).toInt()
        }
}
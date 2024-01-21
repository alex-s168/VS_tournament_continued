package org.valkyrienskies.tournament.util.math

import org.joml.Vector2i
import org.joml.primitives.Rectanglei

// TODO: remove?
data class AxisExpandableBox2i(
    var mid: Vector2i,
    var expansionLeft: Double,
    var expansionRight: Double,
    var expansionUp: Double,
    var expansionDown: Double
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
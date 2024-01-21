package org.valkyrienskies.tournament.util.extension

import org.joml.Vector2i
import org.joml.primitives.Rectanglei

fun Rectanglei.fix(): Rectanglei =
    this.also {
        if (it.minX > it.maxX) {
            val temp = it.minX
            it.minX = it.maxX
            it.maxX = temp
        }
        if (it.minY > it.maxY) {
            val temp = it.minY
            it.minY = it.maxY
            it.maxY = temp
        }
    }

fun Rectanglei.scaleFrom(factor: Float, center: Vector2i): Rectanglei =
    this.also {
        val midX = center.x
        val midY = center.y
        val width = it.maxX - it.minX
        val height = it.maxY - it.minY
        val newWidth = (width * factor).toInt()
        val newHeight = (height * factor).toInt()
        it.minX = midX - newWidth / 2
        it.maxX = midX + newWidth / 2
        it.minY = midY - newHeight / 2
        it.maxY = midY + newHeight / 2
    }

fun Rectanglei.values(): Sequence<Vector2i> =
    sequence {
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                yield(Vector2i(x, y))
            }
        }
    }
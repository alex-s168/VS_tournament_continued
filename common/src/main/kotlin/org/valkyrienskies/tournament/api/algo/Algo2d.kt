package org.valkyrienskies.tournament.api.algo

import org.joml.Vector2d

object Algo2d {

    //TODO: broken
    fun filledCircle(vec: Vector2d?, r: Double) : List<Vector2d> {
        val pos = vec!!
        return fillVectors(pos, circle(pos, r))
    }

    fun filledCircleDirty(vec: Vector2d?, r: Double) : List<Vector2d> {
        val pos = vec!!

        val res = ArrayList<Vector2d>()

        for (i in 1..r.toInt()) {
            res.addAll(circle(pos, i.toDouble()))
            res.addAll(circle(pos.add(1.0,1.0), i.toDouble()))
            res.addAll(circle(pos.add(1.0,0.0), i.toDouble()))
            res.addAll(circle(pos.add(0.0,1.0), i.toDouble()))
            res.addAll(circle(pos.sub(1.0,1.0), i.toDouble()))
            res.addAll(circle(pos.sub(1.0,0.0), i.toDouble()))
            res.addAll(circle(pos.sub(0.0,1.0), i.toDouble()))
        }

        return res
    }

    fun fill(pos: Vector2d, targetVectors: List<Vector2d>, filledVectors: List<Vector2d>, max: Vector2d, min: Vector2d) : List<Vector2d> {
        val vecs = ArrayList<Vector2d>()
        vecs.addAll(filledVectors)

        if (pos.x < min.x || pos.x > max.x || pos.y < min.y || pos.y > max.y) {
            return vecs
        }

        if (targetVectors.contains(pos) || vecs.contains(pos)) {return vecs}

        println("it pos: $pos    max: $max    min: $min")

        vecs.add(pos)
        vecs.add(pos.add(1.0, 0.0))

        vecs.addAll(fill(pos.add(1.0, 0.0), targetVectors, vecs, max, min))
        vecs.addAll(fill(pos.sub(1.0, 0.0), targetVectors, vecs, max, min))
        vecs.addAll(fill(pos.add(0.0, 1.0), targetVectors, vecs, max, min))
        vecs.addAll(fill(pos.sub(0.0, 1.0), targetVectors, vecs, max, min))

        return vecs
    }

    fun fillVectors(pos: Vector2d, targetVectors: List<Vector2d>) : List<Vector2d> {
        val maxX = targetVectors.map { it.x }.max()
        val maxY = targetVectors.map { it.y }.max()

        val minX = targetVectors.map { it.x }.min()
        val minY = targetVectors.map { it.y }.min()

        val res = fill(pos, targetVectors, ArrayList(), Vector2d(maxX, maxY), Vector2d(minX, minY))

        println("Fill Vectors result: ${res.size}")

        return res
    }


    fun circle(vec: Vector2d, r: Double) : List<Vector2d> {
        val x0 = vec.x
        val y0 = vec.y

        val result = mutableListOf<Vector2d>()
        var x = r
        var y = 0
        var decisionOver2 = 1 - x

        while (y <= x) {
            result.add(Vector2d(x + x0, y + y0))
            result.add(Vector2d(y + x0, x + y0))
            result.add(Vector2d(-x + x0, y + y0))
            result.add(Vector2d(-y + x0, x + y0))
            result.add(Vector2d(-x + x0, -y + y0))
            result.add(Vector2d(-y + x0, -x + y0))
            result.add(Vector2d(x + x0, -y + y0))
            result.add(Vector2d(y + x0, -x + y0))
            y++
            if (decisionOver2 <= 0) {
                decisionOver2 += 2 * y + 1
            } else {
                x--
                decisionOver2 += 2 * (y - x) + 1
            }
        }

        return result
    }

}
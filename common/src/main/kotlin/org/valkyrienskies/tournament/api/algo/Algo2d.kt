package org.valkyrienskies.tournament.api.algo

import de.m_marvin.univec.impl.Vec2d

object Algo2d {

    //TODO: broken
    fun filledCircle(vec: Vec2d?, r: Double): List<Vec2d>? {
        val pos = vec!!
        println("pos: $pos")
        return fillVectors(pos, circle(pos, r))
    }

    fun filledCircleDirty(vec: Vec2d?, r: Double): List<Vec2d>? {
        val pos = vec!!

        var res = ArrayList<Vec2d>()

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

    fun fill(pos: Vec2d, targetVectors: List<Vec2d>, filledVectors: List<Vec2d>, max: Vec2d, min: Vec2d) : List<Vec2d> {
        var vecs = ArrayList<Vec2d>()
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

    fun fillVectors(pos: Vec2d, targetVectors: List<Vec2d>): List<Vec2d> {
        val maxX = targetVectors.map { it.x }.max()
        val maxY = targetVectors.map { it.y }.max()

        val minX = targetVectors.map { it.x }.min()
        val minY = targetVectors.map { it.y }.min()

        val res = fill(pos, targetVectors, ArrayList(), Vec2d(maxX, maxY), Vec2d(minX, minY))

        println("Fill Vectors result: ${res.size}")

        return res
    }


    fun circle(vec: Vec2d, r: Double): List<Vec2d> {
        val x0 = vec.x
        val y0 = vec.y

        val result = mutableListOf<Vec2d>()
        var x = r
        var y = 0
        var decisionOver2 = 1 - x

        while (y <= x) {
            result.add(Vec2d(x + x0, y + y0))
            result.add(Vec2d(y + x0, x + y0))
            result.add(Vec2d(-x + x0, y + y0))
            result.add(Vec2d(-y + x0, x + y0))
            result.add(Vec2d(-x + x0, -y + y0))
            result.add(Vec2d(-y + x0, -x + y0))
            result.add(Vec2d(x + x0, -y + y0))
            result.add(Vec2d(y + x0, -x + y0))
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
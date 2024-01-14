package org.valkyrienskies.tournament.util.math

fun lerp(a: Double, b: Double, t: Double): Double =
    a + (b - a) * t

fun lerp(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t

fun invLerp(a: Double, b: Double, v: Double): Double =
    (v - a) / (b - a)

fun invLerp(a: Float, b: Float, v: Float): Float =
    (v - a) / (b - a)
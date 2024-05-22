package org.valkyrienskies.tournament.util.math

import kotlin.math.max
import kotlin.math.min

fun clamp(v: Double, minv: Double, maxv: Double): Double =
    if (minv > maxv) clamp(v, maxv, minv)
    else min(max(v, minv), maxv)

fun lerp(a: Double, b: Double, t: Double): Double =
    clamp(a + (b - a) * t, a, b)

fun invLerp(a: Double, b: Double, v: Double): Double =
    (v - a) / (b - a)

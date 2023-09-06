package org.valkyrienskies.tournament.util.extension

fun Int.getRange(b: Int): IntRange =
    if (this > b) (b..this) else (this..b)
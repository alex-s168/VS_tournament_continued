package org.valkyrienskies.tournament.util

data class LazyWithLateParam<T: Any, P>(
    val compute: (P) -> T,
) {
    var value: T? = null

    inline fun get(param: () -> P): T =
        value ?: compute(param()).also { value = it }
}
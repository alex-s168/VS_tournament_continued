package org.valkyrienskies.tournament.util.reflection

data class IsLoaded(
    val loaded: Boolean,
    val clazz: Class<*>? = null
): Comparable<Boolean> {
    override fun compareTo(other: Boolean): Int =
        loaded.compareTo(other)
}

fun loadedClass(className: String): IsLoaded =
    try {
        IsLoaded(true, Class.forName(className))
    } catch (e: ClassNotFoundException) {
        IsLoaded(false)
    }
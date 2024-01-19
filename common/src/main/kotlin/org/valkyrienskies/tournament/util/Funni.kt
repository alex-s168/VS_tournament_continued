package org.valkyrienskies.tournament.util

class Funni<T> {
    private val id = next ++

    override fun equals(other: Any?) =
        other is Funni<*> && other.id == id

    override fun hashCode(): Int =
        id

    companion object {
        private var next = 0
    }
}
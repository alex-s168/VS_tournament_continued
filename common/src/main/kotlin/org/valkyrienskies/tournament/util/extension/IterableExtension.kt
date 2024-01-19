package org.valkyrienskies.tournament.util.extension

fun <T> Iterable<T>.with(other: Iterable<T>): Iterable<T> =
    object: Iterable<T> {
        override fun iterator(): Iterator<T> =
            object: Iterator<T> {
                val it1 = this@with.iterator()
                val it2 = other.iterator()

                override fun hasNext(): Boolean = it1.hasNext() || it2.hasNext()

                override fun next(): T = if (it1.hasNext()) it1.next() else it2.next()

            }
    }

fun <T> Iterable<T>.itTake(n: Int): Iterable<T> =
    object: Iterable<T> {
        override fun iterator(): Iterator<T> =
            object: Iterator<T> {
                var itbg = this@itTake.iterator()
                var pos = 0

                override fun hasNext(): Boolean =
                    pos < n && itbg.hasNext()

                override fun next(): T =
                    itbg.next()
            }
    }
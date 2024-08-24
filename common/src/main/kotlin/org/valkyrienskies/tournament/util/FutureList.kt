package org.valkyrienskies.tournament.util

class FutureList<T>(
    val futures: List<() -> T>
): List<T> {
    override val size: Int
        get() = futures.size

    override fun get(index: Int): T =
        futures[index]()

    override fun isEmpty(): Boolean =
        futures.isEmpty()

    override fun iterator(): Iterator<T> =
        listIterator()

    override fun listIterator(): ListIterator<T> =
        listIterator(0)

    override fun listIterator(index: Int): ListIterator<T> =
        object : ListIterator<T> {
            var idx = index

            override fun hasNext(): Boolean = idx < size
            override fun hasPrevious(): Boolean {
                TODO("Not yet implemented")
            }

            override fun next(): T {
                if (!hasNext()) throw NoSuchElementException()
                return get(idx++)
            }

            override fun nextIndex(): Int =
                idx + 1

            override fun previous(): T =
                get(idx - 1)

            override fun previousIndex(): Int =
                idx - 1
        }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> =
        FutureList(futures.subList(fromIndex, toIndex))

    override fun lastIndexOf(element: T): Int =
        futures.map { it() }.lastIndexOf(element)

    override fun indexOf(element: T): Int =
        futures.map { it() }.indexOf(element)

    override fun containsAll(elements: Collection<T>): Boolean =
        futures.map { it() }.containsAll(elements)

    override fun contains(element: T): Boolean =
        futures.map { it() }.contains(element)

}
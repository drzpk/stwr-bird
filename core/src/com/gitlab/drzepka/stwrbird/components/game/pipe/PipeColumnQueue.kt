package com.gitlab.drzepka.stwrbird.components.game.pipe

/**
 * Kolejka cykliczna rur.
 */
class PipeColumnQueue(collection: Collection<BasePipeColumn>) : Iterable<BasePipeColumn> {

    private val array = collection.toTypedArray()

    private var current = 0

    /**
     * Zwraca pierwszy element kolejki.
     */
    fun first(): BasePipeColumn = array[current]

    /**
     * Zwraca ostatni element kolejki.
     */
    fun last(): BasePipeColumn = array[if (current > 0) current - 1 else array.lastIndex]

    /**
     * Przesuwa pierwszy element na ostatnią pozycję.
     */
    fun shift() {
        current = (current + 1) % array.size
    }

    override fun iterator(): Iterator<BasePipeColumn> {
        return object : Iterator<BasePipeColumn> {

            private var index = current
            private var passed = 0

            override fun hasNext(): Boolean = passed < array.size

            override fun next(): BasePipeColumn {
                val pipe = array[index]
                index = (index + 1) % array.size
                passed++
                return pipe
            }

        }
    }
}
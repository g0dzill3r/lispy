package lispy.internal

/**
 * An extended-features iterator for moving through a source-file or source-stream.
 */

class Source (iter: Iterator<Char>, val file: String = "input"): Iterator<Char> {
    constructor (input: String, file: String = "input"): this (input.iterator ())

    private val wrapped = iter
    private var saved: MutableList<Char> = mutableListOf ()

    var row: Int = 0
        private set

    var column: Int = 0
        private set

    data class Location (val file: String, val row: Int, val column: Int) {
        override fun toString (): String = "$file($row:$column)"
    }
    val location: Location
        get () = Location (file, row, column)

    fun hasNext (index: Int): Boolean {
        while (saved.size < index + 1) {
            if (wrapped.hasNext ()) {
                saved.add (wrapped.next ())
            } else {
                return false
            }
        }
        return true
    }

    override fun hasNext(): Boolean {
        return if (saved.isNotEmpty ()) {
            true
        } else {
            wrapped.hasNext ()
        }
    }

    fun peek (index: Int = 0): Char {
        while (saved.size < index + 1) {
            saved.add (wrapped.next ())
        }
        return saved.get (index)
    }

    private fun update (c: Char) {
        if (c == '\n') {
            row ++
            column = 0
        } else {
            column ++
        }
    }

    override fun next(): Char {
        return if (saved.isNotEmpty()) {
            saved.removeAt (0)
        } else {
            val next = wrapped.next ()
            update (next)
            next
        }
    }
}



// EOF
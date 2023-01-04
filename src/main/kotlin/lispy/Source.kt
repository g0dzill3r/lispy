package lispy

/**
 * An extended-features iterator for moving through a source-file or source-stream.
 */

class Source (iter: Iterator<Char>, val file: String = "input"): Iterator<Char> {
    constructor (input: String, file: String = "input"): this (input.iterator ())

    private val wrapped = iter
    private var saved: Char? = null;

    var row: Int = 0
        private set

    var column: Int = 0
        private set

    data class Location (val file: String, val row: Int, val column: Int) {
        override fun toString (): String = "$file($row:$column)"
    }
    val location: Location
        get () = Location (file, row, column)

    override fun hasNext(): Boolean {
        return if (saved != null) {
            true
        } else {
            wrapped.hasNext ()
        }
    }

    fun peek (): Char {
        if (saved == null) {
            saved = next ()
        }
        return saved!!
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
        return if (saved != null) {
            val tmp = saved!!
            saved = null
            tmp
        } else {
            val next = wrapped.next ()
            update (next)
            next
        }
    }
}



// EOF
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

fun main () {
    val source = Source ("""
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce rhoncus molestie ipsum quis vulputate. 
        Vivamus sit amet libero tellus. Etiam lacinia eros ante, in finibus mi lobortis tincidunt. Integer eget 
        lorem eu tellus faucibus placerat. Sed ornare facilisis risus eu egestas. In nisl tortor, porta eu iaculis id, 
        venenatis sit amet mi. Aenean quis interdum purus. Integer feugiat pulvinar nisl vestibulum viverra. 
        Aliquam vel orci vitae est finibus placerat. Proin lectus nunc, fringilla in fermentum nec, consequat vitae nisi. 
        Praesent faucibus dui nec fermentum feugiat. Donec semper tortor viverra nisi elementum, sed tincidunt dolor convallis. 
        Suspendisse consectetur eget urna quis posuere. Donec tincidunt blandit mauris at luctus. Curabitur consectetur 
        venenatis augue, eget aliquet mi consectetur at. Etiam ut egestas ex.
    """.trimIndent())

    source.forEach { c ->
        when {
            c.isUpperCase() -> println("Found an $c at ${source.location}.")
            else -> Unit
        }
    }

    return
}

// EOF
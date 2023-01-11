package scheme.util

import lispy.peekable

/**
 * Demonstrate the indexed lookahead in the [PeekableIterator].
 */

fun main () {

    // Test lookahead peek

    val str = "Once upon a time..."
    str.let {
        val iter = it.iterator ().peekable ()
        for (i in 0 .. 3) {
            println ("peek($i) = ${iter.peek (i)}")
        }
        while (iter.hasNext ()) {
            println (iter.next ())
        }
    }

    // Test lookahead hasNext

    val str2 = "Test"
    str2.let {
        val iter = it.iterator().peekable()
        for (i in 0 .. 10) {
            println ("hasNext($i) = ${iter.hasNext (i)}")
        }
    }

    return
}

// EOF
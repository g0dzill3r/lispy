package lispy

import java.util.*

/**
 * Primitive REPL style interpreter.
 */
fun interpreter (prompt: String = "> ", func: (String) -> Boolean) {
    while (true) {
        try {
            print (prompt)
            val input = readln()
            if (! func (input)) {
                break
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return
}

/**
 * Extension function for the stack to allow us to push something, perform and action
 * and then ensure that the item is popped.
 */
fun <T, S> Stack<T>.push (t: T, func: () -> S): S {
    return try {
        push(t)
        func ()
    }
    finally {
        pop()
    }
}


/**
 * A wrapper for the Iterator<T> that allows for single look-ahead. 
 */
fun <T> Iterator<T>.peekable () : PeekableIterator<T> = PeekableIterator<T> (this)

class PeekableIterator<T> (val wrapped: Iterator<T>): Iterator<T> {
    private var saved: T? = null;

    override fun hasNext(): Boolean {
        return if (saved != null) {
            true
        } else {
            wrapped.hasNext ()
        }
    }

    fun peek (): T {
        if (saved == null) {
            saved = wrapped.next ()
        }
        return saved!!
    }

    override fun next(): T {
        return saved?.let {
            val tmp = saved
            saved = null
            tmp
        } ?: wrapped.next ()
    }
}

// EOF
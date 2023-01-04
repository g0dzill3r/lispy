package lispy

import java.util.*

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

fun <T, S> Stack<T>.push (t: T, func: () -> S): S {
    return try {
        push(t)
        func ()
    }
    finally {
        pop()
    }
}

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
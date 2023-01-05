package lispy.test

import lispy.Interpreter
import lispy.NilValue
import lispy.ProviderFactory

fun main() {
    println ("Enter a blank line for force evaluation")

    val provider = ProviderFactory.getProvider()
    val interp = Interpreter (provider)

    fun isValid (str: String): Boolean {
        return try {
            provider.parser.parseMany (str)
            true
        }
        catch (e: Exception) {
            false
        }
    }

    loop {
        absorb {
            val buf = StringBuffer ()
            while (true) {
                val count = buf.count { it == '\n' }
                val isValid = isValid (buf.toString ())
                print ("$count${if (isValid) "." else ":"} ")
                val s = readln()
                if (s == "" || s == "e") {
                    break
                } else if (s == "?") {
                    println ("""
                        p - print buffer
                        d - delete last line
                        c - clear buffer
                        e - execute buffer
                    """.trimIndent())
                } else if (s == "p") {
                    val str = buf.toString ()
                    if (str.isNotEmpty()) {
                        println(str.substring(0, str.length - 1))
                    }
                } else if (s == "d") {
                    val i = buf.lastIndexOf("\n", buf.length - 2) + 1
                    buf.setLength (i)
                } else if (s == "c") {
                    buf.setLength (0)
                } else {
                    buf.append(s)
                    buf.append ("\n")
                }
            }
            val expr = buf.toString ()
            if (expr.isNotEmpty ()) {
                val result = interp.eval(buf.toString())
//                if (result != NilValue) {
                    println("-> $result")
//                }
            }
        }
    }
}

fun loop (func: () -> Unit): Nothing {
    while (true) {
        func ()
    }
}

fun absorb (func: () -> Unit) {
    try {
        func ()
    }
    catch (e: Exception) {
        e.printStackTrace()
    }
}

// EOF
package lispy.test

import lispy.*

fun main() {
    fun usage () {
        println ("""
                        p - print buffer
                        d - delete last line
                        c - clear buffer
                        e - execute buffer (or blank line)
                        ? - show these commands
                    """.trimIndent())
    }

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
                print ("$count: ")
                val s = readln()
                if (s == "") {
                    // IGNORED
                } else if (s == "e") {
                    break
                } else if (s == "?") {
                    usage ()
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
                    buf.append (s)
                    buf.append ("\n")
                    if (isValid (buf.toString ())) {
                        break
                    }
                }
            }

            // Execute the expressions that they provided

            val exprs = provider.parser.parseMany (buf.toString ())
            exprs.forEach {
                try {
                    val (_, result, output) = interp.evalOne (it)
                    if (output.isNotEmpty()) {
                        println (output.stripTrailingNewlines ())
                    }
                    when (result) {
                        NilValue -> Unit
                        else -> println ("=> $result")
                    }
                }
                catch (e: Exception) {
                    println (e.toString ())
                }
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
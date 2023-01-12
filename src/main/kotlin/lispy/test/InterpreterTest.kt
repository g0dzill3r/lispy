package lispy.test

import lispy.*

/**
 * An interactive REPL for interacting with the interpreter.
 */

fun main () {
//    val type = ProviderType.LIXY
//    val provider = ProviderFactory.getProvider (type)
    val provider = ProviderFactory.getProvider()
    val parser = provider.parser
    val lisp = Interpreter (provider)

    interpreter ("repl> ") {
        val exprs = parser.parseMany (it)
        exprs.forEach {
            try {
                val (_, result, output) = lisp.evalOne (it)
                if (output.isNotEmpty()) {
                    println (output.stripTrailingNewlines ())
                }
                when (result) {
                    is NilValue -> Unit
                    else -> println("=> $result")
                }
            }
            catch (e: Exception) {
                println (e.toString ())
            }
        }
        true
    }
    // NOT REACHED
}

// EOF
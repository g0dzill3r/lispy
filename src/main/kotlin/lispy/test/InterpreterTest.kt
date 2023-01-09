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
        lisp.eval (it) { input, result, output ->
            if (output.isNotEmpty()) {
                println (output.stripTrailingNewlines ())
            }
            when (result) {
                is NilValue -> Unit
                is StringValue -> println("=> \"${result}\"")
                else -> println("=> $result")
            }
        }
        true
    }
    // NOT REACHED
}

// EOF
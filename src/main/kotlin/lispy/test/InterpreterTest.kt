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
        val els: List<Expression> = parser.parseMany (it)
        els.forEach {
            val res = lisp.eval (it)
            when (res) {
                is NilValue -> Unit
                is StringValue -> println ("-> \"${res}\"")
                else -> println("-> $res")
            }
        }
        true
    }
    // NOT REACHED
}

// EOF
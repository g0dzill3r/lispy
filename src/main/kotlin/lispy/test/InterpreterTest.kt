package lispy.test

import lispy.Interpreter
import lispy.Parser
import lispy.interpreter

/**
 * An interactive REPL for interacting with the interpreter.
 */

fun main () {
    val lisp = Interpreter ()

    interpreter ("repl> ") {
        val exprs = Parser.parseMany (it)
        exprs.forEach {
            val res = lisp.eval (it)
            println ("-> $res")
        }
        true
    }
    // NOT REACHED
}

// EOF
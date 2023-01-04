package lispy.test

import lispy.Lexer
import lispy.interpreter

/**
 * An interactive REPL for interacting with the lexer.
 */

fun main() {
    interpreter ("l> ") {
        val tokens = Lexer.lex (it)
        tokens.iterator ().forEach {
            println(it)
        }
        true
    }

    // NOT REACHED
}

// EOF
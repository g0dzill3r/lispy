package lispy.test

import lispy.ExpressionCell
import lispy.Parser
import lispy.interpreter

/**
 * An interactive REPL for interacting with the parser.
 */
fun main () {
    interpreter ("p> ") {
        val exprs = Parser.parseMany (it)
        exprs.forEach {
            println (it)
            if (it is ExpressionCell) {
                println (it.toBrackets())
            }
        }
        true
    }
    // NOT REACHED
}

// EOF
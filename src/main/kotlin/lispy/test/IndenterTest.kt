package lispy.test

import lispy.Lexer
import lispy.interpreter
import lispy.Token

fun main() {
    interpreter ("l> ") {
        val tokens = Lexer.lex (it).toList ()
        Indenter (tokens).dump ()
        true
    }

    // NOT REACHED
}

class Indenter (val tokens: List<Token>) {
    fun dump () {
        var indent = 0
        for (token in tokens) {
            if (token is Token.RightParen) {
                indent --
            }
            if (indent < 0) {
                throw Exception ("Mismatched right parenthesis.")
            }
            val prefix = "  ".repeat (indent)
            println ("$prefix${Token.render (token)}")
            if (token is Token.LeftParen) {
                indent ++
            }
        }
        if (indent != 0) {
            throw Exception ("Missing right parenthesis (${indent}).")
        }
        return
    }
}


// EOF
package lispy.test

import lispy.ProviderFactory
import lispy.ProviderType
import lispy.interpreter
import lispy.Token

private fun dump (tokens: List<Token>) {
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

fun main() {
    val type = ProviderType.LIXY
    val provider = ProviderFactory.getProvider(type)
    val lexer = provider.lexer

    interpreter ("l> ") {
        val tokens = lexer.lex (it).toList ()
        dump (tokens)
        true
    }

    // NOT REACHED
}

// EOF
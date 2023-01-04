package lispy.test

import lispy.ProviderFactory
import lispy.interpreter

/**
 * An interactive REPL for interacting with the lexer.
 */

fun main() {
//    val type = ProviderType.LIXY
//    val provider = ProviderFactory.getProvider(type)
    val provider = ProviderFactory.getProvider()
    val lexer = provider.lexer

    interpreter ("lex[${provider.type.name}]> ") {
        val tokens = lexer.lex (it)
        tokens.iterator ().forEach {
            println(it)
        }
        true
    }

    // NOT REACHED
}

// EOF
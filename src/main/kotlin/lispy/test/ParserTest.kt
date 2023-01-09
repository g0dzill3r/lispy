package lispy.test

import lispy.Pair
import lispy.ProviderFactory
import lispy.interpreter

/**
 * An interactive REPL for interacting with the parser.
 */
fun main () {
//    val type = ProviderType.LIXY
//    val provider = ProviderFactory.getProvider(type)
    val provider = ProviderFactory.getProvider()
    val parser = provider.parser

    interpreter ("parser[${provider.type}]> ") {
        val exprs = parser.parseMany (it)
        exprs.forEach {
            println (it)
            if (it is Pair) {
                println (it.toBrackets())
            }
        }
        true
    }
    // NOT REACHED
}

// EOF
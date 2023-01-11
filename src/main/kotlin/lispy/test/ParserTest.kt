package lispy.test

import lispy.ConsPair
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
            println ("toString:       $it")
            (it as? ConsPair)?.let {
                println ("toDottedString: ${it.toDottedString ()}")
            }
        }
        true
    }
    // NOT REACHED
}

// EOF
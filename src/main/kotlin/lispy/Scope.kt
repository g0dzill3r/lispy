package lispy

/**
 * Represents a lexical scope.
 */

class Scope (val map: MutableMap<String, Any> = mutableMapOf (), val parent: Scope? = null) {
    fun put (symbol: Symbol, value: Any) = put (symbol.symbol, value)

    fun put (symbol: String, value: Any) {
        map[symbol] = value
//        if (value is Scoped) {
//            value.setScope (this)
//        }
        return
    }

    fun get (symbol: Symbol): Any? = get (symbol.symbol)

    fun get (symbol: String): Any? {
        return map[symbol] ?: parent?.get (symbol)
    }
}

// EOF
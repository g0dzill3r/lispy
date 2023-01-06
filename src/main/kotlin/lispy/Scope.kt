package lispy

import lispy.builtin.ActivationRecord

/**
 * Represents a lexical scope created by invocation of a procedure.
 */

class Scope (val map: MutableMap<String, Any> = mutableMapOf (), val parent: Scope? = null) {
    fun put (symbol: Symbol, value: Any) = put (symbol.symbol, value)

    fun put (symbol: String, value: Any) {
        map[symbol] = value
        return
    }

    fun get (symbol: Symbol): Any? = get (symbol.symbol)

    fun get (symbol: String): Any? {
        return map[symbol] ?: parent?.get (symbol)
    }

    fun getStack (): List<ActivationRecord> {
        val list = mutableListOf<ActivationRecord> ()
        var scope: Scope? = this
        while (scope != null) {
            val rec = scope.get ("_")
            if (rec != null) {
                list.add (rec as ActivationRecord)
            }
            scope = scope.parent
        }
        return list
    }
}

// EOF
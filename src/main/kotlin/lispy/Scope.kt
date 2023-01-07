package lispy

import lispy.builtin.ActivationRecord
import lispy.builtin.Invokable
import lispy.builtin.InvokableSupport

/**
 * Represents a lexical scope created by invocation of a procedure.
 */

class Scope (val map: MutableMap<String, Expression> = mutableMapOf (), val parent: Scope? = null) {
    fun put (symbol: Symbol, value: Expression) = put (symbol.symbol, value)

    fun put (symbol: String, value: Expression) {
        map[symbol] = value
        return
    }

    fun get (symbol: Symbol): Expression? = get (symbol.symbol)

    fun get (symbol: String, static: Boolean = false): Expression? {
        return if (static) {
            map[symbol] ?: parent?.get(symbol, static)
        } else {
            map[symbol]
        }
    }

    override fun toString (): String {
        return StringBuffer ().apply {
            val filtered = map.filter {
                 (it.value as? InvokableSupport)?.isBuiltin ?: true
            }
            append (filtered)
        }.toString ()
    }

//    fun getStack (): List<ActivationRecord> {
//        val list = mutableListOf<ActivationRecord> ()
//        var scope: Scope? = this
//        while (scope != null) {
//            val rec = scope.get ("_")
//            if (rec != null) {
//                list.add (rec as ActivationRecord)
//            }
//            scope = scope.parent
//        }
//        return list
//    }
}

// EOF
package lispy.builtin

import lispy.*

interface Invokable {
    val symbol: String
    fun invoke (cell: ConsPair, interp: Interpreter): Expression
}

abstract class InvokableSupport (override val symbol: String) : Invokable, Expression() {
    override fun toString (): String = "builtin:$symbol"

    val isBuiltin: Boolean
        get () = closure == null

    val isNotBuiltin: Boolean
        get () = closure != null

    var closure: Scope? = null

    fun setScope (scope: Scope) {
        closure = scope
        return
    }

    fun evalList (cell: ConsPair, interp: Interpreter, expect: Int? = null): List<Expression> {
        return buildList {
            cell.toList ().map {
                add (interp.eval (it))
            }
            expect?.let {
                if (this.size != expect) {
                    throw IllegalArgumentException ("Expected $expect arguments found ${this.size} in $cell.")
                }
            }
        }
    }

    fun expect (cell: ConsPair, count: Int): List<Expression> {
        val list = cell.toList ()
        if (list.size != count) {
            throw IllegalArgumentException ("Expected $count arguments; found ${list.size} in ${cell}")
        }
        return list
    }

    protected fun requireString (expr: Expression): StringValue {
        if (expr !is StringValue) {
            throw IllegalArgumentException ("Not a string: $expr")
        }
        return expr
    }

    protected fun requireSymbol (expr: Expression): Symbol {
        if (expr !is Symbol) {
            throw IllegalArgumentException ("Not a symbol: $expr")
        }
        return expr
    }
    fun requirePair (expr: Expression) : ConsPair {
        if (expr !is ConsPair) {
            throw IllegalArgumentException ("Not an expression cell: $expr")
        }
        return expr
    }
}

// EOF
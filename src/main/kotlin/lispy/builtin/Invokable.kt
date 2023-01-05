package lispy.builtin

import lispy.*

interface Invokable {
    val symbol: String
    fun invoke (cell: ExpressionCell, interp: Interpreter): Expression
}

abstract class InvokableSupport (override val symbol: String) : Invokable, Expression() {
    override fun toString(): String = "builtin:$symbol"

//    private var scope: Scope? = null
//    override fun getScope (): Scope = scope as Scope
//    override fun setScope (scope: Scope) {
//        this.scope = scope
//        return
//    }

    fun evalList (cell: ExpressionCell, interp: Interpreter): List<Expression> {
        val list = cell.toList ()
        return list.map {
            interp.eval (it)
        }
    }

    fun expect (cell: ExpressionCell, count: Int): List<Expression> {
        val list = cell.toList ()
        if (list.size != count) {
            throw IllegalArgumentException ("Expected $count arguments; found ${list.size}")
        }
        return list
    }

    protected fun requireSymbol (expr: Expression): Symbol {
        if (expr !is Symbol) {
            throw IllegalArgumentException ("Not a symbol: $expr")
        }
        return expr
    }
    fun requireExpressionCell (expr: Expression) : ExpressionCell {
        if (expr !is ExpressionCell) {
            throw IllegalArgumentException ("Not an expression cell: $expr")
        }
        return expr
    }
}

// EOF
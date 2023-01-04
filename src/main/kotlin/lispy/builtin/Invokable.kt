package lispy.builtin

import lispy.*

interface Invokable {
    val symbol: String
    fun invoke (cell: ExpressionCell, interp: Interpreter): Expression
}

abstract class InvokableSupport (override val symbol: String) : Invokable, Expression() {
    override fun toString(): String = "builtin:$symbol"

    fun evalList (cell: ExpressionCell, interp: Interpreter): List<Expression> {
        val list = toList (cell)
        return list.map {
            interp.eval (it)
        }
    }

    fun toList (cell: ExpressionCell, list: MutableList<Expression> = mutableListOf ()): List<Expression> {
        if (cell.car != NilValue) {
            list.add (cell.car)
        }
        when (cell.cdr) {
            is NilValue -> Unit
            ExpressionCell.NIL -> Unit
            is ExpressionCell -> toList (cell.cdr, list)
            else -> throw IllegalStateException ("Invalid cdr list list: ${cell.cdr}")
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
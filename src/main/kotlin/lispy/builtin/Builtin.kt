package lispy.builtin

import lispy.*

interface Invokable {
    val symbol: String
    fun invoke (cell: ExpressionCell, interp: Interpreter): Expression
}

abstract class InvokableSupport (override val symbol: String) : Invokable, Expression () {
    override fun toString(): String = "builtin:$symbol"

    fun evalList (cell: ExpressionCell, interp: Interpreter): List<Expression> {
        val list = toList (cell)
        return list.map {
            interp.eval (it)
        }
    }
    fun fromList (list: List<Expression>) : ExpressionCell {
        return if (list.isEmpty ()) {
            ExpressionCell.NIL
        } else {
            var last: Expression = NilValue
            for (i in list.size - 1 downTo 0) {
                last = ExpressionCell (list [i], last)
            }
            last as ExpressionCell
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

object Builtins {
    val ALL = listOf (
        ScopeOp (), EnvOp (), ResetOp (),
        NoopOp (), DumpOp (), QuoteOp (), DefineOp (), DisplayOp (), LambdaOp (),
        AddOp (), MultOp (), SubtractOp (), DivideOp (), ModulusOp (),
        EqualsOp (), LessThanOp (), GreaterThanOp (),
        ListOp (), CarOp (), CdrOp (), ConsOp (), NullOp (),
        CondOp (), IfOp (),
        OrOp (), AndOp (), NotOp ()
    )
    val EXTRAS = buildList {
        addAll (MATH_EXTRAS)
        addAll (LIST_EXTRAS)
        addAll (TEST_EXTRAS)
    }

    class QuoteOp : InvokableSupport("quote") {
        override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression = cell.car as Expression
    }

    class DisplayOp: InvokableSupport ("display") {
        override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
            val list = evalList (cell, interp)
            if (list.size == 1) {
                println (list[0])
            } else {
                println (list)
            }
            return NilValue
        }
    }

    class NoopOp : InvokableSupport("noop") {
        override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression = NilValue
    }

    class DumpOp : InvokableSupport ("dump") {
        override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
            println ("CELL - $cell")
            println ("BRACKETS - ${cell.toBrackets()}")
            val eval = interp.eval (cell)
            if (eval is ExpressionCell) {
                println ("EVAL - ${eval.toBrackets()}")
            }
            return eval
        }
    }
}

private val TEST_EXTRAS = listOf (
    "(define a '(1 2 3 4 5))",
    "(define b '())",
    "(define (length l) (if (null? l) 0 (+ 1 (length (cdr l)))))"
)

// EOF
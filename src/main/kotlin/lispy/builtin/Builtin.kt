package lispy.builtin

import lispy.*


object Builtins {
    val ALL = listOf (
        ScopeOp (), ResetOp (),
        NoopOp (), DumpOp (), QuoteOp (), DefineOp (), DisplayOp (), LambdaOp (), NewlineOp (), BeginOp (),
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
            if (list.size != 1) {
                throw IllegalStateException ("Expected 1 argument found ${list.size}")
            } else {
                print (list[0])
            }
            return NilValue
        }
    }

    class NewlineOp : InvokableSupport ("newline") {
        override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
            val list = expect (cell, 0)
            println ()
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
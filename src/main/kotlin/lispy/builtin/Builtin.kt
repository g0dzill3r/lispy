package lispy.builtin

import lispy.*

interface OpSource {
    val extras: List<String>
    val buildins: List<Invokable>
}

private val TEST_EXTRAS = listOf (
    "(define (length l) (if (null? l) 0 (+ 1 (length (cdr l)))))"
)

private val TEST_BUILTINS = listOf (
    NoopOp::class,
    DumpOp::class,
    QuoteOp::class,
    DisplayOp::class,
    NewlineOp::class
)

object TestBuiltins : OpSource {
    override val extras: List<String>
        get() = TEST_EXTRAS

    override val buildins: List<Invokable>
        get() = instances(TEST_BUILTINS)
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

// EOF
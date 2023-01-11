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
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression = cell.car as Expression

    companion object {
        fun quote (expression: Expression) = ConsPair (Symbol ("quote"), ConsPair (expression))
    }
}

class DisplayOp: InvokableSupport ("display") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val list = evalList (cell, interp, 1)
        interp.buffer.append (list[0])
        return NilValue
    }
}

class NewlineOp : InvokableSupport ("newline") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        expect (cell, 0)
        interp.buffer.append ("\n")
        return NilValue
    }
}

class NoopOp : InvokableSupport("noop") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression = NilValue
}

class DumpOp : InvokableSupport ("dump") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        println ("CELL - $cell")
        val eval = interp.eval (cell)
        println (eval)
        return eval
    }
}

// EOF
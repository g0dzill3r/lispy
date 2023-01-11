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
    NewlineOp::class,
    QuasiquoteOp::class,
    UnquoteOp::class
)

object TestBuiltins : OpSource {
    override val extras: List<String>
        get() = TEST_EXTRAS

    override val buildins: List<Invokable>
        get() = instances(TEST_BUILTINS)
}

class QuoteOp : InvokableSupport (QUOTE) {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression = cell.car as Expression

    companion object {
        val QUOTE = "quote"
        fun quote (expression: Expression) = ConsPair (Symbol ("quote"), ConsPair (expression))
    }
}

class UnquoteOp: InvokableSupport (UNQUOTE) {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val arg = expect (cell, 1)[0]
        return interp.eval (arg)
    }
    companion object {
        val UNQUOTE = "unquote"
    }
}

/**
 * https://docs.racket-lang.org/reference/quasiquote.html
 */

class QuasiquoteOp : InvokableSupport (QUASIQUOTE) {
    private fun eval (pair: ConsPair, interp: Interpreter): Expression {
        val results = mutableListOf<Expression> ()
        pair.toList ().forEach { el ->
            if (el is ConsPair) {
                if (el.car == Symbol (UnquoteOp.UNQUOTE)) {
                    results.add (interp.eval (el))
                } else {
                    results.add (eval ((el as ConsPair), interp))
                }
            } else {
                results.add (el)
            }
        }
        return ConsPair.fromList (results)
    }

    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val list = expect (cell,1)[0] as ConsPair
        return eval (list, interp)
    }
    companion object {
        val QUASIQUOTE = "quasiquote"
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
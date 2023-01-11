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
    UnquoteOp::class,
    UnquoteSplicingOp::class
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

class UnquoteSplicingOp: InvokableSupport (UNQUOTE_SPLICING) {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val arg = expect (cell, 1)[0]
        return interp.eval (arg)
    }
    companion object {
        val UNQUOTE_SPLICING = "unquote-splicing"
    }
}

/**
 * (define x 12345) `(a b ,(list 'c x) ,@(list x x))
 *
 * TODO: A quasiquote form within the original datum increments the level of quasiquotation: within the quasiquote form,
 * each unquote or unquote-splicing is preserved, but a further nested unquote or unquote-splicing escapes.
 * Multiple nestings of quasiquote require multiple nestings of unquote or unquote-splicing to escape.
 *
 * https://docs.racket-lang.org/reference/quasiquote.html
 */

class QuasiquoteOp : InvokableSupport (QUASIQUOTE) {
    private fun eval (pair: ConsPair, interp: Interpreter): Expression {
        val results = mutableListOf<Expression> ()
        val list = pair.toList ()

        list.forEachIndexed { i, el ->
            if (el is ConsPair) {
                if (el.car == Symbol (UnquoteOp.UNQUOTE)) {
                    results.add (interp.eval (el))
                } else if (el.car == Symbol (UnquoteSplicingOp.UNQUOTE_SPLICING)) {
                    val eval = interp.eval (el)
                    if (eval.isList) {
                        results.addAll ((eval as ConsPair).toList ())
                    } else {
                        if (i != list.size - 1) {
                            throw IllegalStateException ("Illegal dotted pair in non-trailing position: $eval")
                        } else {
                            val result = ConsPair.fromList (results)
                            result.last.cdr = eval
                            return result
                        }
                    }
                } else {
                    results.add (eval (el, interp))
                }
            } else {
                results.add (el)
            }
        }
        return ConsPair.fromList (results)
    }

    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val list = expect (cell,1)[0]
        return if (list is ConsPair) {
            eval (list, interp)
        } else {
            list
        }
    }
    companion object {
        val QUASIQUOTE = "quasiquote"
    }
}

class DisplayOp: InvokableSupport ("display") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val list = evalList (cell, interp)
        list.forEachIndexed { i, el ->
            if (i != 0) {
                interp.buffer.append (' ')
            }
            interp.buffer.append (el)
        }
        return NilValue
    }
}

class NewlineOp : InvokableSupport (NEWLINE) {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        expect (cell, 0)
        interp.buffer.append ("\n")
        return NilValue
    }

    companion object {
        val NEWLINE = "newline"
    }
}

class NoopOp : InvokableSupport(NOOP) {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression = NilValue

    companion object {
        val NOOP = "noop"
    }

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
package lispy.builtin

import lispy.*

class ScopeOp : InvokableSupport ("${'$'}scope") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        interp.scope.map.forEach { (key, value) ->
            println ("$key: $value")
        }
        return NilValue
    }
}

/**
 * Resets the state of the interpreter to a clear startup state.
 */

class ResetOp : InvokableSupport ("${'$'}reset") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        interp.reset ()
        return BooleanValue.TRUE
    }
}

/**
 * (define (loop x y λ)
 *   (if (> x y)
 *       nil
 *       (begin (λ x) (loop (inc x) y λ))
 *   )
 * )
 * (loop 1 10 (lambda (x) (display (format "Whee I'm on %d" x)) (newline)))
 */

class FormatOp: InvokableSupport ("format") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        if (eval.isEmpty()) {
            throw IllegalArgumentException ("Expected 1+ arguments; found ${eval.size}")
        }
        val format = requireString (eval[0])
        val args = eval.subList(1, eval.size).map {
            when (it) {
                is FloatValue -> it.value
                is IntValue -> it.value
                is StringValue -> it.value
                is BooleanValue -> it.value
                is Symbol -> it.symbol
                else -> it.toString ()
            }
        }.toTypedArray()
        val formatted = String.format (format.value, *args)
        return StringValue (formatted)
    }
}

// EOF
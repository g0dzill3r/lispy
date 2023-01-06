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

/**
 * (define (factorial n)
 *   (define (iter product counter)
 *     (if (> counter n)
 *       (begin (stack?) product)
 *       (iter (* counter product) (+ counter 1))
 *     )
 *   )
 *   (iter 1 1)
 * )
 * (factorial 5)
 *
 * returns:
 *
 * 7: iter [product=120.0, counter=6]
 * 6: iter [product=24.0, counter=5]
 * 5: iter [product=6.0, counter=4]
 * 4: iter [product=2.0, counter=3]
 * 3: iter [product=1.0, counter=2]
 * 2: iter [product=1, counter=1]
 * 1: factorial [n=5]
 * -> nil
 * -> 120.0
 */

class StackOp : InvokableSupport ("stack?") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        val list = interp.scope.getStack ()
        list.forEachIndexed { i, el ->
            val params = el.params.toList ()
            val operands = el.operands.toList ()
            val arguments = params.mapIndexed { i, v -> "$v=${operands[i]}" }
            println ("${i}: ${if (el.symbol == "lambda") el.lambda else el.symbol} $arguments")
        }
        return NilValue
    }
}

// EOF
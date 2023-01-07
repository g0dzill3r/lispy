package lispy.builtin

import lispy.*

data class ActivationRecord (val symbol: String, val params: ExpressionCell, var operands: ExpressionCell, val lambda: Expression)

/**
 * Data structure for storing non-builtin functions (bound and lambdas).
 */
class BoundFunction (symbol: String, val args: ExpressionCell, val lambda: Expression) : InvokableSupport (symbol) {
    private val argList = buildList {
        args.toList ().map {
            add ((it as Symbol).symbol)
        }
    }

    override fun toString(): String = "(lambda $args $lambda)"

    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {

        // Check the parameters list

        val params = evalList (cell, interp)
        if (params.size != argList.size) {
            throw IllegalArgumentException("Invalid argument count: expected ${argList.size} found ${params.size} in $cell")
        }

        // Include them in the current scope

        val map = mutableMapOf<String, Expression> ()
        params.forEachIndexed { i, value ->
            map.put (argList[i], value)
//            map.put ("_", ActivationRecord (symbol, args, ExpressionCell.fromList (params), lambda))
        }

        closure?.let {
            return interp.scoped (it) {
                interp.scoped (map) {
                    interp.eval (lambda)
                }
            }
        }
        return interp.scoped (map) {
            interp.eval (lambda)
        }
    }

    companion object {
        const val PROCEDURE = "__proc"
        const val FORMAL_PARAMETERS = "__params"
        const val OPERANDS = "__operands"
        const val EXPRESSION = "__expr"
    }
}

/**
 * The builtin operation for defining a lambda function.
 *
 *  repl> (define example (lambda (x) (* x x)))
 *  -> nil
 *  repl> (example 3)
 *  -> 9
 */

class LambdaOp : InvokableSupport ("lambda") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        val args = cell.car as ExpressionCell
        val procs = cell.cdr as ExpressionCell
        val begin = when (procs.length) {
            0 -> throw IllegalStateException ("Expected 1 or expressions, found 0")
            1 -> procs.car
            else -> ExpressionCell (Symbol ("begin"), procs)
        }
        return BoundFunction ("lambda", args, begin).apply {
            closure = interp.scope
        }
    }
}

/**
 * The builtin operation for defining a new value or function.
 *
 *  repl> (define (square x) (* x x))
 *  -> nil
 *  repl> (square 3)
 *  -> 9
 */

class DefineOp : InvokableSupport ("define") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        val type = cell.car

        when (type) {
            is ExpressionCell -> {
                val operator = requireSymbol (type.car)
                val args = if (type.cdr == NilValue) ExpressionCell.NIL else requireExpressionCell(type.cdr)
                val procs = cell.cdr as ExpressionCell
                val begin = when (procs.length) {
                    0 -> throw IllegalStateException ("Expected 1 or expressions, found 0")
                    1 -> procs.car
                    else -> ExpressionCell (Symbol ("begin"), procs)
                }

                val bound = BoundFunction (operator.symbol, args, begin)
                bound.closure = interp.scope
                interp.put (operator, bound)
            }
            is Symbol -> {
                val list = cell.toList ()
                if (list.size != 2) {
                    throw IllegalStateException ("Invalid argument count: ${list.size}")
                }
                val rval = interp.eval (list [1])
                interp.put (type, rval)
            }
            else -> throw IllegalStateException ("Invalid lval type: ${type::class.simpleName}")
        }

        return NilValue
    }
}

/**
 * The begin operation which allows a set of expressions to comprise the body of a procedure. It returns
 * the result of the last evaluation.
 */

class BeginOp : InvokableSupport ("begin") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        return if (eval.isEmpty()) {
            NilValue
        } else {
            eval.last ()
        }
    }
}


/**
 * Introduces lexically scoped symbols. In this variant (Also see [LetRecOp]) prior
 * assignments are not available in subsequent expressions.
 *
 * (let ((x 1) (y 2) (z 3))
 *   (+ x y z)
 * )
 */

class LetOp : InvokableSupport ("let") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        if (cell.length < 2) {
            throw IllegalArgumentException ("Expected 2+ arguments found ${cell.length}")
        }

        // Convert the arguments to a map that we can generate a scope form

        val args = requireExpressionCell (cell.car)
        val map = mutableMapOf<String, Expression> ().apply {
            args.toList().forEach {
                it as ExpressionCell
                if (it.length != 2) {
                    throw IllegalStateException ("Expected 2 elements; found ${it.length}")
                }
                val arg = requireSymbol (it.car)
                val value = interp.eval (requireExpressionCell(it.cdr).car)
                put (arg.symbol, value)
            }
        }

        return interp.scoped (map) {
            val begin = requireExpressionCell (cell.cdr)
            val eval = evalList (begin, interp)
            eval.last ()
        }
    }
}

/**
 *
 */

class LetRecOp : InvokableSupport ("letrec") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        if (cell.length < 2) {
            throw IllegalArgumentException ("Expected 2+ arguments found ${cell.length}")
        }

        // Convert the arguments to a map that we can generate a scope form

        val args = requireExpressionCell (cell.car)
        val map = mutableMapOf<String, Expression> ()

        return interp.scoped (map) {
            args.toList().forEach {
                it as ExpressionCell
                if (it.length != 2) {
                    throw IllegalStateException ("Expected 2 elements; found ${it.length}")
                }
                val arg = requireSymbol (it.car)
                val value = interp.eval (requireExpressionCell (it.cdr).car)
                map[arg.symbol] = value
            }

            val begin = requireExpressionCell (cell.cdr)
            val eval = evalList (begin, interp)
            eval.last ()
        }
    }

}

// EOF
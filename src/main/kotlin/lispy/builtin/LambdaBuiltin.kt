package lispy.builtin

import lispy.*

class BoundFunction (symbol: String, val args: ExpressionCell, val lambda: Expression) : InvokableSupport (symbol) {
    private val argList = buildList {
        toList(args).map {
            add((it as Symbol).symbol)
        }
    }

    override fun toString(): String = "(lambda $args $lambda)"

    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {

        // Check the parameters list

        val params = evalList (cell, interp)
        if (params.size != argList.size) {
            throw IllegalArgumentException("Invalid argument count: expected ${argList.size} found ${params.size}")
        }

        // Include them in the current scope

        val scope = mutableMapOf<String, Any>()
        params.forEachIndexed { i, value ->
            scope.put(argList[i], value)
        }

        return interp.scoped (scope) {
            interp.eval (lambda)
        }
    }
}

class LambdaOp : InvokableSupport ("lambda") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        val list = toList (cell)
        if (list.size != 2) {
            throw IllegalStateException ("Invalid argument count: ${list.size}")
        }

        val args = requireExpressionCell (list[0])
        val rval = list[1]
        return BoundFunction ("lambda", args, rval)
    }
}

/**
 * The builtin operation for defining a new value or function.
 */
class DefineOp : InvokableSupport ("define") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        val list = toList (cell)
        if (list.size != 2) {
            throw IllegalStateException ("Invalid argument count: ${list.size}")
        }

        // See if we're defining a function or just a symbol with a value

        val lval = list [0]

        if (lval is Symbol) {
            val rval = interp.eval (list [1])
            interp.put (lval, rval, true)
        } else if (lval is ExpressionCell) {
            val variable = requireSymbol (lval.car)
            val args = lval.cdr
            toList (args as ExpressionCell).forEach {
                requireSymbol (it)
            }
            val rval = list [1]
            val bound = BoundFunction (variable.symbol, args, rval)
            interp.put (variable, bound, true)
        } else {
            throw IllegalStateException ("Invalid lval type: ${lval::class.simpleName}")
        }

        return NilValue
    }
}

// EOF
package lispy.builtin

import lispy.BooleanValue
import lispy.Expression
import lispy.ExpressionCell
import lispy.Interpreter

class AndOp : InvokableSupport ("and") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        var eval: Expression = BooleanValue.FALSE
        toList (cell).forEach {
            eval = it
            if (it is ExpressionCell) {
                eval = interp.eval (it)
            }
            when (it) {
                is BooleanValue -> {
                    if (it == BooleanValue.FALSE) {
                        return it
                    }
                }
                else -> eval = it
            }
        }
        return eval
    }
}

class OrOp : InvokableSupport ("or") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        toList (cell).forEach {
            val eval = if (it is ExpressionCell) {
                interp.eval (it)
            } else {
                it
            }
            when (eval) {
                is BooleanValue -> {
                    if (eval.value) {
                        return eval
                    }
                }
                else -> return eval
            }
        }
        return BooleanValue.FALSE
    }
}

class NotOp : InvokableSupport ("not") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        val list = evalList (cell, interp)
        if (list.size != 1) {
            throw IllegalStateException ("Expected 1 argument; found ${list.size} in $cell")
        }
        val arg = list[0]
        return when (arg) {
            is BooleanValue -> BooleanValue (! arg.value)
            else -> BooleanValue.FALSE
        }
    }
}

// EOF
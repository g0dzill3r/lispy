package lispy.builtin

import lispy.*

class IfOp : InvokableSupport ("if") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        val list = cell.toList ()
        if (list.size != 3) {
            throw IllegalArgumentException ("Expected 3 arguments found ${list.size} in $cell")
        }
        val result = interp.eval (list[0])
        return if (result == BooleanValue.FALSE) {
            interp.eval (list[2])
        } else {
            interp.eval (list[1])
        }
    }
}

class CondOp : InvokableSupport ("cond") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        cell.toList ().forEach { pair ->
            val sublist = requireExpressionCell (pair).toList ()
            if (sublist.size != 2) {
                throw IllegalArgumentException ("Expected 2 arguments found ${sublist.size} in $sublist")
            }
            val result = interp.eval (sublist [0])
            if (result != BooleanValue.FALSE) {
                return interp.eval (sublist[1])
            }
        }
        return NilValue
    }
}

// EOF
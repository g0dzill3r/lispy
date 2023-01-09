package lispy.builtin

import lispy.*

private val COND_EXTRAS = listOf ("")

private val COND_BUILTINS = listOf (
    CondOp::class,
    IfOp::class
)

object CondBuiltins : OpSource {
    override val extras: List<String>
        get() = COND_EXTRAS

    override val buildins: List<Invokable>
        get() = instances (COND_BUILTINS)
}

/**
 *
 */

class IfOp : InvokableSupport ("if") {
    override fun invoke(cell: Pair, interp: Interpreter): Expression {
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

/**
 *
 */

class CondOp : InvokableSupport ("cond") {
    override fun invoke (cell: Pair, interp: Interpreter): Expression {
        cell.toList ().forEach { pair ->
            val sublist = requirePair (pair).toList ()
            if (sublist.size != 2) {
                throw IllegalArgumentException ("Expected 2 arguments found ${sublist.size} in $sublist")
            }

            val result = if (sublist [0] is Symbol && (sublist [0] as Symbol).symbol == "else") {
                BooleanValue.TRUE
            } else {
                interp.eval (sublist [0])
            }
            if (result != BooleanValue.FALSE) {
                return interp.eval (sublist[1])
            }
        }
        return NilValue
    }
}

// EOF
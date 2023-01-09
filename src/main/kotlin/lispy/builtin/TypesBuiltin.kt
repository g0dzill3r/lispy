package lispy.builtin

import lispy.*


private val TYPE_EXTRAS = listOf (
    "1"
)

private val TYPE_BUILTINS = listOf (
    IsBooleanOp::class,
    IsStringOp::class,
    IsSymbolOp::class,
    IsProcedureOp::class,
    IsNumberOp::class,
    IsVectorOp::class,
    IsCharacterOp::class,
    IsListOp::class,
    IsPairOp::class
)

object TypeBuiltins : OpSource {
    override val extras: List<String>
        get() = TYPE_EXTRAS

    override val buildins: List<Invokable>
        get() = instances (TYPE_BUILTINS)
}

class IsBooleanOp : InvokableSupport ("boolean?") {
    override fun invoke(cell: Pair, interp: Interpreter): Expression = BooleanValue (interp.eval (cell.car) is BooleanValue)
}

class IsStringOp: InvokableSupport ("string?") {
    override fun invoke(cell: Pair, interp: Interpreter): Expression = BooleanValue (interp.eval (cell.car) is StringValue)
}

class IsSymbolOp: InvokableSupport ("symbol?") {
    override fun invoke(cell: Pair, interp: Interpreter): Expression = BooleanValue (interp.eval (cell.car) is Symbol)
}

class IsProcedureOp: InvokableSupport ("procedure?") {
    override fun invoke(cell: Pair, interp: Interpreter): Expression = BooleanValue (interp.eval (cell.car) is Invokable)
}

class IsNumberOp: InvokableSupport ("number?") {
    override fun invoke(cell: Pair, interp: Interpreter): Expression {
        val eval = interp.eval (cell.car)
        return BooleanValue (eval is IntValue || eval is FloatValue)
    }
}

class IsCharacterOp: InvokableSupport ("character?") {
    override fun invoke(cell: Pair, interp: Interpreter): Expression {
        TODO("Unsupported")
    }
}

class IsVectorOp: InvokableSupport ("vector?") {
    override fun invoke(cell: Pair, interp: Interpreter): Expression {
        TODO("Unsupported")
    }
}

class IsPairOp: InvokableSupport ("pair?") {
    override fun invoke(cell: Pair, interp: Interpreter): Expression {
        if (cell.length != 1) {
            throw IllegalArgumentException ("Expected 1 argument found ${cell.length} in ${cell}")
        }
        val eval = interp.eval (cell.car)
        return when (eval) {
            is Pair -> BooleanValue (eval.car != NilValue)
            else -> BooleanValue.FALSE
         }
    }
}

class IsListOp: InvokableSupport ("list?") {
    override fun invoke(cell: Pair, interp: Interpreter): Expression = BooleanValue (interp.eval (cell.car) is Pair)
}
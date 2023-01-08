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
    IsListOp::class
)

object TypeBuiltins : OpSource {
    override val extras: List<String>
        get() = TYPE_EXTRAS

    override val buildins: List<Invokable>
        get() = instances (TYPE_BUILTINS)
}

class IsBooleanOp : InvokableSupport ("boolean?") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression = BooleanValue (interp.eval (cell.car) is BooleanValue)
}

class IsStringOp: InvokableSupport ("string?") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression = BooleanValue (interp.eval (cell.car) is StringValue)
}

class IsSymbolOp: InvokableSupport ("symbol?") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression = BooleanValue (interp.eval (cell.car) is Symbol)
}

class IsProcedureOp: InvokableSupport ("procedure?") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression = BooleanValue (interp.eval (cell.car) is Invokable)
}

class IsNumberOp: InvokableSupport ("number?") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        val eval = interp.eval (cell.car)
        return BooleanValue (eval is IntValue || eval is FloatValue)
    }
}

class IsCharacterOp: InvokableSupport ("character?") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        TODO("Unsupported")
    }
}

class IsVectorOp: InvokableSupport ("vector?") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        TODO("Unsupported")
    }
}

class IsListOp: InvokableSupport ("list?") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression = BooleanValue (interp.eval (cell.car) is ExpressionCell)
}
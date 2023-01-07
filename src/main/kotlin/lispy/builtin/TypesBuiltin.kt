package lispy.builtin

import lispy.*

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
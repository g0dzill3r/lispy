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

class ResetOp : InvokableSupport ("${'$'}reset") {
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
        interp.reset ()
        return BooleanValue.TRUE
    }

}

// EOF
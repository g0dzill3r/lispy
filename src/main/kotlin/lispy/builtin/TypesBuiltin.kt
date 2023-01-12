package lispy.builtin

import lispy.*


private val TYPE_EXTRAS = listOf<String> (

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
    IsPairOp::class,
    TypeOfOp::class
)

object TypeBuiltins : OpSource {
    override val extras: List<String>
        get() = TYPE_EXTRAS

    override val buildins: List<Invokable>
        get() = instances (TYPE_BUILTINS)
}

class IsBooleanOp : InvokableSupport ("boolean?") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 1)[0]
        return BooleanValue (eval is BooleanValue)
    }
}

class IsStringOp: InvokableSupport ("string?") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 1)[0]
        return BooleanValue (eval is StringValue)
    }
}

class IsSymbolOp: InvokableSupport ("symbol?") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 1)[0]
        return BooleanValue (eval is Symbol)
    }
}

class IsProcedureOp: InvokableSupport ("procedure?") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 1)[0]
        return BooleanValue (eval is Invokable)
    }
}

class IsNumberOp: InvokableSupport ("number?") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 1)[0]
        return BooleanValue (eval is IntValue || eval is DoubleValue)
    }
}

class IsCharacterOp: InvokableSupport ("character?") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        throw IllegalStateException ("Unimplemented")
    }
}

class IsVectorOp: InvokableSupport ("vector?") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        throw IllegalStateException ("Unimplemented")
    }
}

class IsPairOp: InvokableSupport ("pair?") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 1)[0]
        return BooleanValue (eval.isPair && (eval as ConsPair).car != NilValue)
    }
}

class IsListOp: InvokableSupport ("list?") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 1)[0]
        return BooleanValue (eval.isList)
    }
}

class TypeOfOp : InvokableSupport ("typeof?") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList(cell, interp, 1)[0]
        val type = when (eval) {
            NilValue -> "nil"
            is BooleanValue -> "boolean"
            is StringValue -> "string"
            is IntValue,
            is DoubleValue -> "number"
            is Symbol -> "symbol"
            is ConsPair -> "pair"
            is Invokable -> "procedure"
            else -> "${eval::class.simpleName}"
        }
        return StringValue (type)
    }

}
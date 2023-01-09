package lispy.builtin

import lispy.BooleanValue
import lispy.Expression
import lispy.Pair
import lispy.Interpreter
import kotlin.reflect.KClass

val BOOLEAN_EXTRAS = listOf (
    "(define true #t)",
    "(define false #f)"
)
val BOOLEAN_BULTINS = listOf (
    AndOp::class, OrOp::class, NotOp::class
)

object BooleanBuiltins : OpSource {
    override val extras: List<String>
        get() = BOOLEAN_EXTRAS

    override val buildins: List<Invokable>
        get() = instances (BOOLEAN_BULTINS)
}

fun <T: Invokable> instances (types: List<KClass<out T>>): List<T> {
    return types.map {
        it.java.getConstructor().newInstance()
    }
}

class AndOp : InvokableSupport ("and") {
    override fun invoke(cell: Pair, interp: Interpreter): Expression {
        var eval: Expression = BooleanValue.FALSE
        cell.toList ().forEach {
            eval = interp.eval (it)
            if (eval == BooleanValue.FALSE) {
                return eval
            }
        }
        return eval
    }
}

class OrOp : InvokableSupport ("or") {
    override fun invoke (cell: Pair, interp: Interpreter): Expression {
        cell.toList ().forEach {
            val eval = if (it is Pair) {
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
    override fun invoke(cell: Pair, interp: Interpreter): Expression {
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
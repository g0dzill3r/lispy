package lispy.builtin

import lispy.*

val MATH_OPS = listOf (
    AddOp::class, MultOp::class, SubtractOp::class, DivideOp::class, ModulusOp::class,
    EqualsOp::class, LessThanOp::class, GreaterThanOp::class, SinOp::class, CosOp::class,
    RandOp::class, SqrtOp::class,
    FloorOp::class, CeilOp::class, RoundOp::class, IntOp::class, FloatOp::class
)

val MATH_EXTRAS = listOf (
    "(define (even? x) (= (% x 2) 0))",
    "(define (odd? x) (= (% x 2) 1))",
    "(define (inc x) (+ x 1))",
    "(define (dec x) (- x 1))",
    "(define (cube x) (* x x x))",
    "(define (square x) (* x x))",
    "(define (>= x y) (or (> x y) (= x y)))",
    "(define (<= x y) (or (< x y) (= x y)))",
    """(define (expt b n)
            (if (= n 0)
                1
                (* b (expt b (- n 1)))))""",
    """(define (factorial n)
            (if (= n 1)
                1
                (* n (factorial (- n 1)))))""",
    "(define (zero? x) (= x 0))",
    "(define (abs x) ((if (< x 0) - +) x))",
    "(define (nonzero? x) (not (zero? x)))",
    "(define (average x y) (/ (+ x y) 2))",
    "(define (average-list L) (/ (sum L) (length L)))",
    """(define (clamp x lo hi)
            (cond ((< x lo) lo)
                ((> x hi) hi)
                (else x)))""",
    """(define (gcd a b) 
      (if (> b a)
        (gcd b a)
        (let ((rem (% a b)))
          (if (= rem 0) 
            b
            (gcd b rem)
          )
        )
      )
    )""",
    // Aliases using unicode characters
    "(define ≤ <=)",
    "(define ≥ >=)",
    "(define ÷ /)",
    "(define (sum L) (fold-right + 0 L))",
    "(define ∑ sum)",
    "(define (∏ L) (fold-right * 1 L))",
    """
        (define (random a b) 
          (->int (floor (+ a (* (rand) (+ 1 (- b a))))))
        )
     """
)

object MathBuiltins: OpSource {
    override val extras: List<String>
        get() = MATH_EXTRAS

    override val buildins: List<Invokable>
        get() = instances (MATH_OPS)
}


abstract class MathSupport (symbol: String) : InvokableSupport (symbol) {
    protected fun coerceList (list: List<Expression>): List<Expression> {
        val intCount = list.count { it is IntValue }
        val floatCount = list.count { it is FloatValue }
        return if (intCount + floatCount != list.size) {
            list.forEachIndexed { i, v -> println ("$i: $v") }
            throw IllegalArgumentException ("Arguments must be int or float values: $list")
        } else if (intCount == list.size) {
            list
        } else {
            list.map {
                if (it is IntValue) {
                    FloatValue (it.value.toFloat())
                } else {
                    it
                }
            }
        }
    }

    protected fun asFloat (e: Expression): FloatValue {
        return when (e) {
            is FloatValue -> e
            is IntValue -> FloatValue (e.value.toFloat ())
            else -> throw IllegalArgumentException ("Not numeric: $e")
        }
    }

    protected fun coerceArgs (cell: ConsPair, interp: Interpreter, expected: Int? = null): List<Expression> {
        val list = evalList (cell, interp)
        if (list.isEmpty ()) {
            throw IllegalArgumentException ("Invalid empty list.")
        }
        if (expected != null) {
            if (list.size != expected) {
                throw IllegalArgumentException ("Invalid argument count; expected $expected, found ${list.size}.")
            }
        }
        val coerced = coerceList (list)
        return coerced
    }
}

class SubtractOp : MathSupport("-") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val coerced = coerceArgs (cell, interp)
        if (coerced.size == 1) {
            val value = coerced[0]
            return when (value) {
                is IntValue -> IntValue (-value.value)
                is FloatValue -> FloatValue (-value.value)
                else -> throw Exception ()
            }
        }

        return if (coerced[0] is IntValue) {
            var total = (coerced[0] as IntValue).value
            for (i in 1 until coerced.size) {
                total -= (coerced[i] as IntValue).value
            }
            IntValue (total)
        } else {
            var total = (coerced[0] as FloatValue).value
            for (i in 1 until coerced.size) {
                total -= (coerced[i] as FloatValue).value
            }
            FloatValue (total)
        }
    }
}

class AddOp : MathSupport("+") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val coerced = coerceArgs (cell, interp)
        return if (coerced[0] is IntValue) {
            var total = 0
            for (i in coerced.indices) {
                total += (coerced[i] as IntValue).value
            }
            IntValue (total)
        } else {
            var total = 0f
            for (i in coerced.indices) {
                total += (coerced[i] as FloatValue).value
            }
            FloatValue (total)
        }
    }
}


class DivideOp : MathSupport("/") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val list = coerceList (evalList(cell, interp))
        if (list.isEmpty ()) {
            throw IllegalArgumentException("Empty list found.")
        }

        val first = list[0]
        return if (first is IntValue) {
            IntValue (list.subList (1, list.size).fold (first.value) { acc, value ->
                value as IntValue
                acc / value.value
            })
        } else {
            val initial = (first as FloatValue).value
            FloatValue (list.subList (1, list.size).fold (first.value) { acc, value ->
                value as FloatValue
                acc / value.value
            })
        }
    }
}

class MultOp : MathSupport("*") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val list = coerceList(evalList(cell, interp))
        if (list.isEmpty()) {
            throw IllegalArgumentException("Empty list found.")
        }

        return if (list[0] is IntValue) {
            IntValue (list.fold (1) { acc, value ->
                value as IntValue
                acc * value.value
            })
        } else {
            FloatValue (list.fold (1.0f) { acc, value ->
                value as FloatValue
                acc * value.value
            })
        }
    }
}

abstract class UnaryOp (symbol: String): MathSupport (symbol) {
    abstract fun op (a: Int): Expression
    abstract fun op (a: Float): Expression

    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val coerced = coerceArgs (cell, interp, 1)
        return if (coerced[0] is IntValue) {
            val value = coerced[0] as IntValue
            op (value.value)
        } else {
            val value = coerced[0] as FloatValue
            op (value.value)
        }
    }
}

abstract class BinaryOp (symbol: String)  : MathSupport (symbol) {
    abstract fun op (a: Int, b: Int): Expression
    abstract fun op (a: Float, b: Float): Expression
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val coerced = coerceArgs (cell, interp, 2)
        return if (coerced[0] is IntValue) {
            val a = coerced[0] as IntValue
            val b = coerced[1] as IntValue
            op (a.value, b.value)
        } else {
            val a = coerced[0] as FloatValue
            val b = coerced[1] as FloatValue
            op (a.value, b.value)
        }
    }

}

class EqualsOp : BinaryOp ("=") {
    override fun op(a: Int, b: Int): Expression = BooleanValue (a == b)
    override fun op(a: Float, b: Float): Expression = BooleanValue (a == b)
}

class LessThanOp : BinaryOp ("<") {
    override fun op(a: Int, b: Int): Expression = BooleanValue (a < b)
    override fun op(a: Float, b: Float): Expression = BooleanValue (a < b)
}

class GreaterThanOp : BinaryOp (">") {
    override fun op(a: Int, b: Int): Expression = BooleanValue (a > b)
    override fun op(a: Float, b: Float): Expression = BooleanValue (a > b)
}

class ModulusOp : BinaryOp ("%") {
    override fun op(a: Int, b: Int): Expression = IntValue (a % b)
    override fun op(a: Float, b: Float): Expression = FloatValue (a % b)
}

class SinOp: UnaryOp ("sin") {
    override fun op(a: Int): Expression = op (a.toFloat ())
    override fun op(a: Float): Expression = FloatValue (Math.sin (a.toDouble ()).toFloat())
}

class CosOp : UnaryOp ("cos") {
    override fun op(a: Int): Expression = op (a.toFloat ())
    override fun op(a: Float): Expression = FloatValue (Math.cos (a.toDouble ()).toFloat ())
}

class RandOp : InvokableSupport ("rand") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        expect (cell, 0)
        return FloatValue(Math.random().toFloat())
    }
}

class SqrtOp: InvokableSupport ("sqrt") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 1)[0]
        return when (eval) {
            is IntValue -> FloatValue (Math.sqrt (eval.value.toDouble ()).toFloat())
            is FloatValue -> FloatValue (Math.sqrt (eval.value.toDouble ()).toFloat())
            else -> throw IllegalArgumentException ("Invalid type: ${eval::class.simpleName} of ${cell.car}")
        }
    }
}

class FloorOp: InvokableSupport ("floor") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val arg = evalList (cell, interp, 1)[0]
        return when (arg) {
            is IntValue -> IntValue (Math.floor (arg.value.toDouble()).toInt())
            is FloatValue -> FloatValue (Math.floor (arg.value.toDouble()).toFloat ())
            else -> throw IllegalArgumentException ("Invalid type: ${arg::class.simpleName} of ${cell.car}")
        }
    }
}

class CeilOp: InvokableSupport ("ceil") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val arg = evalList (cell, interp, 1)[0]
        return when (arg) {
            is IntValue -> IntValue (Math.ceil (arg.value.toDouble()).toInt ())
            is FloatValue -> FloatValue (Math.ceil (arg.value.toDouble()).toFloat())
            else -> throw IllegalArgumentException ("Invalid type: ${arg::class.simpleName} of ${cell.car}")
        }
    }
}

class RoundOp: InvokableSupport ("round") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val arg = evalList (cell, interp, 1)[0]
        return when (arg) {
            is IntValue -> IntValue (Math.round (arg.value.toFloat ()).toInt ())
            is FloatValue -> FloatValue (Math.round (arg.value.toFloat ()).toFloat ())
            else -> throw IllegalArgumentException ("Invalid type: ${arg::class.simpleName} of ${cell.car}")
        }
    }
}

class IntOp: InvokableSupport ("->int") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val arg = evalList (cell, interp, 1)[0]
        return when (arg) {
            is IntValue -> arg
            is FloatValue -> IntValue (arg.value.toInt ())
            else -> throw IllegalArgumentException ("Invalid type: ${arg::class.simpleName} of ${cell.car}")
        }
    }
}

class FloatOp: InvokableSupport ("->float") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val arg = evalList (cell, interp, 1)[0]
        return when (arg) {
            is IntValue -> FloatValue (arg.value.toFloat ())
            is FloatValue -> arg
            else -> throw IllegalArgumentException ("Invalid type: ${arg::class.simpleName} of ${cell.car}")
        }
    }

}

// EOF
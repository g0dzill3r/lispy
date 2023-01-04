package lispy.builtin

import lispy.*


abstract class MathSupport (symbol: String) : InvokableSupport (symbol) {
    protected fun coerceList (list: List<Expression>): List<Expression> {
        val intCount = list.count { it is IntValue }
        val floatCount = list.count { it is FloatValue }
        return if (intCount + floatCount != list.size) {
            list.forEachIndexed { i, v -> println ("$i: $v") }
            throw IllegalArgumentException ("Arguments must be int or float values.")
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

    protected fun coerceArgs (cell: ExpressionCell, interp: Interpreter, expected: Int? = null): List<Expression> {
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
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
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

class DivideOp : MathSupport("/") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        val coerced = coerceArgs (cell, interp)
        return if (coerced[0] is IntValue) {
            var total = (coerced[0] as IntValue).value
            for (i in 1 until coerced.size) {
                total /= (coerced[i] as IntValue).value
            }
            IntValue (total)
        } else {
            var total = (coerced[0] as FloatValue).value
            for (i in 1 until coerced.size) {
                total /= (coerced[i] as FloatValue).value
            }
            FloatValue (total)
        }
    }
}

class AddOp : MathSupport("+") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
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
class MultOp : MathSupport("*") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        val coerced = coerceArgs (cell, interp)

        return if (coerced[0] is IntValue) {
            var total = 1
            for (i in coerced.indices) {
                total *= (coerced[i] as IntValue).value
            }
            IntValue (total)
        } else {
            var total = 1f
            for (i in coerced.indices) {
                total *= (coerced[i] as FloatValue).value
            }
            FloatValue (total)
        }
    }
}

abstract class BinaryOp (symbol: String)  : MathSupport (symbol) {
    abstract fun op (a: Int, b: Int): Expression
    abstract fun op (a: Float, b: Float): Expression
    override fun invoke(cell: ExpressionCell, interp: Interpreter): Expression {
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
    "(define (nonzero? x) (not (zero? x)))",
    """(define (clamp x lo hi)
            (cond ((< x lo) lo)
                ((> x hi) hi)
                (else x)))""",
    "(define ≤ <=)",
    "(define ≥ >=)",
    "(define √ sqrt)",
    "(define ÷ /)"

//    "(define ≤≥√∑∏∀÷ <=)"


)
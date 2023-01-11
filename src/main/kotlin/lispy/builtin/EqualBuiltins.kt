package lispy.builtin

import lispy.*

private val EQUAL_EXTRAS = listOf (
    "#t"
)

private val EQUAL_BUILTINS = listOf (
    EqOp::class,
    EqualOp::class
)

object EqualBuiltins : OpSource {
    override val extras: List<String>
        get() = EQUAL_EXTRAS

    override val buildins: List<Invokable>
        get() = instances (EQUAL_BUILTINS)
}

/**

 */

class EqOp : InvokableSupport ("eq?") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 2)
        val (a, b) = eval
        return BooleanValue (a.equals (b))
    }
}

/**
 * Equal? recursively compares the contents of pairs, vectors, and strings, applying eqv? on
 * other objects such as numbers and symbols. A rule of thumb is that objects are generally equal?
 * if they print the same. Equal? may fail to terminate if its arguments are circular data structures.
 */

class EqualOp : InvokableSupport ("equal?") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 2)
        val (a, b) = eval
        return BooleanValue (isEqual (a, b))
    }

    companion object {
        private fun isEqual (c1: ConsPair, c2: ConsPair): Boolean {
            if (c1.length != c2.length) {
                return false
            }
            var p1 = c1
            var p2 = c2
            while (true) {
                if (! isEqual (p1.car, p2.car)) {
                    return false
                }
                if (p1.cdr is ConsPair) {
                    if (p2.cdr !is ConsPair) {
                        return false
                    }
                    p1 = p1.cdr as ConsPair
                    p2 = p2.cdr as ConsPair
                } else {
                    return isEqual (p1.cdr, p2.cdr)
                }
            }
        }

        fun isEqual (c1: Expression, c2: Expression): Boolean {
            return if (c1::class == c2::class) {
                if (c1 is ConsPair) {
                    isEqual (c1, c2 as ConsPair)
                } else {
                    c1 == c2
                }
            } else if (c1.isNil && c2.isNil) {
                return true
            } else {
                false
            }
        }
    }
}


// EOF
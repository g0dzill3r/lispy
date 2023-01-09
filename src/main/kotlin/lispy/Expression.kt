package lispy

/**
 * The expression is the parent type of all scheme data types.
 */

open class Expression {
    val asString: StringValue
        get () {
            if (this !is StringValue) {
                throw IllegalArgumentException ("Not a string: $this")
            }
            return this
        }

    val asSymbol: Symbol
        get () {
            if (this !is Symbol) {
                throw IllegalArgumentException ("Not a symbol: $this")
            }
            return this
        }

    val asInt: IntValue
        get () {
            if (this !is IntValue) {
                throw IllegalArgumentException ("Not an int: $this")
            }
            return this
        }

    val asPair: Pair
        get () {
            if (this !is Pair) {
                throw IllegalArgumentException ("Not an expression cell: $this")
            }
            return this
        }
}

class Pair (var car: Expression, var cdr: Expression = NilValue) : Expression () {
    val isNil: Boolean
        get () = car == NilValue && cdr == NilValue

    val length: Int
        get () {
            if (car == NilValue) {
                return 0
            }
            var total = 1
            var ptr = this
            while (ptr.cdr is Pair) {
                total ++
                ptr = ptr.cdr as Pair
            }
            return total
        }

    fun toList (list: MutableList<Expression> = mutableListOf ()): List<Expression> {
        if (car != NilValue) {
            list.add (car)
        }
        when (cdr) {
            NIL -> Unit
            is NilValue -> Unit
            is Pair -> (cdr as Pair).toList (list)
            else -> throw IllegalStateException ("Malformed list: $cdr")
        }
        return list
    }

    fun toBrackets (): String {
        return StringBuffer ().apply {
            if (car is NilValue) {
                append ("[$car.$cdr]")
            } else {
                append ("[$car")
                if (cdr !is NilValue) {
                    append (", ")
                    if (cdr is Pair) {
                        append ((cdr as Pair).toBrackets())
                    } else {
                        append (cdr)
                    }
                }
                append ("]")
            }

        }.toString ()
    }

    private fun cdrToString (expr: Expression): String {
        return if (expr is Pair) {
            if (expr.isNil) {
                return ""
            } else {
                return " ${expr.car}${cdrToString(expr.cdr)}"
            }
        } else if (expr is NilValue) {
            return ""
        } else {
            " . $expr"
        }
    }
    override fun toString(): String {
        if (isNil) {
            return ("()")
        }
        return StringBuilder ().apply {
            append ("($car")
            append (cdrToString (cdr))
            append (")")
        }.toString ()
    }

    companion object {
        val NIL = Pair(NilValue, NilValue)

        fun fromList (list: List<Expression>) : Pair {
            return if (list.isEmpty ()) {
                NIL
            } else {
                var last: Expression = NilValue
                for (i in list.size - 1 downTo 0) {
                    last = Pair(list [i], last)
                }
                last as Pair
            }
        }
    }
}

open class Value : Expression ()
object NilValue : Value () {
    override fun toString (): String = "nil"
}
data class IntValue (val value: Int) : Value () {
    override fun toString (): String = "$value"
}
data class FloatValue (val value: Float) : Value () {
    override fun toString (): String = "$value"
}
data class StringValue (val value: String): Value () {
    override fun toString (): String = value
}
data class BooleanValue (val value: Boolean): Value () {
    override fun toString (): String = "$value"

    companion object {
        val TRUE = BooleanValue (true)
        val FALSE = BooleanValue (false)
        fun encode (value: Boolean) = if (value) "#t" else "#f"
        fun decode (encoded: String) = encoded == "#t"
    }
}

data class Symbol (val symbol: String) : Expression () {
    override fun toString (): String = symbol
}

// EOF
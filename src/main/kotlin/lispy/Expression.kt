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

    val asPair: ConsPair
        get () {
            if (this !is ConsPair) {
                throw IllegalArgumentException ("Not an expression cell: $this")
            }
            return this
        }
}

class ConsPair (var car: Expression, var cdr: Expression = NilValue) : Expression () {
    val isNil: Boolean
        get () = car == NilValue && cdr == NilValue

    val length: Int
        get () {
            if (car == NilValue) {
                return 0
            }
            var total = 1
            var ptr = this
            while (ptr.cdr is ConsPair) {
                total ++
                ptr = ptr.cdr as ConsPair
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
            is ConsPair -> (cdr as ConsPair).toList (list)
            else -> throw IllegalStateException ("Malformed list: $cdr")
        }
        return list
    }


    /**
     *
     */

    private fun cdrToString (expr: Expression, seen: MutableSet<ConsPair>): String {
        return if (expr is ConsPair) {
            if (expr.isNil) {
                ""
            } else if (seen.contains (expr)) {
                " ●"
            } else if (expr.car is ConsPair) {
                StringBuffer ().apply {
                    append (' ')
                    if (seen.contains (expr.car)) {
                        append ("(●)")
                    } else {
                        append (cdrToString (expr.car, seen))
                    }
                    append (cdrToString (expr.cdr, seen))
                }.toString ()
            } else {
                StringBuffer ().apply {
                    append (' ')
                    append (expr.car)
                    append (cdrToString (expr.cdr, seen))
                }.toString ()
            }
        } else if (expr is NilValue) {
            ""
        } else {
            StringBuffer ().apply {
                append (" . ")
                if (cdr is ConsPair) {
                    append ((cdr as ConsPair).toString (seen))
                } else {
                    append (cdr)
                }
            }.toString ()
        }
    }

    fun toString (seen: MutableSet<ConsPair>): String {
        return if (seen.contains (this)) {
            "(●)"
        } else if (isNil) {
            "()"
        } else {
            seen.add (this)
            StringBuilder ().apply {
                append ("(")
                if (car is ConsPair) {
                    append ((car as ConsPair).toString (seen))
                } else {
                    append (car)
                }
                append (cdrToString (cdr, seen))
                append (")")
            }.toString ()
        }
    }

    override fun toString (): String = toString (mutableSetOf ())

    companion object {
        val NIL = ConsPair(NilValue, NilValue)

        fun fromList (list: List<Expression>) : ConsPair {
            return if (list.isEmpty ()) {
                NIL
            } else {
                var last: Expression = NilValue
                for (i in list.size - 1 downTo 0) {
                    last = ConsPair(list [i], last)
                }
                last as ConsPair
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
package lispy

/**
 * The expression is the parent type of all scheme data types.
 */

abstract class Expression {
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

    val isNil: Boolean
        get () = this == NilValue || this == ConsPair.NIL

    val isNotNil: Boolean
        get () = ! isNil

    val isPair: Boolean
        get () = this is ConsPair

    val isList: Boolean
        get () {
            return if (this is ConsPair) {
                if (cdr == NilValue) {
                    true
                } else if (cdr is ConsPair) {
                    cdr.isList
                } else {
                    false
                }
            } else {
                false
            }
        }
}

class ConsPair (var car: Expression, var cdr: Expression = NilValue) : Expression () {
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
     * A [toString] implementation that is able to defeat circular structures and yield a
     * string representation of structures.
     */

    fun toString (seen: MutableList<ConsPair>): String {
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
                var ptr = cdr
                while (true) {
                    if (ptr == NilValue) {
                        break
                    } else if (ptr !is ConsPair) {
                        append (" . ")
                        append (ptr)
                        break
                    } else if (seen.contains (ptr)) {
                        append (" (●)")
                        break
                    } else {
                        seen.add (ptr)
                        append (" ")
                        if (ptr.car is ConsPair) {
                            append ((ptr.car as ConsPair).toString (seen))
                        } else {
                            append (ptr.car)
                        }
                        ptr = ptr.cdr
                    }
                }
                append (")")
            }.toString ()
        }
    }

    override fun toString (): String = toString (mutableListOf ())

    /**
     * Displays the physical structure of a list using the tradditional dotted notation.
     */

    fun toDottedString (): String {
        return StringBuffer ().apply {
            append ("(")
            if (car is ConsPair) {
                append ((car as ConsPair).toDottedString ())
            } else {
                append (car)
            }
            append (" . ")
            if (cdr is ConsPair) {
                append ((cdr as ConsPair).toDottedString ())
            } else {
                append (cdr)
            }
            append (")")
        }.toString ()
    }

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

abstract class Value : Expression ()

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
    override fun toString (): String = "\"$value\""
}
data class BooleanValue (val value: Boolean): Value () {
    override fun toString (): String = if (value) "#t" else "#f"

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
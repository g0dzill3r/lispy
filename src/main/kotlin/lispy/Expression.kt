package lispy

/**
 * The expression is the parent type of all scheme data types.
 */

open class Expression
class ExpressionCell (val car: Expression, val cdr: Expression = NilValue) : Expression () {
    val isNil: Boolean
        get () = car == NilValue && cdr == NilValue

    fun toBrackets (): String {
        return StringBuffer ().apply {
            if (car is NilValue) {
                append ("[$car.$cdr]")
            } else {
                append ("[$car")
                if (cdr !is NilValue) {
                    append (", ")
                    if (cdr is ExpressionCell) {
                        append (cdr.toBrackets())
                    } else {
                        append (cdr)
                    }
                }
                append ("]")
            }

        }.toString ()
    }

    private fun cdrToString (expr: Expression): String {
        return if (expr is ExpressionCell) {
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
        val NIL = ExpressionCell (NilValue, NilValue)
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
    override fun toString (): String = "\"$value\""
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


fun main() {
    val a = ExpressionCell (StringValue ("a"), StringValue ("b"))
    println (a)
    println (a.toBrackets())

    val b = ExpressionCell (IntValue (1), ExpressionCell (IntValue (2)))
    println (b)
    println (b.toBrackets())

    val c = ExpressionCell (IntValue (1), ExpressionCell (IntValue (2), ExpressionCell (IntValue (3))))
    println (c)
    println (c.toBrackets())

    val d = ExpressionCell (ExpressionCell (IntValue (1), ExpressionCell (IntValue (2))), IntValue (3))
    println (d)
    println (d.toBrackets ())

    val e = ExpressionCell (ExpressionCell (IntValue (1), ExpressionCell (IntValue (2))))
    println (e)
    println (e.toBrackets ())
    return
}
package lispy.test

import lispy.Expression
import lispy.ExpressionCell
import lispy.IntValue
import lispy.NilValue

private val make = { e1: Expression, e2: Expression -> ExpressionCell (e1, e2) }

private fun show (e: ExpressionCell) {
    println ("=======")
    println (e)
    println (e.toBrackets())
    return
}

fun main() {
    make (IntValue (1), NilValue).let {
        show (it)
    }

    val a = make (NilValue, NilValue)
    show (a)

    val b =  make (IntValue (1), make (IntValue (2), make (IntValue (3), NilValue)))
    show (b)

    val c = make (a, make (a, make (a, NilValue)))
    show (c)

    return
}

// EOF
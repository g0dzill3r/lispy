package lispy.test

import lispy.*

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

    run {
        val a = ExpressionCell(StringValue("a"), StringValue("b"))
        println(a)
        println(a.toBrackets())

        val b = ExpressionCell(IntValue(1), ExpressionCell(IntValue(2)))
        println(b)
        println(b.toBrackets())

        val c = ExpressionCell(IntValue(1), ExpressionCell(IntValue(2), ExpressionCell(IntValue(3))))
        println(c)
        println(c.toBrackets())

        val d = ExpressionCell(ExpressionCell(IntValue(1), ExpressionCell(IntValue(2))), IntValue(3))
        println(d)
        println(d.toBrackets())

        val e = ExpressionCell(ExpressionCell(IntValue(1), ExpressionCell(IntValue(2))))
        println(e)
        println(e.toBrackets())
    }

    run {
        val e1 = ExpressionCell.NIL
        println ("$e1: ${e1.length}")

        val e2 = ExpressionCell (Symbol ("a"))
        println ("$e2: ${e2.length}")

        val e3 = ExpressionCell (Symbol ("b"), e2)
        println ("$e3: ${e3.length}")
    }

    return
}

// EOF
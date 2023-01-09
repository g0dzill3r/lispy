package lispy.test

import lispy.*

private val make = { e1: Expression, e2: Expression -> Pair(e1, e2) }

private fun show (e: Pair) {
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
        val a = Pair(StringValue("a"), StringValue("b"))
        println(a)
        println(a.toBrackets())

        val b = Pair(IntValue(1), Pair(IntValue(2)))
        println(b)
        println(b.toBrackets())

        val c = Pair(IntValue(1), Pair(IntValue(2), Pair(IntValue(3))))
        println(c)
        println(c.toBrackets())

        val d = Pair(Pair(IntValue(1), Pair(IntValue(2))), IntValue(3))
        println(d)
        println(d.toBrackets())

        val e = Pair(Pair(IntValue(1), Pair(IntValue(2))))
        println(e)
        println(e.toBrackets())
    }

    run {
        val e1 = Pair.NIL
        println ("$e1: ${e1.length}")

        val e2 = Pair(Symbol ("a"))
        println ("$e2: ${e2.length}")

        val e3 = Pair(Symbol ("b"), e2)
        println ("$e3: ${e3.length}")
    }

    return
}

// EOF
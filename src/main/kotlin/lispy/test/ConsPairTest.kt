package lispy.test

import lispy.*
import lispy.ConsPair

private val make = { e1: Expression, e2: Expression -> ConsPair(e1, e2) }

private fun show (e: ConsPair) {
    println ("=======")
    println (e)
    return
}

fun main() {
//    make (IntValue (1), NilValue).let {
//        show (it)
//    }
//
//    run {
//        val a = make(NilValue, NilValue)
//        show(a)
//
//        val b = make(IntValue(1), make(IntValue(2), make(IntValue(3), NilValue)))
//        show(b)
//
//        val c = make(a, make(a, make(a, NilValue)))
//        show(c)
//    }
//
//    run {
//        val a = ConsPair(StringValue("a"), StringValue("b"))
//        println(a)
//
//        val b = ConsPair(IntValue(1), ConsPair(IntValue(2)))
//        println(b)
//
//        val c = ConsPair(IntValue(1), ConsPair(IntValue(2), ConsPair(IntValue(3))))
//        println(c)
//
//        val d = ConsPair(ConsPair(IntValue(1), ConsPair(IntValue(2))), IntValue(3))
//        println(d)
//
//        val e = ConsPair(ConsPair(IntValue(1), ConsPair(IntValue(2))))
//        println(e)
//    }
//
//    run {
//        val e1 = ConsPair.NIL
//        println ("$e1: ${e1.length}")
//
//        val e2 = ConsPair(Symbol ("a"))
//        println ("$e2: ${e2.length}")
//
//        val e3 = ConsPair(Symbol ("b"), e2)
//        println ("$e3: ${e3.length}")
//    }

    run {
        println ("CYCLIC")
        val a = ConsPair (LongValue (1), NilValue)
        val b = ConsPair (LongValue (2), a)
        val c = ConsPair (LongValue (3), b)
        val d = ConsPair (LongValue (4), c)
//        a.cdr = d
//        show (d)

        a.cdr = d
        show (d)

        c.car = d
        println (d)

        c.cdr = d
        println (d)
    }

    return
}

// EOF
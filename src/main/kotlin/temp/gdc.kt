package temp

import lispy.interpreter

fun gcd (a: Int, b: Int): Int {
    return if (b > a) {
        gcd (b, a)
    } else {
        val r = a % b
        if (r == 0) {
            b
        } else {
            gcd (b, r)
        }
    }
}

fun sd (a: Int): Int {
    for (i in 2 until a) {
        if (a % i == 0) {
            return i
        }
    }
    return a
}

fun main() {
    interpreter ("gcd> ") {
        val els = it.split (" ")
        if (els.isNotEmpty()) {
//            if (els.size == 2) {
//                val (a, b) = els.map { it.toInt () }
//                println (gcd (a, b))
            if (els.size == 1) {
                val (a) = els.map { it.toInt () }
                println (sd (a))
            } else {
                println("ERROR: Too many arguments.")
            }
        }

        true
    }
}

// EOF
package lispy.test

import lispy.ConsPair
import lispy.LongValue

fun main() {
    val cell = ConsPair (LongValue (1))
    cell.cdr = cell
    println (System.identityHashCode (cell))
    println (cell.hashCode ())
    println (cell)
    return
}
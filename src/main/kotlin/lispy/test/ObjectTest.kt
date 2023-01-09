package lispy.test

import lispy.ConsPair
import lispy.IntValue

fun main() {
    val cell = ConsPair (IntValue (1))
    cell.cdr = cell
    println (System.identityHashCode (cell))
    println (cell.hashCode ())
    println (cell)
    return
}
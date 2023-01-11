package lispy.test

import lispy.ConsPair
import lispy.ProviderFactory

private val TESTS = listOf (
    "#t",
    "#f",
    "true",
    "false",
    "1",
    "2.345",
    "(- 1)",
    "(- 1.234)",
    "\"string\"",
    "symbol",
    "'symbol",
    "nil",
    "()",
    "(1)",
    "(1 2 3)",
    "(1 . 2)",
    "(1 . (2 . (3 . (4 . nil))))",
    "(1 . (2 . (3 . (4 . ()))))",
    "((1 2) . (1 2))",
    "((1 . 2) . (1 . 2))",
    "((1 . (1 2)) . (1 . (1 2)))",
    "((1 . (1 . 2)) . (1 . (1 . 2)))",
    "(1 (1 2) ((1 2) 3) 1 2 ())",
    "(((())))",
    "(((1)))"
)

fun main() {
    val provider = ProviderFactory.getProvider()
    TESTS.forEach {
        val e = provider.parser.parse (it)
        val same = if (it == e.toString()) " " else "*"
        val pair = if (e is ConsPair) ((e as ConsPair).toDottedString()) else ""
        println (String.format ("%50s  %s  %50s  %60s", it, same, e, pair))
    }
    return
}
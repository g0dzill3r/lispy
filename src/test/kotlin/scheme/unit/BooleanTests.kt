package scheme.unit

typealias T = ExpressionTest

object BooleanTests : ExpressionTests {
    override val tests: List<ExpressionTest> = listOf<ExpressionTest> (
        T ("#t", "#t"),
        T ("true", "#t"),
        T ("#f", "#f"),
        T ("false", "#f"),
        // and tests
        T ("(and)", "#f"),
        T ("(and #f)", "#f"),
        T ("(and #f #f)", "#f"),
        T ("(and #f  #f #t)", "#f"),
        T ("(and #f #f 1)", "#f"),
        T ("(and #f #f 1.1)", "#f"),
        T ("(and #f #f \"a\")", "#f"),
        T ("(and #f #f 'symbol)", "#f"),
        T ("(and #f #f '())", "#f"),
        T ("(and #f #f '(1))", "#f"),
        T("(and #t 1)", "1"),
        T("(and #t 1.1)", "1.1"),
        T("(and #t '())", "'()"),
        T("(and #t '(1))", "'(1)"),
        T("(and #t '(1 2 3))", "'(1 2 3)"),
        T("(and #t '(1 . 2))", "'(1 . 2)"),
        T("(and #t \"a\")", "\"a\""),
        T("(and #t 'symbol)", "'symbol"),
        T("(and #t square)", "square"),
        T("(and #t #t)", "#t"),
        // or tests
        T ("(or)", "#f"),
        T ("(or #f)", "#f"),
        T ("(or #f #f)", "#f"),
        T ("(or #f #f #t)", "#t"),
        T ("(or #f #f (undefined))", "1", true),
        T ("(or #f #t (undefined))", "#t"),
        T ("(or #f 1 (undefined))", "1"),
        T ("(or #f 2.3 (undefined))", "2.3"),
        T ("(or #f \"a\" (undefined))", "\"a\""),
        T ("(or #f square (undefined))", "square"),
        T ("(or #f '() (undefined))", "'()"),
        T ("(or #f '(1 2 3) (undefined))", "'(1 2 3)"),
        // not tests
        T ("(not)", "1", true),
        T ("(not #f)", "#t"),
        T ("(not #t)", "#f"),
        T ("(not 1)", "#f"),
        T ("(not 1.1)", "#f"),
        T ("(not not)", "#f"),
        T ("(not 'foo)", "#f"),
        T ("(not \"a\")", "#f"),
        T ("(not '(1 2 3))", "#f"),
        T ("(not nil)", "#f"),
        T ("(not (not #t))", "#t"),
        T ("(not (not #f))", "#f")
    )
}

fun main() {
    val tester = ExpressionTester (true)
    // All tests
    tester.runTests (BooleanTests.tests)

    // Single test
//    val test = T("(and #t and)", "and")
//    val result = tester.runTest (test)
//    println (result)

    // Dump tests
//    tester.dump (BooleanTests.tests)
    return
}

// EOF
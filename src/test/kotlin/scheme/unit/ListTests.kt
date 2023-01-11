package scheme.unit

object ListTests: ExpressionTests {
    override val tests: List<ExpressionTest> = listOf<ExpressionTest> (
        // ListOp
        T ("(list)", "'()"),
        T ("(list 1)", "'(1)"),
        T ("(list 1 2 3)", "'(1 2 3)"),
        T ("(list 1 '(2 3) #t #f)", "'(1 (2 3) #t #f)"),
        // CarOp
        T ("(car '())", "nil"),
        T ("(car '(1))", "1"),
        T ("(car '(1.1))", "1.1"),
        T ("(car '(a))", "'a"),
        T ("(car '((1)))", "'(1)"),
        T ("(car '(1 2 3 4 5))", "1"),
        T ("(car (cdr (cdr '(1 2 3 4 5))))", "3"),
        // CdrOp
        T ("(cdr '())", "nil"),
        T ("(cdr '(1))", "nil"),
        T ("(cdr '(1 2 3))", "'(2 3)"),
        // ConsOp
        T ("(cons 1 '())", "'(1)"),
        T ("(cons 1 2)", "'(1 . 2)"),
        T ("(cons 1 '(2 3))", "'(1 2 3)"),
        // NullOp
        T ("(null?)", "1", true),
        T ("(null? nil)", "#t"),
        T ("(null? '())", "#t"),
        T ("(null? '(1))", "#f"),
        T ("(null? 1)", "#f"),
        T ("(null? 1.1)", "#f"),
        T ("(null? \"a\")", "#f"),
        T ("(null? square)", "#f"),
        T ("(null? #t)", "#f"),
        T ("(null? #f)", "#f"),
        // AppendOp
        // SetCarOp
        // SetCdrOp
        // ListTailOp
        // ListRefOp
    )
}

fun main() {
    val tester = ExpressionTester ()
    tester.runTests (ListTests.tests)
    return
}

// EOF
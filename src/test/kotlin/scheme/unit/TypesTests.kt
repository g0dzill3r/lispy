package scheme.unit

object TypesTests: ExpressionTests {
    override val tests: List<ExpressionTest> = listOf<ExpressionTest> (
        // boolean?
        T("(boolean?)", "1", true),
        T("(boolean? #t)", "#t"),
        T("(boolean? #f)", "#t"),
        T("(boolean? nil)", "#f"),
        T("(boolean? '())", "#f"),
        T("(boolean? 1)", "#f"),
        T("(boolean? 2.3)", "#f"),
        T("(boolean? \"a\")", "#f"),
        T("(boolean? square)", "#f"),
        T("(boolean? '(1 2 3))", "#f"),
        T("(boolean? '(1 . 3))", "#f"),
        // string?
        T("(string?)", "1", true),
        T("(string? #t)", "#f"),
        T("(string? #f)", "#f"),
        T("(string? nil)", "#f"),
        T("(string? '())", "#f"),
        T("(string? '(1))", "#f"),
        T("(string? '(1 2 3))", "#f"),
        T("(string? '(1 . 3))", "#f"),
        T("(string? 1)", "#f"),
        T("(string? 2.3)", "#f"),
        T("(string? \"a\")", "#t"),
        T("(string? string?)", "#f"),
        // symbol?
        T("(symbol?)", "1", true),
        T("(symbol? #f)", "#f"),
        T("(symbol? #t)", "#f"),
        T("(symbol? nil)", "#f"),
        T("(symbol? '())", "#f"),
        T("(symbol? '(1))", "#f"),
        T("(symbol? '(1 2))", "#f"),
        T("(symbol? '(1 . 2))", "#f"),
        T("(symbol? 1)", "#f"),
        T("(symbol? 2.3)", "#f"),
        T("(symbol? \"a\")", "#f"),
        T("(symbol? square)", "#f"),
        T("(symbol? 'square)", "#t"),
        // procedure?
        T("(procedure?)", "1", true),
        T("(procedure? #f)", "#f"),
        T("(procedure? #t)", "#f"),
        T("(procedure? nil)", "#f"),
        T("(procedure? '())", "#f"),
        T("(procedure? '(1))", "#f"),
        T("(procedure? '(1 2))", "#f"),
        T("(procedure? '(1 . 2))", "#f"),
        T("(procedure? 1)", "#f"),
        T("(procedure? 2.3)", "#f"),
        T("(procedure? \"a\")", "#f"),
        T("(procedure? square)", "#t"),
        T("(procedure? 'square)", "#f"),
        // number?
        T("(number?)", "1", true),
        T("(number? #f)", "#f"),
        T("(number? #t)", "#f"),
        T("(number? nil)", "#f"),
        T("(number? '())", "#f"),
        T("(number? '(1))", "#f"),
        T("(number? '(1 2))", "#f"),
        T("(number? '(1 . 2))", "#f"),
        T("(number? 1)", "#t"),
        T("(number? 2.3)", "#t"),
        T("(number? square)", "#f"),
        T("(number? 'square)", "#f"),
        T("(number? \"a\")", "#f"),
        // vector? - UNIMPLEMENTED
        T ("(vector?)", "#t", true),
        // character? - UNIMPlEMENTED
        T ("(character?)", "#t", true),
        // list?
        T("(list?)", "1", true),
        T("(list? #t)", "#f"),
        T("(list? #f)", "#f"),
        T("(list? 1)", "#f"),
        T("(list? 2.3)", "#f"),
        T("(list? \"a\")", "#f"),
        T("(list? square)", "#f"),
        T("(list? 'square)", "#f"),
        T("(list? '())", "#t"),
        T("(list? '(1))", "#t"),
        T("(list? '(2 3))", "#t"),
        T("(list? '(1 . 2))", "#f"),
        T("(list? nil)", "#t"),
        // pair?
        T("(pair?)", "1", true),
        T("(pair? #t)", "#f"),
        T("(pair? #f)", "#f"),
        T("(pair? 1)", "#f"),
        T("(pair? 2.3)", "#f"),
        T("(pair? 'a)", "#f"),
        T("(pair? square)", "#f"),
        T("(pair? '())", "#f"),
        T("(pair? '(1))", "#t"),
        T("(pair? '(1 . 2))", "#t"),
        T("(pair? '(1 2 3))", "#t"),
        T ("(pair? nil)", "#f")
    )
}

fun main() {
    val tester = ExpressionTester ()
    tester.runTests (TypesTests.tests)

//    val res = tester.runTest (T ("(pair? nil)", "#f"))
//    println (res)
    return
}


// EOF
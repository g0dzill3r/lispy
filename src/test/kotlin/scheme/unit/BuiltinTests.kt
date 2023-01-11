package scheme.unit

object BuiltinTests: ExpressionTests {
    override val tests: List<ExpressionTest> = listOf<ExpressionTest>(
        // NoopOp
        T ("(noop)", "nil"),
        T ("(noop 1)", "nil"),
        // DumpOp
        // QuoteOp
        T ("'()", "'()"),
        T ("'(1)", "'(1)"),
        T ("'(1 2 3)", "'(1 2 3)"),
        T ("'(1 . 2)", "'(1 . 2)"),
        T ("'#t", "'#t"),
        T ("'#f", "'#f"),
        T ("'1", "'1"),
        T ("'1.2", "'1.2"),
        T ("'\"a\"", "'\"a\""),
        T ("'a", "'a"),
        T ("'(+ 1 (- 2 3) (* 2 3))", "'(+ 1 (- 2 3) (* 2 3))"),
        // DisplayOp
        T ("(display)", "nil"),
        T ("(display 1)", "nil"),
        T ("(display 1 2 3)", "nil"),
        // NewlineOp
        T ("(newline)", "nil"),
        T ("(newline 1)", "nil", true),
        // QuasiquoteOp
        T ("`()", "`()"),
        T ("`(1)", "`(1)"),
        T ("`(1 (2 3) 4)", "`(1 (2 3) 4)"),
        T ("`1", "`1"),
        T ("`1.1", "`1.1"),
        T ("`\"a\"", "`\"a\""),
        T ("`a", "`a"),
        T ("`#t", "`#t"),
        T ("`#f", "`#f"),
        // UnquoteOp
        T ("`(1 ,(+ 1 1) 3)", "'(1 2 3)"),
        T ("(define x 123) `(1 ,x (+ x x) ,(+ x x))", "(list 1 123 '(+ x x) 246)"),
        T ("(quasiquote (0 1 2))", "'(0 1 2)"),
        T ("(quasiquote (0 (unquote (+ 1 2)) 4))", "'(0 3 4)"),
        T ("`(0 1 2)", "'(0 1 2)"),
        T ("`(1 ,(+ 1 2) 4)", "'(1 3 4)"),
//        T ("`(1 `,(+ 1 ,(+ 2 3)) 4)", "'(1 `,(+ 1 5) 4)"),        BROKEN
//        T ("`(1 ```,,@,,@(list (+ 1 2)) 4)", "'(1 ```,,@,3 4)"),  BROKEN
        // UnquoteSplicingOp
        T ("(quasiquote (0 (unquote-splicing (list 1 2)) 4))","'(0 1 2 4)"),
        T ("(quasiquote (0 (unquote-splicing 1)))", "'(0 . 1)"),
        T ("`(0 ,@(list 1 2) 4)", "'(0 1 2 4)"),
        T ("`(0 ,@'(1 2) 4)", "'(0 1 2 4)"),
        T ("`(0 ,@1)", "'(0 . 1)"),
        T ("`(0 ,@(list 1))", "'(0 1)"),
        T ("(let ((ls '(a b c))) `(func ,@ls))", "'(func a b c)"),
        T ("(define (foo x y z) (+ x y z)) (define a '(1 2 3)) ,,,,,,`(foo ,@a)", "'(foo 1 2 3)"),   // CORRECT, NOT SURE?
        T ("(define (eval-with-context ctx expr) (eval `(let ,ctx ,expr) (environment '(rnrs))))", "nil")
    )
}

//fun main() = ExpressionTester.runAll (BuiltinTests.tests)
fun main() = ExpressionTester.dumpAll (BuiltinTests.tests)

// EOF
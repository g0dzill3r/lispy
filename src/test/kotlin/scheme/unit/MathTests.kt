package scheme.unit


object MathTests: ExpressionTests {
    override val tests: List<ExpressionTest> = listOf<ExpressionTest> (
        // AddOp
        T ("(+)", "1", true),
        T ("(+ 0)", "0"),
        T ("(+ 1 2 3)", "6"),
        T ("(+ (+ 1 1) (+ 1 1))", "4"),
        T ("(+ 1.0 1)", "2.0"),
        T ("(+ 1 1.0)", "2.0"),
        // MultOp
        // SubtractOp
        T ("(-)", "1", true),
        T ("(- 1)", "(- 1)"),
        T ("(- 10 5)", "5"),
        T ("(- 10 1 1 1 1 1)", "5"),
        T ("(- 10 5.0)", "5.0"),
        T ("(- 10.0 5)", "5.0"),
        // DivideOp
        T("(/)", "1", true),
        T("(/ 1)", "1"),
        T("(/ 10 5)", "2"),
        T("(/ 100 10 5)", "2"),
        T("(/ 1.0)", "1.0"),
        T("(/ 10 5.0)", "2.0"),
        T("(/ 10.0 5)", "2.0"),
        T("(/ 10.0 5.0)", "2.0"),
        // ModulusOp
        T("(%)", "1", true),
        T("(% 10)", "1", true),
        T("(% 10 2)", "0"),
        T("(% 10 3)", "1"),
        T("(% 10 -2)", "0"),
        T("(% 10 -3)", "1"),
        // EqualsOp
        T("(=)", "1", true),
        T("(= 1)", "1", true),
        T("(= 1 1 1)", "1", true),
        T ("(= 1 1)", "#t"),
        T ("(= 1 1.0)", "#t"),
        T ("(= 1.0 1.0)", "#t"),
        T ("(= 1 2)", "#f"),
        T ("(= 1 1.1)", "#f"),
        T ("(= 1.0 1.1)", "#f"),
        T ("(= 'a 'a)", "1", true),
        T ("(= $#t #t)", "1", true),
        // LessThanOp
        // GreaterThanOp
        // SinOp
        // CosOp
        // RandOp
        // SqrtOp
        // FloorOp
        // CeilOp
        // RoundOp
        // IntOp
        T ("(->int)", "1", true),
        T ("(->int 1 2)", "1", true),
        T ("(->int 1)", "1"),
        T ("(->int -1)", "-1"),
        T ("(->int 1.33333333)", "1"),
        T ("(->int -1.333333333)", "-1"),
        // FloatOp
        T ("(->double)", "1", true),
        T ("(->double 1 2)", "1", true),
        T ("(->double 1)", "1.0"),
        T ("(->double -1)", "-1.0"),
        T ("(->double 1.33333333)", "1.33333333"),
        T ("(->double -1.333333333)", "-1.333333333"),
    )
}
fun main() = ExpressionTester.runAll (MathTests.tests)

// EOF
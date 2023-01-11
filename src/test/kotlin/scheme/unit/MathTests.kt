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
        // ModulusOp
        // EqualsOp
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
        // FloatOp
    )
}
fun main() {
    val tester = ExpressionTester ()
    tester.runTests (MathTests.tests)
    return
}

// EOF
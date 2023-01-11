package scheme.unit

private val ALL_TESTS = buildList<ExpressionTest> {
    addAll (BooleanTests.tests)
    addAll (ListTests.tests)
    addAll (TypesTests.tests)
    addAll (MathTests.tests)
    addAll (CondTests.tests)
}

fun main() {
    val tester = ExpressionTester ()
    tester.runTests (ALL_TESTS)
    return
}

// EOF
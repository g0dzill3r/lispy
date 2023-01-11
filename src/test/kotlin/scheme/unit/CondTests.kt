package scheme.unit

object CondTests: ExpressionTests {
    override val tests: List<ExpressionTest> = listOf<ExpressionTest> (

    )
}

fun main() {
    val tester = ExpressionTester ()
    tester.runTests (MathTests.tests)
    return
}
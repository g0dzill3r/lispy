package scheme.unit


data class ExpressionTest (val expression: String, val result: String, val isException: Boolean = false)

interface ExpressionTests {
    val tests: List<ExpressionTest>
}

// EOF
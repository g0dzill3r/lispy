package scheme.unit

import lispy.*
import lispy.builtin.EqualOp

class ExpressionTester (val debug: Boolean = false) {
    val provider = ProviderFactory.getProvider ()
    val interp = Interpreter (provider)

    fun dump (test: ExpressionTest) = println ("${test.expression} ;; ${if (test.isException) "EXCEPTION" else { test.result }}")
    fun dump (tests: List<ExpressionTest>) = tests.forEach { dump (it) }

    /**
     *
     */

    fun runTests (tests: List<ExpressionTest>): Pair<Int, Int> {
        var total = 0
        var success = 0

        for (test in tests) {
            total++
            val result = runTest (test)
            if (result.success) {
                success++
            } else {
                println (result)
            }
        }

        println("$success tests of $total succeeded.")
        return Pair(success, total)
    }

    /**
     * The result of running a test.
     */

    data class ExpressionTestResult (
        val test: ExpressionTest,
        val success: Boolean,
        val input: List<Expression>? = null,
        val expected: Expression? = null,
        val output: Expression? = null,
        val exception: Throwable? = null
    ) {
        override fun toString(): String {
            fun render (e: Expression?): String {
                return stringBuilder {
                    if (e == null) {
                        append ("null")
                    } else {
                        append (e.toString())
                        if (e is ConsPair) {
                            append (" ")
                            append ((e as ConsPair).toDottedString ())
                        }
                    }
                }
            }

            return StringBuffer ().apply {
                append ("""
                    ExpressionTestResult(
                        test=(
                            expression=${test.expression}
                            result=${test.result}
                            isException=${test.isException}
                        ),
                        sucess=$success,
                        input=$input,
                        expected=${render (expected)},
                        output=${render (output)},
                        exception=$exception
                    )""".trimIndent())
            }.toString ()
        }
    }

    /**
     *
     */

    fun runTest (test: ExpressionTest): ExpressionTestResult {
        val input = try {
            provider.parser.parseMany (test.expression)
        } catch (e: Exception) {
            return ExpressionTestResult (test, false, exception = e)
        }
        val expected = try {
            provider.parser.parse (test.result)
        } catch (e: Exception) {
            return ExpressionTestResult (test, false, exception = e)
        }

        return try {
            val (_, result, _) = interp.evalMany (input).last ()
            val equal = EqualOp.isEqual (result, interp.eval (expected))
            try {
                val success = equal && ! test.isException
                ExpressionTestResult (test, success, input, expected, result)
            }
            catch (e: Exception) {
                ExpressionTestResult (test, test.isException, input, expected, result, exception = e)
            }
        }
        catch (e: Exception) {
            ExpressionTestResult (test, test.isException, input, expected, exception = e)
        }

        // NOT REACHED
    }
}

// EOF
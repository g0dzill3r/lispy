package lispy

import lispy.builtin.Builtins
import lispy.builtin.Invokable
import java.util.*

private val DEBUG = false

/**
 * A simple scheme interpreter.
 *
 * Book at: https://web.mit.edu/6.001/6.037/sicp.pdf
 * Also: https://www.cs.rpi.edu/academics/courses/fall00/ai/scheme/reference/schintro-v14/schintro_56.html
 * Online interpreter at: https://inst.eecs.berkeley.edu/~cs61a/fa14/assets/interpreter/scheme.html
 * Lisp interpreter here: http://nhiro.org/learn_language/LISP-on-browser.html
 */

class Interpreter (val provider: Provider) {
    private val scopes = Stack<Scope> ()

    val scope: Scope
        get () = scopes.peek ()

    fun put (symbol: Symbol, value: Any) = scope.put (symbol.symbol, value)
    fun put (symbol: String, value: Any) = scope.put (symbol, value)

    fun get (symbol: Symbol): Any? = scope.get (symbol.symbol)
    fun get (symbol: String): Any? = scope.get (symbol)

    fun <T> scoped (map: MutableMap<String, Any>, func: () -> T): T {
        try {
            scopes.push (Scope (map, scope))
            return func ()
        }
        finally {
            scopes.pop ()
        }
    }

    init {
        reset ()
    }

    fun reset () {
        scopes.clear ()
        scopes.push (Scope ())
        Builtins.ALL.forEach {
            scope.put (it.symbol, it)
        }
        Builtins.EXTRAS.forEach {
            eval (it)
        }
    }

    /**
     * Evaluate some number of expressions in a string
     */

    fun eval (string: String): List<Expression> {
        val els = provider.parser.parseMany (string)
        return els.map { eval (it) }
    }

    /**
     *
     */

    fun eval (expr: Expression): Expression {
        if (DEBUG) {
            println ("DEBUG ${expr}")
        }
        return when (expr) {
            is Value -> expr
            is Symbol -> {
                val value = scope.get (expr)
                when (value) {
                    null -> NilValue
                    is Expression -> value
                    else -> StringValue ("${value::class.simpleName}:${value}")
                }
            }
            is ExpressionCell -> evalCell (expr)
            else -> throw IllegalStateException ("Didn't expect a ${expr::class.java}")
        }
    }

    /**
     *
     */


    private fun evalCell (expr: ExpressionCell): Expression {
        if (expr.car == NilValue || expr.car == ExpressionCell.NIL) {
            return ExpressionCell.NIL
        }
        if (expr.car == NilValue) {
            throw IllegalStateException ("Cannot evaluate ${expr}")
        }
        val car = eval (expr.car)
        if (car == NilValue) {
            throw IllegalStateException ("Unrecognized symbol: ${expr.car}")
        }
        return when (car) {
            is Invokable -> {
                val cell = if (expr.cdr is ExpressionCell) expr.cdr else ExpressionCell.NIL
                car.invoke (cell, this)
            }
            else -> throw IllegalStateException ("Expected symbol; found $car")
        }
    }
}


// EOF
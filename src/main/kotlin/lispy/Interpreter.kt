package lispy

import lispy.builtin.*
import java.util.*

private val DEBUG = false

/**
 * A simple scheme interpreter.
 *
 * Book at: https://web.mit.edu/6.001/6.037/sicp.pdf
 * Also: https://www.cs.rpi.edu/academics/courses/fall00/ai/scheme/reference/schintro-v14/schintro_56.html
 * Online interpreter at: https://inst.eecs.berkeley.edu/~cs61a/fa14/assets/interpreter/scheme.html
 * Scheme basics: https://courses.cs.washington.edu/courses/cse341/02wi/scheme/basics.html
 * Lisp interpreter here: http://nhiro.org/learn_language/LISP-on-browser.html
 */

class Interpreter (val provider: Provider, val startTime: Long = System.currentTimeMillis()) {
    private val scopes = Stack<Scope> ()

    val scope: Scope
        get () = scopes.peek ()

    fun put (symbol: Symbol, value: Expression) = scope.put (symbol.symbol, value)
    fun put (symbol: String, value: Expression) = scope.put (symbol, value)

    fun get (symbol: Symbol): Expression = get (symbol.symbol)
    fun get (symbol: String): Expression {
        for (i in scopes.indices.reversed ()) {
            // TODO: We can short circuit this if the static chain and dynamic are the same
            val static = i == scopes.size - 1
            val maybe = scopes[i].get (symbol, static)
            if (maybe != null) {
                return maybe
            }
        }
        throw IllegalStateException ("Unknown identifier: ${symbol}")
    }

    fun locate (symbol: Symbol): MutableMap<String, Expression> = locate (symbol.symbol)
    fun locate (symbol: String): MutableMap<String, Expression> {
        for (i in scopes.indices.reversed ()) {
            // TODO: We can short circuit this if the static chain and dynamic are the same
            val static = i == scopes.size - 1
            val maybe = scopes[i].locate (symbol, static)
            if (maybe != null) {
                return maybe
            }
        }
        throw IllegalStateException ("Unknown identifier: ${symbol}")
    }

    fun <T> scoped (scope: Scope?, func: () -> T): T {
        try {
            if (scope != null) {
                scopes.push (scope)
            }
            return func ()
        }
        finally {
            if (scope != null) {
                scopes.pop ()
            }
        }
    }

    fun <T> scoped (map: MutableMap<String, Expression>, func: () -> T): T = scoped (Scope (map, scope), func)

    init {
        reset ()
    }

    fun reset () {
        scopes.clear ()
        scopes.push (Scope ())

        install (LambdaBuiltins)
        install (MathBuiltins)
        install (BooleanBuiltins)
        install (TypeBuiltins)
        install (CondBuiltins)
        install (ListBuiltins)
        install (DebugBuiltins)
        install (TestBuiltins)
        install (RationalBuiltin)
        install (EqualBuiltins)
    }

    private fun install (source: OpSource) {
        source.buildins.forEach {
            scope.put (it.symbol, it as Expression)
        }
        source.extras.forEach {
            eval (it)
        }
    }


    fun dumpScope (scope: Scope) {
        for ((key, value) in scope.map) {
            println ("  $key = $value")
        }
        return
    }

    fun dumpScopes () {
        for (i in scopes.indices) {
            println ("SCOPE $i =================")
            dumpScope (scopes[i])
        }
        return
    }

    val buffer = StringBuffer ()

    fun eval (string: String): List<Expression> {
        val els = provider.parser.parseMany (string)
        return els.map {
            it
        }

    }

    /**
     * Evaluate some number of expressions in a string
     */

    fun eval (string: String, func: (input: Expression, result: Expression, output: String) -> Unit): Expression {
        val els = provider.parser.parseMany (string)
        var result: Expression = NilValue
        els.forEach {
            result = eval (it)
            func (it, result, buffer.toString ())
            buffer.setLength (0)
        }

        return result
    }

    /**
     * Used to evaluate an arbitrary expression.
     */

    fun eval (expr: Expression): Expression {
        return when (expr) {
            is Value -> expr
            is Symbol -> {
                val value = get (expr)
                when (value) {
                    is Expression -> value
                    else -> StringValue ("${value::class.simpleName}:${value}")
                }
            }
            is Pair -> evalCell (expr)
            is Invokable -> expr
            else -> throw IllegalStateException ("Didn't expect a ${expr::class.java} in ${expr}")
        }
    }

    /**
     * Used to invoke a procedure.
     */

    private fun evalCell (expr: Pair): Expression {
        if (expr.car == NilValue || expr.car == Pair.NIL) {
            return Pair.NIL
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
                val cell = if (expr.cdr is Pair) expr.cdr else Pair.NIL
                car.invoke (cell as Pair, this)
            }
//            is Value -> car
            else -> throw IllegalStateException ("Expected symbol; found $car in $expr")
        }
    }
}

// EOF
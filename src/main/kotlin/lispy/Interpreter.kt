package lispy

import lispy.builtin.Builtins
import lispy.builtin.Invokable
import java.util.*

private val DEBUG = false

/**
 * A simple scheme interpreter.
 *
 * Book at: https://web.mit.edu/6.001/6.037/sicp.pdf
 * Online interpreter at: https://inst.eecs.berkeley.edu/~cs61a/fa14/assets/interpreter/scheme.html
 * Lisp interpreter here: http://nhiro.org/learn_language/LISP-on-browser.html
 */
class Interpreter {
    private val env = mutableMapOf<String, Any> ()
    private val scopes = Stack<MutableMap<String, Any>> ()
    fun put (symbol: Symbol, value: Any, global: Boolean = false) = put (symbol.symbol, value, global)

    val environment: Map<String, Any>
        get () = env.toMap ()

    val scope: Map<String, Any>?
        get () = if (scopes.isEmpty()) {
            null
        } else {
            scopes.peek ()
        }

    fun <T> scoped (scope: MutableMap<String, Any>, func: () -> T): T {
        try {
            scopes.push (scope)
            return func ()
        }
        finally {
            scopes.pop ()
        }
    }

    fun put (symbol: String, value: Any, global: Boolean = false) {
        if (global) {
            if (env.containsKey(symbol)) {
                println ("WARNING: Redefining global '$symbol'")
            }
            env[symbol] = value
        } else {
            if (scopes.isEmpty ()) {
                throw IllegalStateException ("No active scope.")
            } else {
                scopes.peek ()!![symbol] = value
            }
        }
        return
    }

    fun get (symbol: Symbol): Any? = get (symbol.symbol)

    fun get (symbol: String): Any? {
        return if (scopes.isNotEmpty()) {
            scopes.peek ().get (symbol) ?: env [symbol]
        } else {
            env [symbol]
        }
    }

    init {
        reset ()
    }

    fun reset () {
        env.clear ()
        scopes.clear ()
        env.apply {
            put ("else", BooleanValue (true))
        }
        Builtins.ALL.forEach {
            env[it.symbol] = it
        }
        Builtins.EXTRAS.forEach {
            eval (it)
        }
    }

    fun eval (string: String): Any = eval (Parser.parse (string))

    fun eval (expr: Expression): Expression {
        if (DEBUG) {
            println ("DEBUG ${expr}")
        }
        return when (expr) {
            is Value -> expr
            is Symbol -> {
                val value = get (expr)
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

    private fun evalCell (expr: ExpressionCell): Expression {
        if (expr.car == NilValue) {
            throw IllegalStateException ("Cannot evaluate ${expr}")
        }
        val car = eval (expr.car)
        if (car == NilValue) {
            throw IllegalStateException ("Unrecognized symbol: ${expr.car}")
        }
        return when (car) {
            is Invokable -> car.invoke (if (expr.cdr is ExpressionCell) expr.cdr else ExpressionCell.NIL, this) // TODO: This is a hack, fix me.
            else -> throw IllegalStateException ("Expected symbol; found $car")
        }
    }
}
fun main () {
    val lisp = Interpreter ()

    interpreter ("repl> ") {
        val exprs = Parser.parseMany (it)
        exprs.forEach {
            val res = lisp.eval (it)
            println ("-> $res")
        }
        true
    }
    // NOT REACHED
}

// EOF
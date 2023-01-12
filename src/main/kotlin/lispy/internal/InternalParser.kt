package lispy.internal

import lispy.*
import lispy.builtin.QuasiquoteOp
import lispy.builtin.QuoteOp
import lispy.builtin.UnquoteOp
import lispy.builtin.UnquoteSplicingOp

/**
 * Implements a handwritten parser for scheme source files.
 */

class InternalParser (val lexer: Lexer) : Parser {

    /**
     * Parse all the available expressions from the token stream.
     */

    override fun parseMany (input: String): List<Expression> {
        return buildList {
            val iter = lexer.lex(input).iterator().peekable()
            while (iter.hasNext ()) {
                val expr = parseExpression (iter)
                add (expr)
            }
        }
    }

    /**
     * Parse a single expression from the specified string.
     */

    override fun parse (input: String): Expression {
        val iter = lexer.lex(input).iterator().peekable()
        val expr = parseExpression (iter)
        if (iter.hasNext ()) {
            val next = iter.next ()
            throw IllegalStateException ("Found extra content: $next at ${next.location}")
        }
        return expr
    }

    /**
     * Parses a single expression from the token stream.
     */

    private fun parseExpression (iter: PeekableIterator<Token>) : Expression {
        if (! iter.hasNext ()) {
            throw IllegalStateException ("Unexpected EOF")
        }
        val next = iter.next ()
        return when (next) {
            is Token.Nil -> ConsPair.NIL
            is Token.Long -> LongValue (next.value)
            is Token.Double -> DoubleValue (next.value)
            is Token.Bool -> BooleanValue (next.value)
            is Token.Symbol -> Symbol (next.symbol)
            is Token.Unquote -> ConsPair (Symbol (UnquoteOp.UNQUOTE), ConsPair (parseExpression (iter)))
            is Token.Quasiquote -> ConsPair (Symbol (QuasiquoteOp.QUASIQUOTE), ConsPair (parseExpression (iter)))
            is Token.QuotedString -> StringValue (next.string)
            is Token.Quote -> ConsPair(Symbol (QuoteOp.QUOTE), ConsPair (parseExpression (iter)))
            is Token.UnquoteSplicing -> ConsPair (Symbol (UnquoteSplicingOp.UNQUOTE_SPLICING), ConsPair (parseExpression (iter)))
            is Token.LeftParen -> {
                if (iter.peek () is Token.RightParen) {
                    iter.next ()
                    ConsPair.NIL
                } else {
                    parseList (iter)
                }
            }
            else -> throw IllegalStateException ("Unexpected token: $next at ${next.location}")
        }
    }

    private inline fun <reified T: Token> expect (iter: PeekableIterator<Token>) {
        val token = iter.next ()
        if (token !is T) {
            throw IllegalStateException ("Expected ${T::class.simpleName} found $token")
        }
        return
    }

    private fun parseList (iter: PeekableIterator<Token>): Expression {
        val list = mutableListOf<Expression> ()

        while (iter.hasNext ()) {
            val next = iter.peek ()
            if (next is Token.RightParen) {
                iter.next ()
                return ConsPair.fromList (list)
            }

            // Handle the special case of the dotted cell notation (e.g., ( 1 . 2)).

            if (next is Token.Dot) {
                if (list.size != 1) {
                    throw IllegalStateException ("Invalid dot placement; expected 1 prior expresion found ${list.size}")
                }
                iter.next ()
                val expr = parseExpression (iter)
                val cell = if (expr.isNil) {
                    ConsPair (list[0])
                } else {
                    ConsPair (list[0], expr)
                }
                expect<Token.RightParen> (iter)
                return cell
            }

            list.add (parseExpression (iter))
        }

        throw IllegalStateException ("Unexpected EOF; expected ')'")
    }
}

// EOF
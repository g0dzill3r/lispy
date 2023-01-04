package lispy

/**
 * Implements a handwritten parser for scheme source files.
 */

object Parser {
    fun parseMany (input: String): List<Expression> {
        return buildList {
            val iter = Lexer.lex (input).iterator().peekable()
            while (iter.hasNext ()) {
                val expr = parseExpression (iter)
                add (expr)
            }
        }
    }
    fun parse (input: String): Expression {
        val iter = Lexer.lex (input).iterator().peekable()
        val expr = parseExpression (iter)
        if (iter.hasNext ()) {
            val next = iter.next ()
            throw IllegalStateException ("Found extra content: $next at ${next.location}")
        }
        return expr
    }

    private fun parseExpression (iter: PeekableIterator<Token>) : Expression {
        if (! iter.hasNext ()) {
            throw IllegalStateException ("Unexpected EOF")
        }
        val next = iter.next ()
        return when (next) {
            is Token.Nil -> ExpressionCell.NIL
            is Token.Integer -> IntValue (next.value)
            is Token.Float -> FloatValue (next.value)
            is Token.Bool -> BooleanValue (next.value)
            is Token.Symbol -> Symbol (next.symbol)
            is Token.QuotedString -> StringValue (next.string)
            is Token.Quote -> {
                ExpressionCell (Symbol ("quote"), ExpressionCell (parseExpression (iter)))
            }
            is Token.LeftParen -> {
                if (iter.peek () is Token.RightParen) {
                    iter.next ()
                    ExpressionCell.NIL
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
                return ExpressionCell.fromList (list)
            }

            // Handle the special case of the dotted cell notation (e.g., ( 1 . 2)).

            if (next is Token.Dot) {
                if (list.size != 1) {
                    throw IllegalStateException ("Invalid dot placement; expected 1 prior expresion found ${list.size}")
                }
                iter.next ()
                val cell = ExpressionCell (list[0], parseExpression (iter))
                expect<Token.RightParen> (iter)
                return cell
            }

            list.add (parseExpression (iter))
        }

        throw IllegalStateException ("Unexpected EOF; expected ')'")
    }
}

// EOF
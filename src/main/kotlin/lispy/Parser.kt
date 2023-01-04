package lispy

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

    private fun parseList (iter: PeekableIterator<Token>): Expression {
        if (! iter.hasNext ()) {
            throw IllegalStateException ("Unexpected EOF; expected ')'")
        }
        if (iter.peek () is Token.RightParen) {
            iter.next ()
            return NilValue
        }
        return ExpressionCell (parseExpression (iter), parseList (iter))
    }
}

fun main () {
    interpreter ("p> ") {
        val exprs = Parser.parseMany (it)
        exprs.forEach {
            println (it)
            if (it is ExpressionCell) {
                println (it.toBrackets())
            }
        }
        true
    }
    // NOT REACHED
}

// EOF
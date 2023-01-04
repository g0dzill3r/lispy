package lispy

/**
 * Implements a handwritten lexer for tokenizing scheme source files.
 */

object Lexer {
    fun lex (input: String) : Sequence<Token> {
        return sequence {
            val source = Source (input)
            while (source.hasNext ()) {
                when (source.peek ()) {
                    '(' -> {
                        source.next ()
                        yield (Token.LeftParen (source.location))
                    }
                    ')' -> {
                        source.next ()
                        yield (Token.RightParen (source.location))
                    }
                    '.' -> {
                        source.next ()
                        yield (Token.Dot (source.location))
                    }
                    '\'' -> {
                        source.next () 
                        yield (Token.Quote (source.location))
                    }
                    ' ', '\n', '\t' -> source.next ()
                    in '0' .. '9' -> {
                        yield (readNumeric (source))
                    }
                    '"' -> {
                        yield (readString (source))
                    }
                    else -> {
                        yield (readSymbol (source))
                    }
                }
            }
        }
    }

    private val NUMERIC_VALID_CHARS = buildMap<Boolean, String> {
        put (false, "0123456789.")
        put (true, "0123456789")
    }

    private fun readNumeric (source: Source): Token {
        val buf = StringBuffer ()
        buf.append (source.next ())
        var isFloat = false

        while (source.hasNext ()) {
            val next = source.peek ()
            if (! NUMERIC_VALID_CHARS[isFloat]!!.contains (next)) {
                break
            }
            buf.append (source.next ())
            if (next == '.') {
                isFloat = true
            }
        }

        return if (isFloat) {
            Token.Float (buf.toString ().toFloat (), source.location)
        } else {
            Token.Integer (buf.toString ().toInt (), source.location)
        }
    }


    private fun readString (source: Source): Token {
        val buf = StringBuffer ()
        var c = source.next ()

        while (source.hasNext ()) {
            c = source.peek ()
            source.next ()
            if (c == '\"') {
                break
            } else {
                buf.append (c)
            }
        }
        if (c != '\"') {
            throw IllegalStateException ("Unterminated quoted string at ${source.location}")
        }

        return Token.QuotedString (buf.toString (), source.location)
    }

    private const val SYMBOL_INVALID_CHARS = "(). \t\n"

    private fun readSymbol (source: Source): Token {
        val buf = StringBuffer ()
        buf.append (source.next ())

        while (source.hasNext ()) {
            val next = source.peek ()
            if (SYMBOL_INVALID_CHARS.contains (next)) {
                break
            }
            buf.append (source.next ())
        }

        val symbol = buf.toString ()
        return when (symbol) {
            "nil" -> Token.Nil (source.location)
            "#t" -> Token.Bool (true, source.location)
            "#f" -> Token.Bool (false, source.location)
            else -> Token.Symbol (symbol, source.location)
        }
    }
}




// EOF
package lispy

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

    private fun readNumeric (source: Source): Token {
        val buf = StringBuffer ()
        buf.append (source.next ())
        var isFloat = false

        while (source.hasNext ()) {
            val next = source.peek ()
            when (next) {
                '.' -> {
                    if (isFloat) {
                        throw IllegalStateException ("Unexpected token: $next at ${source.location}")
                    } else {
                        isFloat = true
                        buf.append (source.next ())
                    }
                }
                in '0' .. '9' -> {
                    buf.append (source.next ())
                }
                ')', '(',
                ' ', '\t', '\n' -> {
                    break
                }
                else -> {
                    throw IllegalStateException ("Unexpected token: $next at ${source.location}")
                }
            }
        }

        return if (isFloat) {
            Token.Float (buf.toString ().toFloat (), source.location)
        } else {
            Token.Integer (buf.toString ().toInt (), source.location)
        }
    }

    private val SYMBOL_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~`!@#$%^&*()-_=+{}|[]:;?"

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

    private fun readSymbol (source: Source): Token {
        val buf = StringBuffer ()
        buf.append (source.next ())

        while (source.hasNext ()) {
            val next = source.peek ()
            when (next) {
                '(', ')', ' ', '\t', '\n' -> {
                    break
                }
                else -> {
                    if (SYMBOL_CHARS.indexOf (next) != -1) {
                        buf.append (source.next ())
                    } else {
                        throw IllegalStateException ("Unrecognized token: $next at ${source.location}")
                    }
                }
            }
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
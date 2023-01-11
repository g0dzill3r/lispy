package lispy.internal

import lispy.Lexer
import lispy.Token

/**
 * Implements a handwritten lexer for tokenizing scheme source files.
 */

class InternalLexer : Lexer {
    override fun lex (input: String) : Sequence<Token> {
        return sequence {
            val source = Source (input)

            while (source.hasNext ()) {
                when (source.peek ()) {
                    ';' -> readComment (source)
                    '`' -> {
                        source.next ()
                        yield (Token.Backquote (source.location))
                    }
                    ',' -> {
                        source.next ()
                        yield (Token.Comma (source.location))
                    }
                    '(' -> {
                        source.next ()
                        yield (Token.LeftParen(source.location))
                    }
                    ')' -> {
                        source.next ()
                        yield (Token.RightParen(source.location))
                    }
                    '.' -> {
                        source.next ()
                        yield (Token.Dot(source.location))
                    }
                    '\'' -> {
                        source.next () 
                        yield (Token.Quote(source.location))
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
            Token.Float(buf.toString().toFloat(), source.location)
        } else {
            Token.Integer(buf.toString().toInt(), source.location)
        }
    }

    /**
     * Reads a comment from the source. We'll just eat up characters
     * until we get to the end of the line.
     */

    private fun readComment (source: Source): String {
        val buf = StringBuffer ()

        while (source.hasNext ()) {
            val c = source.next ()
            if (c == '\n') {
                break
            }
            buf.append (c)
        }

        return buf.toString ()
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

        return Token.QuotedString(buf.toString(), source.location)
    }

    private val SYMBOL_INVALID_CHARS = "(). \t\n"

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
            "nil" -> Token.Nil(source.location)
            "#t" -> Token.Bool(true, source.location)
            "#f" -> Token.Bool(false, source.location)
            else -> Token.Symbol(symbol, source.location)
        }
    }
}

// EOF
package lispy

import lispy.internal.Source

/**
 * The Token type represents the range of tokens that can be emitted by the lexer.
 */

sealed class Token (val location: Source.Location) {
    class Integer (val value: Int, loc: Source.Location) : Token (loc) {
        override fun toString() : String = "int:$value"
    }
    class Double (val value: kotlin.Double, loc: Source.Location): Token (loc) {
        override fun toString (): String = "double:$value"
    }
    class LeftParen (loc: Source.Location) : Token (loc) {
        override fun toString(): String = "LeftParen"
    }
    class RightParen (loc: Source.Location) : Token(loc) {
        override fun toString(): String = "RightParen"
    }
    class Symbol (val symbol: String, loc: Source.Location): Token (loc) {
        override fun toString () : String = "symbol:$symbol"
    }
    class QuotedString (val string: String, loc: Source.Location): Token (loc) {
        override fun toString () : String = "quoted:$string"
    }
    class Bool (val value: Boolean, loc: Source.Location): Token (loc) {
        override fun toString(): String = "boolean:$value"
    }
    class Nil (loc: Source.Location) : Token (loc) {
        override fun toString (): String = "nil"
    }
    class Dot (loc: Source.Location) : Token (loc) {
        override fun toString (): String = "dot"
    }
    class UnquoteSplicing (loc: Source.Location): Token (loc) {
        override fun toString (): String = "UnquoteSplicing"
    }
    class Quasiquote (loc: Source.Location): Token (loc) {
        override fun toString (): String = "Quasiquote"
    }
    class Unquote (loc: Source.Location): Token (loc) {
        override fun toString (): String = "Unquote"
    }
    class Quote (loc: Source.Location): Token (loc) {
        override fun toString(): String = "Quote"
    }

    companion object {
        fun render (token: Token) : String{
            return when (token) {
                is LeftParen -> "("
                is RightParen -> ")"
                is Dot -> "."
                is Nil -> "nil"
                is Bool -> if (token.value) "#t" else "#f"
                is Symbol -> token.symbol
                is QuotedString -> "\"${token.string}\""
                is Integer -> "${token.value}"
                is Double -> "${token.value}"
                is Quote -> "\'"
                is Quasiquote -> "\\`"
                is Unquote -> ","
                is UnquoteSplicing -> ",@"
            }
        }
    }
}

// EOF
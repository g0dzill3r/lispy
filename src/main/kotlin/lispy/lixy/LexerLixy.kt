package lispy.lixy

import lispy.Lexer
import guru.zoroark.lixy.LixyStateLabel
import guru.zoroark.lixy.LixyToken
import guru.zoroark.lixy.LixyTokenType
import guru.zoroark.lixy.lixy
import guru.zoroark.lixy.matchers.anyOf
import guru.zoroark.lixy.matchers.matches
import lispy.Token
import lispy.internal.Source

enum class TokenType : LixyTokenType {
    LEFT_PAREN, RIGHT_PAREN, DOT, QUOTE, SYMBOL, STRING_START, STRING_END, ESCAPED_QUOTE, STRING_CONTENT, INT, DOUBlE, WHITESPACE, QUOTED_STRING
}

enum class TokenizerState : LixyStateLabel {
    INSIDE_STRING, OUTSIDE_STRING
}
class LexerLixy : Lexer {
    override fun lex(input: String): Sequence<Token> {
        return sequence {
            val wrapped = tokenize (input).iterator ()
            while (wrapped.hasNext ()) {
                val next = wrapped.next () 
                yield (transform (next))
            }
        }
    }
    
    private fun transform (ltoken: LixyToken): Token {
        val loc = Source.Location ("lixy", ltoken.startsAt, ltoken.endsAt)

        return when (ltoken.tokenType) {
            TokenType.LEFT_PAREN -> Token.LeftParen(loc)
            TokenType.RIGHT_PAREN -> Token.RightParen(loc)
            TokenType.DOT -> Token.Dot(loc)
            TokenType.QUOTE -> Token.Quote(loc)
            TokenType.SYMBOL -> Token.Symbol(ltoken.string, loc)
            TokenType.INT -> Token.Long(ltoken.string.toLong (), loc)
            TokenType.DOUBlE -> Token.Double (ltoken.string.toDouble (), loc)
            TokenType.QUOTED_STRING -> Token.QuotedString(ltoken.string, loc)
            else -> throw IllegalStateException("Unexpected token type: ${ltoken.tokenType}")
        }
    }
    
    fun tokenize (input: String): Sequence<LixyToken> {
        return sequence {
            val tokens = LEXER.tokenize (input).iterator ()
            var startToken: LixyToken? = null
            val buf = StringBuffer ()

            while (tokens.hasNext()) {
                val token = tokens.next ()

                when (token.tokenType) {
                    TokenType.WHITESPACE -> Unit
                    TokenType.DOT -> yield (token)
                    TokenType.INT -> yield (token)
                    TokenType.DOUBlE -> yield (token)
                    TokenType.LEFT_PAREN -> yield (token)
                    TokenType.RIGHT_PAREN -> yield (token)
                    TokenType.SYMBOL -> yield (token)
                    TokenType.QUOTE -> yield (token)
                    TokenType.STRING_START -> {
                        startToken = token
                    }
                    TokenType.ESCAPED_QUOTE -> {
                        buf.append ("\"")
                    }
                    TokenType.STRING_CONTENT -> {
                        buf.append (token.string)
                    }
                    TokenType.STRING_END -> {
                        val concat = LixyToken (buf.toString (), startToken!!.startsAt, token.endsAt, TokenType.QUOTED_STRING)
                        buf.setLength (0)
                        startToken = null
                        yield (concat)
                    }
                }
            }
        }
    }

    companion object {
        val LEXER = lixy {
            default state TokenizerState.OUTSIDE_STRING

            TokenizerState.OUTSIDE_STRING state {
                "(" isToken TokenType.LEFT_PAREN
                ")" isToken TokenType.RIGHT_PAREN
                "." isToken TokenType.DOT
                anyOf(" ", "\t", "\n") isToken TokenType.WHITESPACE
                "\'" isToken TokenType.QUOTE
                "\"" isToken TokenType.STRING_START thenState TokenizerState.INSIDE_STRING
                matches("[0-9]+\\.[0-9]+") isToken TokenType.DOUBlE
                matches("[0-9]+") isToken TokenType.INT
                matches("[^ \t\n(),]+") isToken TokenType.SYMBOL
            }

            TokenizerState.INSIDE_STRING state {
                "\"" isToken TokenType.STRING_END thenState TokenizerState.OUTSIDE_STRING
                "\\\"" isToken TokenType.ESCAPED_QUOTE
                matches("""([^\\"]*)""") isToken TokenType.STRING_CONTENT
            }
        }
    }
}

//fun main() {
//    val type = ProviderType.LIXY
//    val provider = ProviderFactory.getProvider(type)
//    val lexer = provider.lexer
//
//    interpreter ("lixy> ") {
//        val tokens = .tokenize (it)
//        tokens.forEach {
//            println (it)
//        }
//        true
//    }
//
//    return
//}

// EOF
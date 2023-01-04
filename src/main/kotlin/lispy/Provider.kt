package lispy

import lispy.lixy.LexerLixy
import lispy.internal.InternalLexer
import lispy.internal.InternalParser

interface Lexer {
    fun lex (input: String) : Sequence<Token>
}

interface Parser {
    fun parseMany (input: String): List<Expression>
    fun parse (input: String): Expression
}

data class Provider (val type: ProviderType, val lexer: Lexer, val parser: Parser)

enum class ProviderType {
    INTERNAL,
    LIXY
}

private val DEFAULT_PROVIDER = ProviderType.INTERNAL.name

object ProviderFactory {
    fun getProvider (): Provider {
        val provider = System.getenv("PROVIDER") ?: DEFAULT_PROVIDER
        return getProvider (ProviderType.valueOf (provider))
    }

    fun getProvider (type: ProviderType): Provider {
        return when (type) {
            ProviderType.INTERNAL -> {
                val lexer = InternalLexer ()
                Provider (type, lexer, InternalParser (lexer))
            }
            ProviderType.LIXY ->  {
                val lexer = LexerLixy ()
                Provider (type, lexer, InternalParser (lexer))
            }
        }
    }
}

// EOF
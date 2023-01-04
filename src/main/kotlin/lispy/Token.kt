package lispy

sealed class Token (val location: Source.Location) {
    class Integer (val value: Int, loc: Source.Location) : Token (loc) {
        override fun toString() : String = "int:$value"
    }
    class Float(val value: kotlin.Float, loc: Source.Location): Token (loc) {
        override fun toString (): String = "float:$value"
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

//    class Dot (loc: Source.Location) : Token (loc) {
//        override fun toString (): String = "dot"
//    }

    class Quote (loc: Source.Location): Token (loc) {
        override fun toString(): String = "Quote"
    }
}

// EOF
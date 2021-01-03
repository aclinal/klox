package ca.alexleung.lox

class Token(val type: TokenType, val lexeme: String, val literal: Any?, val line: Int) {
    override fun toString(): String {
        return "$type $lexeme $literal $line"
    }

    override fun equals(other: Any?): Boolean {
        return other is Token
                && type == other.type
                && lexeme == other.lexeme
                && literal == other.literal
                && line == other.line
    }
}
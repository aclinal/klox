import ca.alexleung.lox.Scanner
import ca.alexleung.lox.Token
import ca.alexleung.lox.TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScannerTest {
    @Test
    fun `valid tokens are parsed correctly`() {
        val source = """// this is a comment
            (( )){} // grouping stuff
            !*+-/=<> <= == // operators
            """;
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()
        assertEquals(tokens.size, 17)
        assertEquals(tokens[0], Token(TokenType.LEFT_PAREN, "(", null, 2))
        assertEquals(tokens[1], Token(TokenType.LEFT_PAREN, "(", null, 2))
        assertEquals(tokens[2], Token(TokenType.RIGHT_PAREN, ")", null, 2))
        assertEquals(tokens[3], Token(TokenType.RIGHT_PAREN, ")", null, 2))
        assertEquals(tokens[4], Token(TokenType.LEFT_BRACE, "{", null, 2))
        assertEquals(tokens[5], Token(TokenType.RIGHT_BRACE, "}", null, 2))
        assertEquals(tokens[6], Token(TokenType.BANG, "!", null, 3))
        assertEquals(tokens[7], Token(TokenType.STAR, "*", null, 3))
        assertEquals(tokens[8], Token(TokenType.PLUS, "+", null, 3))
        assertEquals(tokens[9], Token(TokenType.MINUS, "-", null, 3))
        assertEquals(tokens[10], Token(TokenType.SLASH, "/", null, 3))
        assertEquals(tokens[11], Token(TokenType.EQUAL, "=", null, 3))
        assertEquals(tokens[12], Token(TokenType.LESS, "<", null, 3))
        assertEquals(tokens[13], Token(TokenType.GREATER, ">", null, 3))
        assertEquals(tokens[14], Token(TokenType.LESS_EQUAL, "<=", null, 3))
        assertEquals(tokens[15], Token(TokenType.EQUAL_EQUAL, "==", null, 3))
        assertEquals(tokens[16], Token(TokenType.EOF, "", null, 4))
    }
}
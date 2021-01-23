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
        assertEquals(17, tokens.size)
        assertEquals(Token(TokenType.LEFT_PAREN, "(", null, 2), tokens[0])
        assertEquals(Token(TokenType.LEFT_PAREN, "(", null, 2), tokens[1])
        assertEquals(Token(TokenType.RIGHT_PAREN, ")", null, 2), tokens[2])
        assertEquals(Token(TokenType.RIGHT_PAREN, ")", null, 2), tokens[3])
        assertEquals(Token(TokenType.LEFT_BRACE, "{", null, 2), tokens[4])
        assertEquals(Token(TokenType.RIGHT_BRACE, "}", null, 2), tokens[5])
        assertEquals(Token(TokenType.BANG, "!", null, 3), tokens[6])
        assertEquals(Token(TokenType.STAR, "*", null, 3), tokens[7])
        assertEquals(Token(TokenType.PLUS, "+", null, 3), tokens[8])
        assertEquals(Token(TokenType.MINUS, "-", null, 3), tokens[9])
        assertEquals(Token(TokenType.SLASH, "/", null, 3), tokens[10])
        assertEquals(Token(TokenType.EQUAL, "=", null, 3), tokens[11])
        assertEquals(Token(TokenType.LESS, "<", null, 3), tokens[12])
        assertEquals(Token(TokenType.GREATER, ">", null, 3), tokens[13])
        assertEquals(Token(TokenType.LESS_EQUAL, "<=", null, 3), tokens[14])
        assertEquals(Token(TokenType.EQUAL_EQUAL, "==", null, 3), tokens[15])
        assertEquals(Token(TokenType.EOF, "", null, 4), tokens[16])
    }
}
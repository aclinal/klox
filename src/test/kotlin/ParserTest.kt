import ca.alexleung.lox.Binary
import ca.alexleung.lox.Expression
import ca.alexleung.lox.Grouping
import ca.alexleung.lox.Literal
import ca.alexleung.lox.Parser
import ca.alexleung.lox.Print
import ca.alexleung.lox.Scanner
import ca.alexleung.lox.Token
import ca.alexleung.lox.TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParserTest {
    @Test
    fun `valid expression statement is parsed correctly`() {
        val source = """"some string" + (1 + 1) / 2 + 5;"""
        val scanner = Scanner(source)

        val parser = Parser(scanner.scanTokens())
        val statements = parser.parse()
        assertEquals(1, statements.size)

        val expectedExpression = Binary(
            Binary(
                Literal("some string"),
                Token(TokenType.PLUS, "+", null, 1),
                Binary(
                    Grouping(
                        Binary(
                            Literal(1.0),
                            Token(TokenType.PLUS, "+", null, 1),
                            Literal(1.0)
                        )
                    ),
                    Token(TokenType.SLASH, "/", null, 1),
                    Literal(2.0)
                )
            ),
            Token(TokenType.PLUS, "+", null, 1),
            Literal(5.0)
        )

        val expectedStatement = Expression(expectedExpression)

        assertEquals(expectedStatement, statements[0])
    }

    @Test
    fun `valid print statement is parsed correctly`() {
        val source = """print "some string" + (1 + 1) / 2 + 5;"""
        val scanner = Scanner(source)

        val parser = Parser(scanner.scanTokens())
        val statements = parser.parse()
        assertEquals(1, statements.size)

        val expectedExpression = Binary(
            Binary(
                Literal("some string"),
                Token(TokenType.PLUS, "+", null, 1),
                Binary(
                    Grouping(
                        Binary(
                            Literal(1.0),
                            Token(TokenType.PLUS, "+", null, 1),
                            Literal(1.0)
                        )
                    ),
                    Token(TokenType.SLASH, "/", null, 1),
                    Literal(2.0)
                )
            ),
            Token(TokenType.PLUS, "+", null, 1),
            Literal(5.0)
        )

        val expectedStatement = Print(expectedExpression)

        assertEquals(expectedStatement, statements[0])
    }
}

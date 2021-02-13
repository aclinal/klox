import ca.alexleung.lox.Expr
import ca.alexleung.lox.Parser
import ca.alexleung.lox.Scanner
import ca.alexleung.lox.Stmt
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

        val expectedExpression = Expr.Binary(
            Expr.Binary(
                Expr.Literal("some string"),
                Token(TokenType.PLUS, "+", null, 1),
                Expr.Binary(
                    Expr.Grouping(
                        Expr.Binary(
                            Expr.Literal(1.0),
                            Token(TokenType.PLUS, "+", null, 1),
                            Expr.Literal(1.0)
                        )
                    ),
                    Token(TokenType.SLASH, "/", null, 1),
                    Expr.Literal(2.0)
                )
            ),
            Token(TokenType.PLUS, "+", null, 1),
            Expr.Literal(5.0)
        )

        val expectedStatement = Stmt.Expression(expectedExpression)

        assertEquals(expectedStatement, statements[0])
    }

    @Test
    fun `valid print statement is parsed correctly`() {
        val source = """print "some string" + (1 + 1) / 2 + 5;"""
        val scanner = Scanner(source)

        val parser = Parser(scanner.scanTokens())
        val statements = parser.parse()
        assertEquals(1, statements.size)

        val expectedExpression = Expr.Binary(
            Expr.Binary(
                Expr.Literal("some string"),
                Token(TokenType.PLUS, "+", null, 1),
                Expr.Binary(
                    Expr.Grouping(
                        Expr.Binary(
                            Expr.Literal(1.0),
                            Token(TokenType.PLUS, "+", null, 1),
                            Expr.Literal(1.0)
                        )
                    ),
                    Token(TokenType.SLASH, "/", null, 1),
                    Expr.Literal(2.0)
                )
            ),
            Token(TokenType.PLUS, "+", null, 1),
            Expr.Literal(5.0)
        )

        val expectedStatement = Stmt.Print(expectedExpression)

        assertEquals(expectedStatement, statements[0])
    }
}

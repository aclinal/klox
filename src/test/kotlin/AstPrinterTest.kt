import ca.alexleung.lox.AstPrinter
import ca.alexleung.lox.Binary
import ca.alexleung.lox.Expr
import ca.alexleung.lox.Grouping
import ca.alexleung.lox.Literal
import ca.alexleung.lox.Token
import ca.alexleung.lox.TokenType
import ca.alexleung.lox.Unary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AstPrinterTest {
    @Test
    fun `prints expression`() {
        val expression: Expr = Binary(
            Unary(
                Token(TokenType.MINUS, "-", null, 1),
                Literal(123)
            ),
            Token(TokenType.STAR, "*", null, 1),
            Grouping(
                Literal(45.67)
            )
        )

        val expressionString = AstPrinter().print(expression)
        assertEquals(expressionString, "(* (- 123) (group 45.67))")
    }
}
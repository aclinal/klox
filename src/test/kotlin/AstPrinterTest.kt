import ca.alexleung.lox.AstPrinter
import ca.alexleung.lox.Expr
import ca.alexleung.lox.Token
import ca.alexleung.lox.TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AstPrinterTest {
    private val astPrinter = AstPrinter()

    @Test
    fun `prints expression`() {
        val expression = Expr.Binary(
            Expr.Unary(
                Token(TokenType.MINUS, "-", null, 1),
                Expr.Literal(123.0)
            ),
            Token(TokenType.STAR, "*", null, 1),
            Expr.Grouping(
                Expr.Literal(45.67)
            )
        )

        val expressionString = astPrinter.print(expression)
        assertEquals("(* (- 123.0) (group 45.67))", expressionString)
    }
}
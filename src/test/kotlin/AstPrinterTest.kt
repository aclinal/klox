import ca.alexleung.lox.AstPrinter
import ca.alexleung.lox.Binary
import ca.alexleung.lox.Grouping
import ca.alexleung.lox.Literal
import ca.alexleung.lox.Token
import ca.alexleung.lox.TokenType
import ca.alexleung.lox.Unary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AstPrinterTest {
    private val astPrinter = AstPrinter()

    @Test
    fun `prints expression`() {
        val expression = Binary(
            Unary(
                Token(TokenType.MINUS, "-", null, 1),
                Literal(123.0)
            ),
            Token(TokenType.STAR, "*", null, 1),
            Grouping(
                Literal(45.67)
            )
        )

        val expressionString = astPrinter.print(expression)
        assertEquals("(* (- 123.0) (group 45.67))", expressionString)
    }
}
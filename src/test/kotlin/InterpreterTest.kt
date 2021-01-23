import ca.alexleung.lox.Binary
import ca.alexleung.lox.Grouping
import ca.alexleung.lox.Interpreter
import ca.alexleung.lox.Literal
import ca.alexleung.lox.Token
import ca.alexleung.lox.TokenType
import ca.alexleung.lox.Unary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class InterpreterTest {
    private val interpreter = Interpreter()
    private val outputStreamCaptor = ByteArrayOutputStream()

    @BeforeEach
    fun setup() {
        System.setOut(PrintStream(outputStreamCaptor))
    }

    @Test
    fun `interprets arithmetic expression`() {
        // ((-333.0 / 3.0) + 6.67) * 2.0
        val expression = Binary(
            Binary(
                Binary(
                    Unary(
                        Token(TokenType.MINUS, "-", null, 1),
                        Literal(333.0)
                    ),
                    Token(TokenType.SLASH, "/", null, 1),
                    Literal(3.0)
                ),
                Token(TokenType.PLUS, "+", null, 1),
                Grouping(
                    Literal(6.67)
                )
            ),
            Token(TokenType.STAR, "*", null, 1),
            Literal(2.0)
        )

        interpreter.interpret(expression)
        assertEquals("-208.66", outputStreamCaptor.toString().trim())
    }

    @Test
    fun `interprets logic expressions`() {
        // 5.0 > 4.0
        var expression = Binary(
            Literal(5.0),
            Token(TokenType.GREATER, ">", null, 1),
            Literal(4.0)
        )

        interpreter.interpret(expression)
        assertEquals("true", outputStreamCaptor.toString().trim())

        outputStreamCaptor.reset()

        // 5.0 < 4.0
        expression = Binary(
            Literal(5.0),
            Token(TokenType.LESS, "<", null, 1),
            Literal(4.0)
        )

        interpreter.interpret(expression)
        assertEquals("false", outputStreamCaptor.toString().trim())
    }
}
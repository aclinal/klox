import ca.alexleung.lox.Assign
import ca.alexleung.lox.Binary
import ca.alexleung.lox.Expression
import ca.alexleung.lox.Grouping
import ca.alexleung.lox.Interpreter
import ca.alexleung.lox.Literal
import ca.alexleung.lox.Print
import ca.alexleung.lox.Token
import ca.alexleung.lox.TokenType
import ca.alexleung.lox.Unary
import ca.alexleung.lox.Var
import ca.alexleung.lox.Variable
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
    fun `interprets print statement with arithmetic`() {
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

        val statements = listOf(Print(expression))

        interpreter.interpret(statements)
        assertEquals("-208.66", outputStreamCaptor.toString().trim())
    }

    @Test
    fun `interprets print statement with logic`() {
        // 5.0 > 4.0
        var expression = Binary(
            Literal(5.0),
            Token(TokenType.GREATER, ">", null, 1),
            Literal(4.0)
        )

        var statements = listOf(Print(expression))

        interpreter.interpret(statements)
        assertEquals("true", outputStreamCaptor.toString().trim())

        outputStreamCaptor.reset()

        // 5.0 < 4.0
        expression = Binary(
            Literal(5.0),
            Token(TokenType.LESS, "<", null, 1),
            Literal(4.0)
        )

        statements = listOf(Print(expression))

        interpreter.interpret(statements)
        assertEquals("false", outputStreamCaptor.toString().trim())
    }

    @Test
    fun `interprets declaration and assignments`() {
        // 3 + 4
        val expression = Binary(
            Literal(3.0),
            Token(TokenType.PLUS, "+", null, 1),
            Literal(4.0)
        )

        val variableStatements = listOf(
            // var myVar = 3 + 4;
            Var(Token(TokenType.IDENTIFIER, "myVar", null, 1), expression),

            // print myVar;
            Print(Variable(Token(TokenType.IDENTIFIER, "myVar", null, 2)))
        )

        interpreter.interpret(variableStatements)
        assertEquals("7", outputStreamCaptor.toString().trim())

        outputStreamCaptor.reset()

        val assignStatements = listOf(
            // myVar = 13;
            Expression(Assign(Token(TokenType.IDENTIFIER, "myVar", null, 3), Literal(13.0))),

            // print myVar;
            Print(Variable(Token(TokenType.IDENTIFIER, "myVar", null, 4)))
        )

        interpreter.interpret(assignStatements)
        assertEquals("13", outputStreamCaptor.toString().trim())

    }
}
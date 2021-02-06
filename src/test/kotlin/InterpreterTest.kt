import ca.alexleung.lox.Assign
import ca.alexleung.lox.Binary
import ca.alexleung.lox.Expression
import ca.alexleung.lox.Grouping
import ca.alexleung.lox.Interpreter
import ca.alexleung.lox.Literal
import ca.alexleung.lox.Parser
import ca.alexleung.lox.Print
import ca.alexleung.lox.Scanner
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

    @Test
    fun `interprets block statements`() {
        val source = """var a = "global a";
            var b = "global b";
            var c = "global c";
            {
              var a = "outer a";
              var b = "outer b";
              {
                var a = "inner a";
                print a;
                print b;
                print c;
              }
              print a;
              print b;
              print c;
            }
            print a;
            print b;
            print c;
            """
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("inner a", output[0].trim())
        assertEquals("outer b", output[1].trim())
        assertEquals("global c", output[2].trim())

        assertEquals("outer a", output[3].trim())
        assertEquals("outer b", output[4].trim())
        assertEquals("global c", output[5].trim())

        assertEquals("global a", output[6].trim())
        assertEquals("global b", output[7].trim())
        assertEquals("global c", output[8].trim())
    }

    @Test
    fun `interprets if statements`() {
        val source = """var a = 5;
            if (a > 3) {
              print "a: gt 3";
            } else {
              print "a: not gt 3";
            }
            
            if (a < 3) {
              print "a: lt 3";
            } else {
              print "a: not lt 3";
            }
            """
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("a: gt 3", output[0].trim())
        assertEquals("a: not lt 3", output[1].trim())
    }

    @Test
    fun `interprets logical operators`() {
        val source = """print "hi" or 2;
            print "hi" and 2;
            print nil or "yes";
            print nil and "yes";
            """
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("hi", output[0].trim())
        assertEquals("2", output[1].trim())
        assertEquals("yes", output[2].trim())
        assertEquals("nil", output[3].trim())
    }

    @Test
    fun `interprets while loops`() {
        val source = """var i = 0;
            while (i < 5) {
              print i;
              i = i + 1;
            }
            """
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("0", output[0].trim())
        assertEquals("1", output[1].trim())
        assertEquals("2", output[2].trim())
        assertEquals("3", output[3].trim())
        assertEquals("4", output[4].trim())
    }
}

import ca.alexleung.lox.Expr
import ca.alexleung.lox.Interpreter
import ca.alexleung.lox.Parser
import ca.alexleung.lox.Resolver
import ca.alexleung.lox.Scanner
import ca.alexleung.lox.Stmt
import ca.alexleung.lox.Token
import ca.alexleung.lox.TokenType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InterpreterTest {
    private val interpreter = Interpreter()
    private val resolver = Resolver(interpreter)
    private val outputTester = OutputTester()

    @BeforeEach
    fun setup() {
        outputTester.setup()
    }

    @Test
    fun `interprets print statement with arithmetic`() {
        // ((-333.0 / 3.0) + 6.67) * 2.0
        val expression = Expr.Binary(
            Expr.Binary(
                Expr.Binary(
                    Expr.Unary(
                        Token(TokenType.MINUS, "-", null, 1),
                        Expr.Literal(333.0)
                    ),
                    Token(TokenType.SLASH, "/", null, 1),
                    Expr.Literal(3.0)
                ),
                Token(TokenType.PLUS, "+", null, 1),
                Expr.Grouping(
                    Expr.Literal(6.67)
                )
            ),
            Token(TokenType.STAR, "*", null, 1),
            Expr.Literal(2.0)
        )

        val statements = listOf(Stmt.Print(expression))

        resolver.resolve(statements)
        interpreter.interpret(statements)
        outputTester.takeAndAssertOutput("-208.66")
    }

    @Test
    fun `interprets print statement with logic`() {
        // 5.0 > 4.0
        var expression = Expr.Binary(
            Expr.Literal(5.0),
            Token(TokenType.GREATER, ">", null, 1),
            Expr.Literal(4.0)
        )

        var statements = listOf(Stmt.Print(expression))

        resolver.resolve(statements)
        interpreter.interpret(statements)
        outputTester.takeAndAssertOutput("true")

        outputTester.clear()

        // 5.0 < 4.0
        expression = Expr.Binary(
            Expr.Literal(5.0),
            Token(TokenType.LESS, "<", null, 1),
            Expr.Literal(4.0)
        )

        statements = listOf(Stmt.Print(expression))

        resolver.resolve(statements)
        interpreter.interpret(statements)
        outputTester.takeAndAssertOutput("false")
    }

    @Test
    fun `interprets declaration and assignments`() {
        // 3 + 4
        val expression = Expr.Binary(
            Expr.Literal(3.0),
            Token(TokenType.PLUS, "+", null, 1),
            Expr.Literal(4.0)
        )

        val variableStatements = listOf(
            // var myVar = 3 + 4;
            Stmt.Var(Token(TokenType.IDENTIFIER, "myVar", null, 1), expression),

            // print myVar;
            Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "myVar", null, 2)))
        )

        resolver.resolve(variableStatements)
        interpreter.interpret(variableStatements)
        outputTester.takeAndAssertOutput("7")

        outputTester.clear()

        val assignStatements = listOf(
            // myVar = 13;
            Stmt.Expression(Expr.Assign(Token(TokenType.IDENTIFIER, "myVar", null, 3), Expr.Literal(13.0))),

            // print myVar;
            Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "myVar", null, 4)))
        )

        resolver.resolve(variableStatements)
        interpreter.interpret(assignStatements)
        outputTester.takeAndAssertOutput("13")
    }

    @Test
    fun `interprets block statements`() {
        val source = """|
            |var a = "global a";
            |var b = "global b";
            |var c = "global c";
            |{
            |  var a = "outer a";
            |  var b = "outer b";
            |  {
            |    var a = "inner a";
            |    print a;
            |    print b;
            |    print c;
            |  }
            |  print a;
            |  print b;
            |  print c;
            |}
            |print a;
            |print b;
            |print c;
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("inner a")
        outputTester.takeAndAssertOutput("outer b")
        outputTester.takeAndAssertOutput("global c")

        outputTester.takeAndAssertOutput("outer a")
        outputTester.takeAndAssertOutput("outer b")
        outputTester.takeAndAssertOutput("global c")

        outputTester.takeAndAssertOutput("global a")
        outputTester.takeAndAssertOutput("global b")
        outputTester.takeAndAssertOutput("global c")
    }

    @Test
    fun `interprets if statements`() {
        val source = """
            |var a = 5;
            |if (a > 3) {
            |  print "a: gt 3";
            |} else {
            |  print "a: not gt 3";
            |}
            |
            |if (a < 3) {
            |  print "a: lt 3";
            |} else {
            |  print "a: not lt 3";
            |}
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("a: gt 3")
        outputTester.takeAndAssertOutput("a: not lt 3")
    }

    @Test
    fun `interprets logical operators`() {
        val source = """
            |print "hi" or 2;
            |print "hi" and 2;
            |print nil or "yes";
            |print nil and "yes";
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("hi")
        outputTester.takeAndAssertOutput("2")
        outputTester.takeAndAssertOutput("yes")
        outputTester.takeAndAssertOutput("nil")
    }

    @Test
    fun `interprets while loops`() {
        val source = """
            |var i = 0;
            |while (i < 5) {
            |  print i;
            |  i = i + 1;
            |}
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("0")
        outputTester.takeAndAssertOutput("1")
        outputTester.takeAndAssertOutput("2")
        outputTester.takeAndAssertOutput("3")
        outputTester.takeAndAssertOutput("4")
    }

    @Test
    fun `interprets for loops`() {
        val source = """
            |for (var i = 0; i < 5; i = i + 1) {
            |  print i;
            |}
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("0")
        outputTester.takeAndAssertOutput("1")
        outputTester.takeAndAssertOutput("2")
        outputTester.takeAndAssertOutput("3")
        outputTester.takeAndAssertOutput("4")
    }
}

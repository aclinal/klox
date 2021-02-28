import ca.alexleung.lox.Interpreter
import ca.alexleung.lox.Lox
import ca.alexleung.lox.Parser
import ca.alexleung.lox.Resolver
import ca.alexleung.lox.Scanner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ClassTest {
    private val interpreter = Interpreter()
    private val resolver = Resolver(interpreter)
    private val outputTester = OutputTester()

    @BeforeEach
    fun setup() {
        outputTester.setup()
    }

    @Test
    fun `printing classes`() {
        val source = """
            |class DevonshireCream {
            |    serveOn() {
            |        return "Scones";
            |    }
            |}
            |print DevonshireCream; // Prints "DevonshireCream".
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("DevonshireCream")
    }

    @Test
    fun `printing class instances`() {
        val source = """
            |class Bagel {}
            |var bagel = Bagel();
            |print bagel; // Prints "Bagel instance".
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("Bagel instance")
    }

    @Test
    fun `get and set instance fields`() {
        val source = """
            |class Bagel {}
            |var bagel = Bagel();
            |bagel.foo = "bar";
            |print bagel.foo; // Prints "bar".
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("bar")
    }
}

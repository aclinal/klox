import ca.alexleung.lox.Interpreter
import ca.alexleung.lox.Lox
import ca.alexleung.lox.Parser
import ca.alexleung.lox.Resolver
import ca.alexleung.lox.Scanner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ResolverTest {
    private val interpreter = Interpreter()
    private val resolver = Resolver(interpreter)
    private val outputStreamCaptor = ByteArrayOutputStream()

    @BeforeEach
    fun setup() {
        System.setOut(PrintStream(outputStreamCaptor))
    }

    @Test
    fun `resolves basic scope`() {
        val source = """var a = "global";
            |{
            |  fun showA() {
            |  print a;
            |}
            |  
            |showA(); // "global"
            |var a = "block";
            |showA(); // "global"
            |}
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("global", output[0].trim())
        assertEquals("global", output[1].trim())
    }

    @Test
    fun `top-level return is an error`() {
        val source = """return "hello world";
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        assert(Lox.hadError)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("[line 1] Error at 'return': Can't return from top-level code.", output[0].trim())
    }

    @Test
    fun `use declaration in initialization`() {
        val source = """{
            |  var a = a;
            |}
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        assert(Lox.hadError)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("[line 2] Error at 'a': Can't read local variable in its own initializer.", output[0].trim())
    }
}

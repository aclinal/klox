import ca.alexleung.lox.Interpreter
import ca.alexleung.lox.Parser
import ca.alexleung.lox.Resolver
import ca.alexleung.lox.Scanner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class FunctionTest {
    private val interpreter = Interpreter()
    private val resolver = Resolver(interpreter)
    private val outputStreamCaptor = ByteArrayOutputStream()

    @BeforeEach
    fun setup() {
        System.setOut(PrintStream(outputStreamCaptor))
    }

    @Test
    fun `basic`() {
        val source = """
            fun add(a, b, c) {
              print a + b + c;
            }

            add(1, 2, 3);
            """
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("6", output[0].trim())
    }

    @Test
    fun `printing functions`() {
        val source = """
            fun add(a, b) {
              print a + b;
            }

            print add;
            """
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')

        // The clock isn't fixed, so just assert that it's non-null.
        assertEquals("<fn add>", output[0].trim())
    }

    @Test
    fun `native functions`() {
        val source = """
            print clock;
            print clock();
            """
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')

        // Native function is identified.
        assertEquals("<native fn>", output[0].trim())

        // The time isn't fixed, so just assert that it's non-null.
        assertNotNull(output[1].trim())
    }

    @Test
    fun `recursive function calls`() {
        val source = """
            fun count(n) {
              if (n > 1) count(n - 1);
              print n;
            }

            count(3);
            """
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("1", output[0].trim())
        assertEquals("2", output[1].trim())
        assertEquals("3", output[2].trim())
    }

    @Test
    fun `functions with return statements`() {
        val source = """
            fun count(n) {
              while (n < 100) {
                if (n == 3) return n; // <--
                n = n + 1;
              }
            }

            print count(1);
            """
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("3", output[0].trim())
    }

    @Test
    fun `recursive functions with return statements`() {
        val source = """
            fun fib(n) {
              if (n <= 1) return n;
              return fib(n - 2) + fib(n - 1);
            }

            for (var i = 0; i < 20; i = i + 1) {
              print fib(i);
            }
            """
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("4181", output[19].trim())
    }

    @Test
    fun `functions with closures`() {
        val source = """
            fun makeCounter() {
              var i = 0;
              fun count() {
                i = i + 1;
                print i;
              }

              return count;
            }

            var counter = makeCounter();
            counter(); // "1".
            counter(); // "2".
            """
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        val output = outputStreamCaptor.toString().split('\n')
        assertEquals("1", output[0].trim())
        assertEquals("2", output[1].trim())
    }
}

import ca.alexleung.lox.Interpreter
import ca.alexleung.lox.Parser
import ca.alexleung.lox.Resolver
import ca.alexleung.lox.Scanner
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FunctionTest {
    private val interpreter = Interpreter()
    private val resolver = Resolver(interpreter)
    private val outputTester = OutputTester()

    @BeforeEach
    fun setup() {
        outputTester.setup()
    }

    @Test
    fun `basic`() {
        val source = """
            |fun add(a, b, c) {
            |  print a + b + c;
            |}
            |
            |add(1, 2, 3);
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("6")
    }

    @Test
    fun `printing functions`() {
        val source = """
            |fun add(a, b) {
            |  print a + b;
            |}
            |
            |print add;
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("<fn add>")
    }

    @Test
    fun `native functions`() {
        val source = """
            |print clock;
            |print clock();
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        // Native function is identified.
        outputTester.takeAndAssertOutput("<native fn>")

        // The time isn't fixed, so just assert that it's non-null.
        assertNotNull(outputTester.take())
    }

    @Test
    fun `recursive function calls`() {
        val source = """
            |fun count(n) {
            |  if (n > 1) count(n - 1);
            |  print n;
            |}
            |
            |count(3);
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("1")
        outputTester.takeAndAssertOutput("2")
        outputTester.takeAndAssertOutput("3")
    }

    @Test
    fun `functions with return statements`() {
        val source = """
            |fun count(n) {
            |  while (n < 100) {
            |    if (n == 3) return n; // <--
            |    n = n + 1;
            |  }
            |}
            |
            |print count(1);
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("3")
    }

    @Test
    fun `recursive functions with return statements`() {
        val source = """
            |fun fib(n) {
            |  if (n <= 1) return n;
            |  return fib(n - 2) + fib(n - 1);
            |}
            |
            |for (var i = 0; i < 20; i = i + 1) {
            |  print fib(i);
            |}
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        // Sink the first 19 fibonacci numbers.
        for (i in 0 until 19) {
            outputTester.take()
        }
        outputTester.takeAndAssertOutput("4181")
    }

    @Test
    fun `functions with closures`() {
        val source = """
            |fun makeCounter() {
            |  var i = 0;
            |  fun count() {
            |    i = i + 1;
            |    print i;
            |  }
            |  
            |  return count;
            |}
            |
            |var counter = makeCounter();
            |counter(); // "1".
            |counter(); // "2".
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("1")
        outputTester.takeAndAssertOutput("2")
    }
}

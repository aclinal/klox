import ca.alexleung.lox.Interpreter
import ca.alexleung.lox.Parser
import ca.alexleung.lox.Resolver
import ca.alexleung.lox.Scanner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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

    @Test
    fun `stateless method calls`() {
        val source = """
            |class Bacon {
            | eat() {
            |   print "Crunch crunch crunch!";
            | }
            |}
            |
            |Bacon().eat();
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("Crunch crunch crunch!")
    }

    @Test
    fun `methods capture this`() {
        val source = """
            |class Egotist {
            | speak() {
            |   print this;
            | }
            |}
            |
            |var method = Egotist().speak;
            |method();
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("Egotist instance")
    }

    @Test
    fun `methods can access fields`() {
        val source = """
            |class Cake {
            |  taste() {
            |    var adjective = "delicious";
            |    print "The " + this.flavor + " cake is " + adjective + "!";
            |  }
            |}
            |
            |var cake = Cake();
            |cake.flavor = "German chocolate";
            |cake.taste(); // Prints "The German chocolate cake is delicious!".
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("The German chocolate cake is delicious!")
    }

    @Test
    fun `methods can return callbacks that capture this`() {
        val source = """
            |class Thing {
            |  getCallback() {
            |    fun localFunction() {
            |      print this;
            |    }
            |    
            |    return localFunction;
            |  }
            |}
            |
            |var callback = Thing().getCallback();
            |callback();
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("Thing instance")
    }
}

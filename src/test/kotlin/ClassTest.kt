import ca.alexleung.lox.Interpreter
import ca.alexleung.lox.Lox
import ca.alexleung.lox.Parser
import ca.alexleung.lox.Resolver
import ca.alexleung.lox.Scanner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

    @Test
    fun `invalid uses of this`() {
        val source = """
            |fun notAMethod() {
            |  print this;
            |}
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        assertTrue(Lox.hadError)
    }

    @Test
    fun `class initalizers`() {
        val source = """
            |class Foo {
            |  init(bar) {
            |    print bar;
            |  }
            |}
            |var foo = Foo("first");
            |print foo.init("second"); // init always returns an instance
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("first")
        outputTester.takeAndAssertOutput("second")
        outputTester.takeAndAssertOutput("Foo instance")
    }

    @Test
    fun `class initializer invalid return statements`() {
        val source = """
            |class Foo {
            |  init() {
            |    return "invalid";
            |  }
            |}
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        assertTrue(Lox.hadError)
        outputTester.takeAndAssertOutput("[line 3] Error at 'return': Can't return a value from an initializer.")
    }

    @Test
    fun `class initializer valid early return statements`() {
        val source = """
            |class Foo {
            |  init() {
            |    return;
            |    print "Can't see me!"; // unreachable
            |  }
            |}
            |
            |print Foo();
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        assertEquals(1, outputTester.size())
        outputTester.takeAndAssertOutput("Foo instance")
    }

    @Test
    fun `basic inheritance`() {
        val source = """
            |class Doughnut {
            |  cook() {
            |    print "Fry until golden brown.";
            |  }
            |}
            |class BostonCream < Doughnut {}
            |BostonCream().cook();
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("Fry until golden brown.")
    }

    @Test
    fun `inheritance - can call superclass method`() {
        val source = """
            |class Doughnut {
            |  cook() {
            |    print "Fry until golden brown.";
            |  }
            |}
            |class BostonCream < Doughnut {
            |  cook() {
            |    super.cook();
            |    print "Pipe full of custard and coat with chocolate.";
            |  }
            |}
            |BostonCream().cook();
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        outputTester.takeAndAssertOutput("Fry until golden brown.")
        outputTester.takeAndAssertOutput("Pipe full of custard and coat with chocolate.")
    }

    @Test
    fun `self-inhertance is disallowed`() {
        val source = """
            |class Oops < Oops {}
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        assertTrue(Lox.hadError)
        outputTester.takeAndAssertOutput(
            "[line 1] Error at 'Oops': A class can't inherit from itself."
        )
    }

    @Test
    fun `inhertance from non-class is disallowed`() {
        val source = """
            |var NotAClass = "I am totally not a class";
            |class Subclass < NotAClass {}
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        assertTrue(Lox.hadRuntimeError)
        outputTester.takeAndAssertOutput("Superclass must be a class.")
    }

    @Test
    fun `invalid use of super - not in a subclass`() {
        val source = """
            |class Eclair {
            |  cook() {
            |    super.cook();
            |    print "Pipe full of crème pâtissière.";
            |  }
            |}
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        assertTrue(Lox.hadRuntimeError)
        outputTester.takeAndAssertOutput("[line 3] Error at 'super': Can't use 'super' in a class with no superclass.")
    }

    @Test
    fun `invalid use of super - outside a class`() {
        val source = """
            |super.notEvenInAClass();
            |""".trimMargin()
        val tokens = Scanner(source).scanTokens()
        val statements = Parser(tokens).parse()
        resolver.resolve(statements)
        interpreter.interpret(statements)

        assertTrue(Lox.hadRuntimeError)
        outputTester.takeAndAssertOutput("[line 1] Error at 'super': Can't use 'super' outside of a class.")
    }
}

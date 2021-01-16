import ca.alexleung.lox.AstPrinter
import ca.alexleung.lox.Parser
import ca.alexleung.lox.Scanner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ParserTest {
    @Test
    fun `valid expression is parsed correctly`() {
        val source = """"some string" + (1 + 1) / 2 + 5""";
        val scanner = Scanner(source)

        val parser = Parser(scanner.scanTokens())
        val expression = parser.parse()
        assertNotNull(expression)
        val expressionString = AstPrinter().print(expression!!)
        assertEquals(expressionString, "(+ (+ some string (/ (group (+ 1.0 1.0)) 2.0)) 5.0)")
    }
}

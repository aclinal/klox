import org.junit.jupiter.api.Assertions.assertEquals
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class OutputTester {
    private val outputStreamCaptor = ByteArrayOutputStream()
    private val capturedOutput = mutableListOf<String>()

    fun setup() {
        System.setOut(PrintStream(outputStreamCaptor))
    }

    fun takeAndAssertOutput(expected: String) {
        // Consume the latest stream output.
        val actualOutput = outputStreamCaptor.toString().split('\n')
        for (actual in actualOutput) {
            capturedOutput.add(actual.trim())
        }

        assertEquals(expected, capturedOutput.removeAt(0))
    }
}
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
        consumeLatestOutput()
        assertEquals(expected, capturedOutput.removeAt(0))
    }

    fun take(): String {
        consumeLatestOutput()
        return capturedOutput.removeAt(0)
    }

    fun size(): Int {
        consumeLatestOutput()
        return capturedOutput.size
    }

    fun clear() {
        consumeLatestOutput()
        capturedOutput.clear()
    }

    private fun consumeLatestOutput() {
        val actualOutput = outputStreamCaptor.toString().split('\n')
        for (actual in actualOutput) {
            val trimmedActual = actual.trim()
            if (trimmedActual.isNotEmpty()) {
                capturedOutput.add(actual.trim())
            }
        }
        outputStreamCaptor.reset()
    }
}
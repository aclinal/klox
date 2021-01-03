package ca.alexleung.lox

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

class Lox() {
    var hadError = false;

    companion object {
        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        fun report(line: Int, where: String, message: String) {
            println("[line $line] Error$where: $message")
        }
    }

    fun runFile(path: String) {
        val bytes = Files.readAllBytes(Paths.get(path))
        run(String(bytes, Charset.defaultCharset()))

        // Propagate error out as an exit code.
        if (hadError) {
            exitProcess(65)
        }
    }

    fun runPrompt() {
        while (true) {
            println("> ")
            val line = readLine() ?: break
            run(line)
            hadError = false;
        }
    }

    private fun run(source: String) {
        val scanner = Scanner(source)

        val tokens = scanner.scanTokens();

        for (token in tokens) {
            println(token)
        }
    }
}
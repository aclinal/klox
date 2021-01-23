package ca.alexleung.lox

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

class Lox() {
    private val interpreter = Interpreter()

    companion object {
        var hadError = false;
        var hadRuntimeError = false

        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        fun report(line: Int, where: String, message: String) {
            println("[line $line] Error$where: $message")
        }

        fun error(token: Token, message: String) {
            if (token.type == TokenType.EOF) {
                report(token.line, " at end", message)
            } else {
                report(token.line, " at '${token.lexeme}'", message)
            }
        }

        fun runtimeError(error: RuntimeError) {
            println("${error.message}\n[line ${error.token.line}]")
            hadRuntimeError = true
        }
    }

    fun runFile(path: String) {
        val bytes = Files.readAllBytes(Paths.get(path))
        run(String(bytes, Charset.defaultCharset()))

        // Propagate error out as an exit code.
        if (hadError) {
            exitProcess(65)
        }
        if (hadError) {
            exitProcess(70)
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

        val parser = Parser(tokens)
        val expression = parser.parse()

        if (hadError) {
            return
        }

        if (expression == null) {
            return
        }

        interpreter.interpret(expression)
    }
}

package ca.alexleung.lox

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    println(args)
    if (args.size > 1) {
        println("Usage: klox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        runFile(args[0])
    } else {
        runPrompt()
    }
}

private fun runFile(path: String) {
    val bytes = Files.readAllBytes(Paths.get(path))
    run(String(bytes, Charset.defaultCharset()))
}

private fun run(bytes: String) {
    println("Read: $bytes")
}

private fun runPrompt() {
    while (true) {
        println("> ")
        val line = readLine() ?: break
        run(line)
    }
}

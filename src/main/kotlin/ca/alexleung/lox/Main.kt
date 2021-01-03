package ca.alexleung.lox

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val lox = Lox()

    if (args.size > 1) {
        println("Usage: klox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        lox.runFile(args[0])
    } else {
        lox.runPrompt()
    }
}


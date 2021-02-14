package ca.alexleung.lox

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val lox = Lox()

    when {
        args.size > 1 -> {
            println("Usage: klox [script]")
            exitProcess(64)
        }
        args.size == 1 -> {
            lox.runFile(args[0])
        }
        else -> {
            lox.runPrompt()
        }
    }
}


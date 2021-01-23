package ca.alexleung.lox

class RuntimeError(val token: Token, message: String) : RuntimeException(message)


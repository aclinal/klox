package ca.alexleung.lox

class Environment {
    private val values = mutableMapOf<String, Any?>()

    fun get(name: Token): Any? {
        if (!values.containsKey(name.lexeme)) {
            throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
        }
        return values[name.lexeme]
    }

    fun define(name: String, value: Any?) {
        values[name] = value
    }
}
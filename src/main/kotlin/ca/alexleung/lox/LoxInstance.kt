package ca.alexleung.lox

class LoxInstance(private val loxClass: LoxClass) {
    private val fields = mutableMapOf<String, Any?>()

    fun get(name: Token): Any? {
        if (name.lexeme in fields) {
            return fields[name.lexeme]
        }
        throw RuntimeError(name, "Undefined property '${name.lexeme}'.")
    }

    fun set(name: Token, value: Any?) {
        fields[name.lexeme] = value
    }

    override fun toString(): String = "${loxClass.name} instance"
}
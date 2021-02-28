package ca.alexleung.lox

class LoxClass(val name: String, private val methods: Map<String, LoxFunction>) : LoxCallable {
    override fun toString(): String = name

    override fun arity(): Int = 0

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any = LoxInstance(this)

    fun findMethod(name: String) = methods[name]
}

package ca.alexleung.lox

class LoxClass(
    val name: String, val superclass: LoxClass?, private val methods: Map<String, LoxFunction>
) : LoxCallable {
    override fun toString(): String = name

    override fun arity(): Int = findMethod(INITIALIZER_NAME)?.arity() ?: 0

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        val instance = LoxInstance(this)
        findMethod(INITIALIZER_NAME)?.bind(instance)?.call(interpreter, arguments)
        return instance
    }

    fun findMethod(name: String): LoxFunction? {
        if (methods.containsKey(name)) {
            return methods[name]
        }

        return superclass?.findMethod(name)
    }

    companion object {
        const val INITIALIZER_NAME = "init"
    }
}

package ca.alexleung.lox

class LoxFunction(
    private val declaration: Stmt.Function
) : LoxCallable {
    override fun arity(): Int = declaration.params.size

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        // Make all arguments available in a new environment context.
        val environment = Environment(interpreter.globals)
        for ((index, param) in declaration.params.withIndex()) {
            environment.define(param.lexeme, arguments[index])
        }

        // Execute the function.
        interpreter.executeBlock(declaration.body, environment)

        // TODO: Return a value.
        return null
    }

    override fun toString(): String = "<fn ${declaration.name.lexeme}>"
}

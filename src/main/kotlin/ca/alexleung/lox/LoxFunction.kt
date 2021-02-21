package ca.alexleung.lox

class LoxFunction(
    private val declaration: Stmt.Function,
    private val closure: Environment
) : LoxCallable {
    override fun arity(): Int = declaration.params.size

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        // Make all arguments available in a new environment context.
        val environment = Environment(closure)
        for ((index, param) in declaration.params.withIndex()) {
            environment.define(param.lexeme, arguments[index])
        }

        // Execute the function.
        // Exceptions are used for control-flow due to the recursive
        // nature of the klox tree-walk implementation.
        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (returnValue: Return) {
            return returnValue.value
        }

        return null
    }

    override fun toString(): String = "<fn ${declaration.name.lexeme}>"
}

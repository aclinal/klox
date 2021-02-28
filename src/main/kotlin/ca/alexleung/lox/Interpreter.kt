package ca.alexleung.lox

import java.util.IdentityHashMap

class Interpreter : Expr.Visitor<Any?>, Stmt.Visitor<Unit> {
    private val globals = Environment()
    private var environment = globals

    // Use an IdentityHashMap to distinguish between identical expressions on the same line that have
    // semantically different scopes.
    private val locals = IdentityHashMap<Expr, Int>()

    constructor() {
        globals.define("clock", object : LoxCallable {
            override fun arity(): Int = 0

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? =
                System.currentTimeMillis() / 1000.0

            override fun toString(): String = "<native fn>"
        })
    }

    fun interpret(stmts: List<Stmt>) {
        try {
            for (stmt in stmts) {
                execute(stmt)
            }
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }

    fun resolve(expr: Expr, depth: Int) {
        locals[expr] = depth
    }

    override fun visit(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)
        if (locals.containsKey(expr)) {
            // Note that this block has a high degree of coupling to the Resolver class.
            val distance = locals[expr]!!
            environment.assignAt(distance, expr.name, value)
        } else {
            globals.assign(expr.name, value)
        }
        return value
    }

    override fun visit(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.PLUS -> {
                if (left is Double && right is Double) {
                    left + right
                } else if (left is String && right is String) {
                    left + right
                } else {
                    null
                }
            }
            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) - (right as Double)
            }
            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) / (right as Double)
            }
            TokenType.STAR -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) * (right as Double)
            }
            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) > (right as Double)
            }
            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) >= (right as Double)
            }
            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) < (right as Double)
            }
            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) <= (right as Double)
            }
            TokenType.BANG_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                left != right
            }
            TokenType.EQUAL_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                left == right
            }
            else -> null
        }
    }

    override fun visit(expr: Expr.Call): Any? {
        val function = evaluate(expr.callee)
        val arguments = expr.arguments.map { evaluate(it) }

        if (function !is LoxCallable) {
            throw RuntimeError(expr.paren, "Can only call functions and classes.")
        }

        if (arguments.size != function.arity()) {
            throw RuntimeError(expr.paren, "Expected ${function.arity()} arguments but got ${arguments.size}.")
        }

        return function.call(this, arguments)
    }

    override fun visit(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visit(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visit(expr: Expr.Logical): Any? {
        val left = evaluate(expr.left)
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) {
                // Short-circuit when LHS is truthy in ORs.
                return left
            }
        } else {
            // Should always be an AND.
            assert(expr.operator.type == TokenType.AND)

            if (!isTruthy(left)) {
                // Short-circuit when LHS is falsy in ANDs.
                return left
            }
        }

        return evaluate(expr.right)
    }

    override fun visit(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                -(right as Double)
            }
            else -> null
        }
    }

    override fun visit(expr: Expr.Variable): Any? {
        return lookUpVariable(expr.name, expr)
    }

    private fun lookUpVariable(name: Token, expr: Expr): Any? {
        return if (locals.containsKey(expr)) {
            // Note that this block has a high degree of coupling to the Resolver class.
            val distance = locals[expr]!!
            environment.getAt(distance, name.lexeme)
        } else {
            globals.get(name)
        }
    }

    override fun visit(stmt: Stmt.Block) {
        executeBlock(stmt.statements, Environment(environment))
    }

    override fun visit(stmt: Stmt.Class) {
        environment.define(stmt.name.lexeme, null)
        val loxClass = LoxClass(stmt.name.lexeme)
        environment.assign(stmt.name, loxClass)
    }

    override fun visit(stmt: Stmt.Expression) {
        evaluate(stmt.expr)
    }

    override fun visit(stmt: Stmt.Function) {
        val function = LoxFunction(stmt, environment)
        environment.define(stmt.name.lexeme, function)
    }

    override fun visit(stmt: Stmt.If) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch)
        }
    }

    override fun visit(stmt: Stmt.Print) {
        val value = evaluate(stmt.expr)
        println(stringify(value))
    }

    override fun visit(stmt: Stmt.Return) {
        val value = if (stmt.value != null) evaluate(stmt.value) else null
        throw Return(value)
    }

    override fun visit(stmt: Stmt.Var) {
        // Lox allows null values for uninitialized variable declarations.
        val value = stmt.initializer?.let { evaluate(it) }

        environment.define(stmt.name.lexeme, value)
    }

    override fun visit(stmt: Stmt.While) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun execute(stmt: Stmt) {
        stmt.accept(this)
    }

    fun executeBlock(statements: List<Stmt>, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment

            for (statement in statements) {
                execute(statement)
            }
        } finally {
            this.environment = previous
        }
    }

    private fun isTruthy(any: Any?): Boolean {
        return any?.let { it as? Boolean ?: true } ?: false
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) {
            return;
        }

        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) {
            return;
        }

        throw RuntimeError(operator, "Operands must be numbers.")
    }

    private fun stringify(any: Any?): String {
        if (any == null) {
            return "nil"
        }

        if (any is Double) {
            val text = any.toString()
            if (text.endsWith(".0")) {
                return text.substring(0, text.length - 2)
            }
            return text
        }

        return any.toString()
    }
}

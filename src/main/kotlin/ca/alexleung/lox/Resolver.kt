package ca.alexleung.lox

class Resolver(
    private val interpreter: Interpreter
) : Expr.Visitor<Unit>, Stmt.Visitor<Unit> {
    // Track each variable in scopes and whether or not it is ready to use.
    private val scopes = mutableListOf<MutableMap<String, Boolean>>()

    private var currentFunction = FunctionType.NONE

    private enum class FunctionType {
        NONE,
        FUNCTION,
        METHOD
    }

    fun resolve(statements: List<Stmt>) {
        for (statement in statements) {
            resolve(statement)
        }
    }

    private fun resolve(stmt: Stmt) {
        stmt.accept(this)
    }

    private fun resolve(expr: Expr) {
        expr.accept(this)
    }

    override fun visit(expr: Expr.Assign) {
        resolve(expr.value)
        resolveLocal(expr, expr.name)
    }

    override fun visit(expr: Expr.Binary) {
        resolve(expr.left)
        resolve(expr.right)
    }

    override fun visit(expr: Expr.Call) {
        resolve(expr.callee)
        for (argument in expr.arguments) {
            resolve(argument)
        }
    }

    override fun visit(expr: Expr.Get) {
        resolve(expr.obj)
    }

    override fun visit(expr: Expr.Grouping) {
        resolve(expr.expression)
    }

    override fun visit(expr: Expr.Literal) {
        // Nothing to do.
    }

    override fun visit(expr: Expr.Logical) {
        resolve(expr.left)
        resolve(expr.right)
    }

    override fun visit(expr: Expr.Set) {
        resolve(expr.value)
        resolve(expr.obj)
    }

    override fun visit(expr: Expr.Unary) {
        resolve(expr.right)
    }

    override fun visit(expr: Expr.Variable) {
        if (scopes.isNotEmpty() && !scopes[scopes.size - 1].getOrDefault(expr.name.lexeme, true)) {
            Lox.error(expr.name, "Can't read local variable in its own initializer.")
        }

        resolveLocal(expr, expr.name)
    }

    override fun visit(stmt: Stmt.Block) {
        beginScope()
        resolve(stmt.statements)
        endScope()
    }

    override fun visit(stmt: Stmt.Class) {
        declare(stmt.name)
        define(stmt.name)

        for (method in stmt.methods) {
            val declaration = FunctionType.METHOD
            resolveFunction(method, declaration)
        }
    }

    override fun visit(stmt: Stmt.Expression) {
        resolve(stmt.expr)
    }

    override fun visit(stmt: Stmt.Function) {
        declare(stmt.name)
        define(stmt.name)
        resolveFunction(stmt, FunctionType.FUNCTION)
    }

    override fun visit(stmt: Stmt.If) {
        resolve(stmt.condition)
        resolve(stmt.thenBranch)
        if (stmt.elseBranch != null) {
            resolve(stmt.elseBranch)
        }
    }

    override fun visit(stmt: Stmt.Print) {
        resolve(stmt.expr)
    }

    override fun visit(stmt: Stmt.Return) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Can't return from top-level code.")
        }

        if (stmt.value != null) {
            resolve(stmt.value)
        }
    }

    override fun visit(stmt: Stmt.Var) {
        declare(stmt.name)

        // Resolve the initializer to handle cases where the
        // local variable refers to variables with the same name.
        if (stmt.initializer != null) {
            resolve(stmt.initializer)
        }

        define(stmt.name)
    }

    override fun visit(stmt: Stmt.While) {
        resolve(stmt.condition)
        resolve(stmt.body)
    }

    private fun beginScope() {
        scopes.add(mutableMapOf())
    }

    private fun endScope() {
        scopes.removeAt(scopes.size - 1)
    }

    private fun declare(name: Token) {
        if (scopes.isEmpty()) {
            return
        }

        val scope = scopes[scopes.size - 1]

        // Disallow re-declaring local variables at the same scope.
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "Already has variable with this name in this scope.")
        }

        scope[name.lexeme] = false
    }

    private fun define(name: Token) {
        if (scopes.isEmpty()) {
            return
        }

        scopes[scopes.size - 1][name.lexeme] = true
    }

    private fun resolveLocal(expr: Expr, name: Token) {
        for ((index, scope) in scopes.withIndex().reversed()) {
            if (scope.containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size - 1 - index)
                return
            }
        }
    }

    private fun resolveFunction(function: Stmt.Function, functionType: FunctionType) {
        val enclosingFunction = currentFunction
        try {
            currentFunction = functionType
            beginScope()
            for (param in function.params) {
                declare(param)
                define(param)
            }
            resolve(function.body)
            endScope()
        } finally {
            currentFunction = enclosingFunction
        }
    }
}

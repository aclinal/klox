package ca.alexleung.lox

sealed class Stmt {
    // Implementations of Stmt.Visitor<R> define actions that can be taken on statements.
    interface Visitor<R> {
        fun visit(stmt: Block): R
        fun visit(stmt: Expression): R
        fun visit(stmt: If): R
        fun visit(stmt: Print): R
        fun visit(stmt: Var): R
    }

    fun <R> accept(visitor: Visitor<R>): R {
        return when (this) {
            is Block -> visitor.visit(this)
            is Expression -> visitor.visit(this)
            is If -> visitor.visit(this)
            is Print -> visitor.visit(this)
            is Var -> visitor.visit(this)
        }
    }
}

// A block statement, delimited by braces (e.g., { 1 + 2; }).
data class Block(val statements: List<Stmt>) : Stmt()

// An expression statement (e.g., 4 + 8;).
data class Expression(val expr: Expr) : Stmt()

// An if-else statement (e.g., if (...) {...} else {...}).
data class If(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?) : Stmt()

// A print statement (e.g., print 5;).
data class Print(val expr: Expr) : Stmt()

// A variable declaration statement (e.g., var myVar = 5;).
data class Var(val name: Token, val initializer: Expr?) : Stmt()

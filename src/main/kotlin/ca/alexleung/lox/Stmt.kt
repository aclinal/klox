package ca.alexleung.lox

sealed class Stmt {
    // Implementations of Stmt.Visitor<R> define actions that can be taken on statements.
    interface Visitor<R> {
        fun visit(stmt: Expression): R
        fun visit(stmt: Print): R
    }

    fun <R> accept(visitor: Visitor<R>): R {
        return when (this) {
            is Expression -> visitor.visit(this)
            is Print -> visitor.visit(this)
        }
    }
}

// An expression statement (e.g., 4 + 8;).
data class Expression(val expr: Expr) : Stmt()

// A print statement (e.g., print 5).
data class Print(val expr: Expr) : Stmt()

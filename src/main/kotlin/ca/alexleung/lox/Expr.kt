package ca.alexleung.lox

sealed class Expr {
    interface Visitor<R> {
        fun visit(expr: Binary): R
        fun visit(expr: Grouping): R
        fun visit(expr: Literal): R
        fun visit(expr: Unary): R
    }

    fun <R> accept(visitor: Visitor<R>): R {
        return when (this) {
            is Binary -> visitor.visit(this)
            is Grouping -> visitor.visit(this)
            is Literal -> visitor.visit(this)
            is Unary -> visitor.visit(this)
        }
    }
}

data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr()
data class Grouping(val expression: Expr) : Expr()
data class Literal(val value: Any?) : Expr()
data class Unary(val operator: Token, val right: Expr) : Expr()

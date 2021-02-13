package ca.alexleung.lox

sealed class Expr {
    // Implementations of Expr.Visitor<R> define actions that can be taken on expressions.
    interface Visitor<R> {
        fun visit(expr: Assign): R
        fun visit(expr: Binary): R
        fun visit(expr: Grouping): R
        fun visit(expr: Literal): R
        fun visit(expr: Logical): R
        fun visit(expr: Unary): R
        fun visit(expr: Variable): R
    }

    fun <R> accept(visitor: Visitor<R>): R {
        return when (this) {
            is Assign -> visitor.visit(this)
            is Binary -> visitor.visit(this)
            is Grouping -> visitor.visit(this)
            is Literal -> visitor.visit(this)
            is Logical -> visitor.visit(this)
            is Unary -> visitor.visit(this)
            is Variable -> visitor.visit(this)
        }
    }

    // An assignment expression (e.g., a = 4).
    data class Assign(val name: Token, val value: Expr) : Expr()

    // A binary expression (e.g., 4 + 8).
    data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr()

    // An expression encapsulated in parentheses.
    data class Grouping(val expression: Expr) : Expr()

    // A literal, including null values.
    data class Literal(val value: Any?) : Expr()

    // A logical expression (e.g., (a and b)).
    data class Logical(val left: Expr, val operator: Token, val right: Expr) : Expr()

    // A unary operator that prefixes an expression (e.g., !).
    data class Unary(val operator: Token, val right: Expr) : Expr()

    // A variable expression statement (e.g., myVar).
    data class Variable(val name: Token) : Expr()

}

package ca.alexleung.lox

class AstPrinter : Expr.Visitor<String> {
    // Visits expr and prints out a string representation.
    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    override fun visit(expr: Assign): String {
        return "(= ${expr.name.lexeme} ${expr.value.accept(this)}"
    }

    override fun visit(expr: Binary): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visit(expr: Grouping): String {
        return parenthesize("group", expr.expression)
    }

    override fun visit(expr: Literal): String {
        return expr.value?.toString() ?: "nil"
    }

    override fun visit(expr: Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    override fun visit(expr: Variable): String {
        return expr.name.lexeme
    }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val builder = StringBuilder()

        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ")
            builder.append(expr.accept(this))
        }
        builder.append(")")

        return builder.toString()
    }
}
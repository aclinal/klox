package ca.alexleung.lox

class AstPrinter : Expr.Visitor<String> {
    // Visits expr and prints out a string representation.
    fun print(expr: Expr): String = expr.accept(this)

    override fun visit(expr: Expr.Assign) = "(= ${expr.name.lexeme} ${expr.value.accept(this)}"

    override fun visit(expr: Expr.Binary) = parenthesize(expr.operator.lexeme, expr.left, expr.right)

    override fun visit(expr: Expr.Call) = parenthesize("call(${print(expr.callee)}", *expr.arguments.toTypedArray())

    override fun visit(expr: Expr.Get) = "${expr.obj.accept(this)}.${expr.name.lexeme}"

    override fun visit(expr: Expr.Grouping) = parenthesize("group", expr.expression)

    override fun visit(expr: Expr.Literal) = expr.value?.toString() ?: "nil"

    override fun visit(expr: Expr.Logical) = parenthesize(expr.operator.lexeme, expr.left, expr.right)

    override fun visit(expr: Expr.Set) = "(= ${expr.obj.accept(this)}.${expr.name.lexeme} ${expr.value.accept(this)})"

    override fun visit(expr: Expr.Super) = "${expr.keyword}.${expr.method}"

    override fun visit(expr: Expr.This) = "${expr.keyword}"

    override fun visit(expr: Expr.Unary) = parenthesize(expr.operator.lexeme, expr.right)

    override fun visit(expr: Expr.Variable) = expr.name.lexeme

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
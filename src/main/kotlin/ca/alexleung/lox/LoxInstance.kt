package ca.alexleung.lox

class LoxInstance(private val loxClass: LoxClass) {
    override fun toString(): String = "${loxClass.name} instance"
}
package com.mariya.skillcraftcalculator.domain

import java.util.Locale
import kotlin.math.abs

object CalculatorEngine {

    data class State(
        val display: String = "0",
        val storedValue: Double? = null,
        val storedOp: Op? = null,
        val isNewEntry: Boolean = true,
        val expression: String = "",
        val liveResult: String = ""
    )

    enum class Op { ADD, SUB, MUL, DIV }

    sealed class Action {
        data class Digit(val d: Char) : Action()
        data object Dot : Action()
        data object Clear : Action()
        data object Backspace : Action()
        data class Operator(val op: Op) : Action()
        data object Equals : Action()
        data object ToggleSign : Action()
        data object Percent : Action()
    }

    fun reduce(s: State, a: Action): State {
        val next = when (a) {
            is Action.Digit -> digit(s, a.d)
            Action.Dot -> dot(s)
            Action.Clear -> State()
            Action.Backspace -> backspace(s)
            is Action.Operator -> op(s, a.op)
            Action.Equals -> equalsNow(s)
            Action.ToggleSign -> sign(s)
            Action.Percent -> percent(s)
        }
        return withDerived(next)
    }

    private fun parse(x: String): Double = x.toDoubleOrNull() ?: 0.0

    private fun fmt(v: Double): String {
        val rounded = String.format(Locale.US, "%.6f", v).toDouble()
        val asLong = rounded.toLong()
        return if (abs(rounded - asLong.toDouble()) < 1e-10) asLong.toString() else rounded.toString()
    }

    private fun opSymbol(op: Op): String = when (op) {
        Op.ADD -> "+"
        Op.SUB -> "-"
        Op.MUL -> "×"
        Op.DIV -> "÷"
    }

    private fun eval(a: Double, b: Double, op: Op): Double? = when (op) {
        Op.ADD -> a + b
        Op.SUB -> a - b
        Op.MUL -> a * b
        Op.DIV -> if (b == 0.0) null else a / b
    }

    // ✅ prevents showing 1+1 immediately after operator press
    private fun withDerived(s: State): State {
        val left = s.storedValue
        val op = s.storedOp
        if (left == null || op == null) return s.copy(expression = "", liveResult = "")

        if (s.isNewEntry) {
            return s.copy(
                expression = "${fmt(left)} ${opSymbol(op)}",
                liveResult = ""
            )
        }

        val expr = "${fmt(left)} ${opSymbol(op)} ${s.display}"
        val preview = eval(left, parse(s.display), op)?.let { fmt(it) } ?: ""
        return s.copy(expression = expr, liveResult = preview)
    }

    private fun digit(s: State, d: Char): State {
        val newDisplay =
            if (s.isNewEntry || s.display == "0" || s.display == "Error") d.toString()
            else s.display + d
        return s.copy(display = newDisplay, isNewEntry = false)
    }

    private fun dot(s: State): State {
        if (s.display == "Error") return s.copy(display = "0.", isNewEntry = false)
        if (s.isNewEntry) return s.copy(display = "0.", isNewEntry = false)
        if (s.display.contains(".")) return s
        return s.copy(display = s.display + ".", isNewEntry = false)
    }

    private fun backspace(s: State): State {
        if (s.isNewEntry || s.display == "Error") return s
        val d = if (s.display.length <= 1) "0" else s.display.dropLast(1)
        val cleaned = if (d == "-" || d.isEmpty()) "0" else d
        return s.copy(display = cleaned)
    }

    private fun sign(s: State): State {
        if (s.display == "Error") return State()
        if (s.display == "0") return s
        val d = if (s.display.startsWith("-")) s.display.drop(1) else "-${s.display}"
        return s.copy(display = d, isNewEntry = false)
    }

    private fun percent(s: State): State {
        if (s.display == "Error") return State()
        val v = parse(s.display) / 100.0
        return s.copy(display = fmt(v), isNewEntry = true)
    }

    private fun op(s: State, newOp: Op): State {
        if (s.display == "Error") return State(display = "0", storedOp = newOp, isNewEntry = true)

        val current = parse(s.display)

        if (s.storedValue == null) {
            return s.copy(storedValue = current, storedOp = newOp, isNewEntry = true)
        }

        if (s.isNewEntry) {
            return s.copy(storedOp = newOp)
        }

        val pending = s.storedOp ?: newOp
        val result = eval(s.storedValue, current, pending)
            ?: return s.copy(display = "Error", storedValue = null, storedOp = null, isNewEntry = true)

        return s.copy(
            display = fmt(result),
            storedValue = result,
            storedOp = newOp,
            isNewEntry = true
        )
    }

    private fun equalsNow(s: State): State {
        if (s.display == "Error") return State()

        val left = s.storedValue ?: return s
        val op = s.storedOp ?: return s
        val right = parse(s.display)

        val result = eval(left, right, op)
            ?: return s.copy(display = "Error", storedValue = null, storedOp = null, isNewEntry = true, expression = "", liveResult = "")

        return s.copy(
            display = fmt(result),
            storedValue = null,
            storedOp = null,
            isNewEntry = true,
            expression = "",
            liveResult = ""
        )
    }
}
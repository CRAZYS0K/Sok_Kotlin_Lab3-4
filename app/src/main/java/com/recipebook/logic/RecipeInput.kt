package com.recipebook.logic

import java.text.NumberFormat
import java.util.Locale

enum class InputError { EMPTY, NOT_A_NUMBER, NOT_POSITIVE, TOO_LARGE }

sealed interface ParsedNumber<out T> {
    data class Valid<T>(val value: T) : ParsedNumber<T>
    data class Invalid(val error: InputError) : ParsedNumber<Nothing>
}

fun ParsedNumber<*>.errorOrNull(): InputError? = (this as? ParsedNumber.Invalid)?.error

object RecipeInput {
    const val MAX_MINUTES = 10_000
    const val MAX_AMOUNT = 100_000.0

    fun checkName(text: String): InputError? =
        if (text.isBlank()) InputError.EMPTY else null

    fun parseWholeNumber(text: String, max: Int): ParsedNumber<Int> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return ParsedNumber.Invalid(InputError.EMPTY)
        val number = trimmed.toIntOrNull()
            ?: return ParsedNumber.Invalid(
                if (trimmed.all { it.isDigit() }) InputError.TOO_LARGE else InputError.NOT_A_NUMBER
            )
        return when {
            number <= 0 -> ParsedNumber.Invalid(InputError.NOT_POSITIVE)
            number > max -> ParsedNumber.Invalid(InputError.TOO_LARGE)
            else -> ParsedNumber.Valid(number)
        }
    }

    fun parseAmount(text: String): ParsedNumber<Double> {
        val trimmed = text.trim().replace(',', '.')
        if (trimmed.isEmpty()) return ParsedNumber.Invalid(InputError.EMPTY)
        val number = trimmed.toDoubleOrNull()
        return when {
            number == null || !number.isFinite() -> ParsedNumber.Invalid(InputError.NOT_A_NUMBER)
            number <= 0 -> ParsedNumber.Invalid(InputError.NOT_POSITIVE)
            number > MAX_AMOUNT -> ParsedNumber.Invalid(InputError.TOO_LARGE)
            else -> ParsedNumber.Valid(number)
        }
    }

    fun amountToText(amount: Double, locale: Locale): String =
        NumberFormat.getNumberInstance(locale)
            .apply {
                maximumFractionDigits = 6
                isGroupingUsed = false
            }
            .format(amount)
}

package com.recipebook.logic

import java.text.NumberFormat
import java.util.Locale

object Servings {
    const val MIN = 1
    const val MAX = 99

    fun scale(amount: Double, fromServings: Int, toServings: Int): Double {
        if (fromServings <= 0) return amount
        return amount * toServings / fromServings
    }

    fun format(amount: Double, locale: Locale): String =
        NumberFormat.getNumberInstance(locale)
            .apply { maximumFractionDigits = 2 }
            .format(amount)
}

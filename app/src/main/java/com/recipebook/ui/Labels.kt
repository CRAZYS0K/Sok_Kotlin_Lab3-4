package com.recipebook.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.recipebook.R
import com.recipebook.data.Category
import com.recipebook.logic.InputError
import java.util.Locale

@StringRes
fun Category.labelRes(): Int = when (this) {
    Category.BREAKFAST -> R.string.category_breakfast
    Category.SOUP -> R.string.category_soup
    Category.MAIN -> R.string.category_main
    Category.BAKING -> R.string.category_baking
    Category.SALAD -> R.string.category_salad
    Category.DESSERT -> R.string.category_dessert
    Category.OTHER -> R.string.category_other
}

@StringRes
fun InputError.messageRes(): Int = when (this) {
    InputError.EMPTY -> R.string.error_empty
    InputError.NOT_A_NUMBER -> R.string.error_not_number
    InputError.NOT_POSITIVE -> R.string.error_not_positive
    InputError.TOO_LARGE -> R.string.error_too_large
}

@StringRes
fun InputError.amountMessageRes(): Int = when (this) {
    InputError.EMPTY -> R.string.error_amount_empty
    InputError.NOT_A_NUMBER -> R.string.error_amount_not_number
    InputError.NOT_POSITIVE -> R.string.error_amount_not_positive
    InputError.TOO_LARGE -> R.string.error_amount_too_large
}

@Composable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0]

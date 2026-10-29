package com.recipebook.ui

import androidx.annotation.StringRes
import com.recipebook.R
import com.recipebook.data.Category

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

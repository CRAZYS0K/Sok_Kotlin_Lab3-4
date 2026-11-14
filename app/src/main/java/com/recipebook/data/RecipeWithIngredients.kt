package com.recipebook.data

import androidx.room3.Embedded
import androidx.room3.Relation

data class RecipeWithIngredients(
    @Embedded val recipe: Recipe,
    @Relation(parentColumns = ["id"], entityColumns = ["recipeId"])
    val ingredients: List<Ingredient>,
)

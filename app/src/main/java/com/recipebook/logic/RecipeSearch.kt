package com.recipebook.logic

import com.recipebook.data.Category
import com.recipebook.data.Recipe
import com.recipebook.data.RecipeWithIngredients

sealed interface RecipeFilter {
    data object All : RecipeFilter
    data object Favorites : RecipeFilter
    data class InCategory(val category: Category) : RecipeFilter
}

object RecipeSearch {
    fun matches(text: String, query: String): Boolean =
        text.contains(query.trim(), ignoreCase = true)

    fun matches(item: RecipeWithIngredients, query: String): Boolean =
        matches(item.recipe.name, query) || item.ingredients.any { matches(it.name, query) }

    fun filter(items: List<RecipeWithIngredients>, query: String, filter: RecipeFilter): List<Recipe> =
        items.filter { fits(it.recipe, filter) && matches(it, query) }.map { it.recipe }

    private fun fits(recipe: Recipe, filter: RecipeFilter): Boolean = when (filter) {
        RecipeFilter.All -> true
        RecipeFilter.Favorites -> recipe.isFavorite
        is RecipeFilter.InCategory -> recipe.category == filter.category
    }
}

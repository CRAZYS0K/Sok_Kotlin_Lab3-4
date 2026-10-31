package com.recipebook.logic

import com.recipebook.data.Category
import com.recipebook.data.Recipe

sealed interface RecipeFilter {
    data object All : RecipeFilter
    data object Favorites : RecipeFilter
    data class InCategory(val category: Category) : RecipeFilter
}

object RecipeSearch {
    fun matches(name: String, query: String): Boolean =
        name.contains(query.trim(), ignoreCase = true)

    fun filter(recipes: List<Recipe>, query: String, filter: RecipeFilter): List<Recipe> =
        recipes.filter { fits(it, filter) && matches(it.name, query) }

    private fun fits(recipe: Recipe, filter: RecipeFilter): Boolean = when (filter) {
        RecipeFilter.All -> true
        RecipeFilter.Favorites -> recipe.isFavorite
        is RecipeFilter.InCategory -> recipe.category == filter.category
    }
}

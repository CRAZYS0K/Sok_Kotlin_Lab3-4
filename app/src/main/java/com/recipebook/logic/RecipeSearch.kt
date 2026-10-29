package com.recipebook.logic

import com.recipebook.data.Category
import com.recipebook.data.Recipe

object RecipeSearch {
    fun matches(name: String, query: String): Boolean =
        name.contains(query.trim(), ignoreCase = true)

    fun filter(recipes: List<Recipe>, query: String, category: Category?): List<Recipe> =
        recipes.filter { recipe ->
            (category == null || recipe.category == category) && matches(recipe.name, query)
        }
}

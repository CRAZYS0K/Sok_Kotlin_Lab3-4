package com.recipebook.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository(private val dao: RecipeDao) {
    fun observeRecipes(): Flow<List<Recipe>> = dao.observeRecipes()

    fun observe(id: Long): Flow<RecipeWithDetails?> = dao.observe(id).map { it?.inOrder() }

    suspend fun find(id: Long): RecipeWithDetails? = dao.findById(id)?.inOrder()

    suspend fun save(recipe: Recipe, ingredients: List<Ingredient>, steps: List<Step>): Long =
        dao.save(recipe, ingredients, steps)


    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    suspend fun setFavorite(id: Long, favorite: Boolean) {
        dao.setFavorite(id, favorite)
    }
}
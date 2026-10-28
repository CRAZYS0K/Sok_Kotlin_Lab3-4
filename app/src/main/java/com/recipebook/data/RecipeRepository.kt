package com.recipebook.data

import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val dao: RecipeDao) {
    fun observeAll(): Flow<List<RecipeWithDetails>> = dao.observeAll()

    fun observe(id: Long): Flow<RecipeWithDetails?> = dao.observe(id)

    suspend fun save(recipe: Recipe, ingredients: List<Ingredient>, steps: List<Step>): Long =
        dao.save(recipe, ingredients, steps)


    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    suspend fun setFavorite(id: Long, favorite: Boolean) {
        dao.setFavorite(id, favorite)
    }
}
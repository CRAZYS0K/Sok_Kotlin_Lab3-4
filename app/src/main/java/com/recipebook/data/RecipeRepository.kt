package com.recipebook.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RecipeRepository(private val dao: RecipeDao, private val photos: PhotoStorage) {
    fun observeRecipes(): Flow<List<Recipe>> = dao.observeRecipes()

    fun observe(id: Long): Flow<RecipeWithDetails?> = dao.observe(id).map { it?.inOrder() }

    suspend fun find(id: Long): RecipeWithDetails? = dao.findById(id)?.inOrder()

    suspend fun save(recipe: Recipe, ingredients: List<Ingredient>, steps: List<Step>): Long =
        dao.save(recipe, ingredients, steps)

    suspend fun delete(id: Long) {
        val photo = dao.findPhotoFileName(id)
        dao.deleteById(id)
        if (photo != null) withContext(Dispatchers.IO) { photos.delete(photo) }
    }

    suspend fun setFavorite(id: Long, favorite: Boolean) {
        dao.setFavorite(id, favorite)
    }
}
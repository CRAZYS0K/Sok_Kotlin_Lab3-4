package com.recipebook.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Transaction
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun observeAll(): Flow<List<RecipeWithDetails>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id=:id")
    fun observe(id: Long): Flow<RecipeWithDetails?>

    @Upsert
    suspend fun upsertRecipe(recipe: Recipe): Long

    @Insert
    suspend fun insertIngredients(ingredients: List<Ingredient>)

    @Insert
    suspend fun insertSteps(steps: List<Step>)

    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredients(recipeId: Long)

    @Query("DELETE FROM steps WHERE recipeId = :recipeId")
    suspend fun deleteSteps(recipeId: Long)

    @Transaction
    suspend fun save(recipe: Recipe, ingredients: List<Ingredient>, steps: List<Step>): Long {
        val inserted = upsertRecipe(recipe)
        val recipeId = if (recipe.id == 0L) inserted else recipe.id
        deleteIngredients(recipeId)
        deleteSteps(recipeId)
        insertIngredients(ingredients.mapIndexed { index, item ->
            item.copy(
                id = 0, recipeId = recipeId, position = index
            )
        })
        insertSteps(steps.mapIndexed { index, item ->
            item.copy(
                id = 0, recipeId = recipeId, position = index
            )
        })
        return recipeId
    }

    @Query("DELETE FROM recipes WHERE id =:id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE recipes SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)
}
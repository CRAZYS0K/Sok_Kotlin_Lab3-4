package com.recipebook.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

@Database(
    entities = [Recipe::class, Ingredient::class, Step::class],
    version = 1, exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        fun create(context: Context): RecipeDatabase =
            Room.databaseBuilder<RecipeDatabase>(context, "recipes.db")
                .setDriver(AndroidSQLiteDriver())
                .build()
    }
}
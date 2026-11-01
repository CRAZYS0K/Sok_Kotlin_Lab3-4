package com.recipebook.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver

@Database(
    entities = [Recipe::class, Ingredient::class, Step::class],
    version = 2, exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override suspend fun migrate(connection: SQLiteConnection) {
                StarterRecipes.insertInto(connection)
            }
        }

        fun create(context: Context): RecipeDatabase =
            Room.databaseBuilder<RecipeDatabase>(context, "recipes.db")
                .setDriver(AndroidSQLiteDriver())
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : RoomDatabase.Callback() {
                    override suspend fun onCreate(connection: SQLiteConnection) {
                        StarterRecipes.insertInto(connection)
                    }
                })
                .build()
    }
}

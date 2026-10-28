package com.recipebook

import android.app.Application
import com.recipebook.data.RecipeDatabase
import com.recipebook.data.RecipeRepository

class RecipeBookApplication : Application() {
    val repository by lazy { RecipeRepository(RecipeDatabase.create(this).recipeDao()) }
}
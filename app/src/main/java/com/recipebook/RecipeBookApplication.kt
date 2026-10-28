package com.recipebook

import android.app.Application
import com.recipebook.data.RecipeDatabase
import com.recipebook.data.RecipeRepository

class RecipeBookApplication : Application() {
    // by lazy: база создаётся при первом обращении и дальше переиспользуется
    val repository by lazy { RecipeRepository(RecipeDatabase.create(this).recipeDao()) }
}
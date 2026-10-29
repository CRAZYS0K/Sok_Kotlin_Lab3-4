package com.recipebook

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.recipebook.data.RecipeDatabase
import com.recipebook.data.RecipeRepository

class RecipeBookApplication : Application() {
    val repository by lazy { RecipeRepository(RecipeDatabase.create(this).recipeDao()) }
}

fun CreationExtras.recipeBookApplication(): RecipeBookApplication =
    this[APPLICATION_KEY] as RecipeBookApplication

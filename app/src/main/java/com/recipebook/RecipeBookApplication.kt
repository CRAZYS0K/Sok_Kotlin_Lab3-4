package com.recipebook

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.recipebook.data.PhotoStorage
import com.recipebook.data.RecipeDatabase
import com.recipebook.data.RecipeRepository

class RecipeBookApplication : Application() {
    val photos by lazy { PhotoStorage(this) }

    val repository by lazy { RecipeRepository(RecipeDatabase.create(this).recipeDao(), photos) }
}

fun CreationExtras.recipeBookApplication(): RecipeBookApplication =
    this[APPLICATION_KEY] as RecipeBookApplication

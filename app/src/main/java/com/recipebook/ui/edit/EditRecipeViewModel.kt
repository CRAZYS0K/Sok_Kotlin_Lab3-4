package com.recipebook.ui.edit

import android.net.Uri
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.recipebook.data.PhotoStorage
import com.recipebook.data.RecipeRepository
import com.recipebook.data.RecipeWithDetails
import com.recipebook.logic.RecipeInput
import com.recipebook.recipeBookApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class EditRecipeViewModel(
    private val repository: RecipeRepository,
    private val photos: PhotoStorage,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val recipeId: Long? = savedStateHandle[KEY_RECIPE_ID]
    val isNew = recipeId == null

    val form = RecipeForm()

    var isSaved by mutableStateOf(false)
        private set

    var isPhotoLoading by mutableStateOf(false)
        private set
    var photoFailed by mutableStateOf(false)
        private set

    private var isFavorite = false
    private var isSaving = false

    private var originalPhoto: String? = null
    private val pickedPhotos = mutableListOf<String>()

    init {
        if (recipeId == null) {
            form.addIngredient()
            form.addStep()
        } else {
            viewModelScope.launch {
                repository.find(recipeId)?.let(::fill)
            }
        }
    }

    private fun fill(details: RecipeWithDetails) {
        val recipe = details.recipe
        form.name.setTextAndPlaceCursorAtEnd(recipe.name)
        form.minutes.setTextAndPlaceCursorAtEnd(recipe.cookingMinutes.toString())
        form.servings.setTextAndPlaceCursorAtEnd(recipe.servings.toString())
        form.category = recipe.category
        form.photo = recipe.photoFileName
        originalPhoto = recipe.photoFileName
        isFavorite = recipe.isFavorite
        details.ingredients.forEach {
            form.addIngredient(it.name, RecipeInput.amountToText(it.amount, Locale.getDefault()), it.unit)
        }
        details.steps.forEach { form.addStep(it.text) }
    }

    fun onPhotoPicked(uri: Uri) {
        viewModelScope.launch {
            isPhotoLoading = true
            photoFailed = false
            val fileName = photos.import(uri)
            isPhotoLoading = false
            if (fileName == null) {
                photoFailed = true
            } else {
                pickedPhotos += fileName
                form.photo = fileName
            }
        }
    }

    fun removePhoto() {
        form.photo = null
    }

    fun save() {
        if (isSaving) return
        val draft = form.toDraft(id = recipeId ?: 0, isFavorite = isFavorite) ?: return
        isSaving = true
        viewModelScope.launch {
            repository.save(draft.recipe, draft.ingredients, draft.steps)
            val unused = (pickedPhotos + listOfNotNull(originalPhoto)).filter { it != draft.recipe.photoFileName }
            withContext(Dispatchers.IO) { unused.forEach(photos::delete) }
            pickedPhotos.clear()
            isSaved = true
        }
    }

    override fun onCleared() {
        pickedPhotos.forEach(photos::delete)
    }

    companion object {
        const val KEY_RECIPE_ID = "recipe_id"

        val Factory = viewModelFactory {
            initializer {
                val app = recipeBookApplication()
                EditRecipeViewModel(app.repository, app.photos, createSavedStateHandle())
            }
        }
    }
}

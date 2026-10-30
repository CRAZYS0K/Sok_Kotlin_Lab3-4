package com.recipebook.ui.edit

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
import com.recipebook.data.RecipeRepository
import com.recipebook.data.RecipeWithDetails
import com.recipebook.logic.RecipeInput
import com.recipebook.recipeBookApplication
import kotlinx.coroutines.launch
import java.util.Locale

class EditRecipeViewModel(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val recipeId: Long? = savedStateHandle[KEY_RECIPE_ID]
    val isNew = recipeId == null

    val form = RecipeForm()

    var isSaved by mutableStateOf(false)
        private set

    private var isFavorite = false
    private var isSaving = false

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
        isFavorite = recipe.isFavorite
        details.ingredients.forEach {
            form.addIngredient(it.name, RecipeInput.amountToText(it.amount, Locale.getDefault()), it.unit)
        }
        details.steps.forEach { form.addStep(it.text) }
    }

    fun save() {
        if (isSaving) return
        val draft = form.toDraft(id = recipeId ?: 0, isFavorite = isFavorite) ?: return
        isSaving = true
        viewModelScope.launch {
            repository.save(draft.recipe, draft.ingredients, draft.steps)
            isSaved = true
        }
    }

    companion object {
        const val KEY_RECIPE_ID = "recipe_id"

        val Factory = viewModelFactory {
            initializer {
                EditRecipeViewModel(recipeBookApplication().repository, createSavedStateHandle())
            }
        }
    }
}

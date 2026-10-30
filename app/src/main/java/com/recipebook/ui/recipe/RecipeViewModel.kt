package com.recipebook.ui.recipe

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
import com.recipebook.logic.Servings
import com.recipebook.recipeBookApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RecipeUiState(
    val details: RecipeWithDetails? = null,
    val servings: Int = 0,
    val isLoading: Boolean = true,
)

class RecipeViewModel(
    private val repository: RecipeRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val recipeId: Long = checkNotNull(savedStateHandle.get<Long>(KEY_RECIPE_ID))

    private val chosenServings = savedStateHandle.getStateFlow(KEY_SERVINGS, 0)

    val uiState: StateFlow<RecipeUiState> = combine(
        repository.observe(recipeId),
        chosenServings,
    ) { details, chosen ->
        RecipeUiState(
            details = details,
            servings = if (chosen > 0) chosen else details?.recipe?.servings ?: 0,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RecipeUiState())

    var isDeleted by mutableStateOf(false)
        private set

    fun changeServings(delta: Int) {
        val current = uiState.value.servings
        if (current == 0) return
        savedStateHandle[KEY_SERVINGS] = (current + delta).coerceIn(Servings.MIN, Servings.MAX)
    }

    fun delete() {
        viewModelScope.launch {
            repository.delete(recipeId)
            isDeleted = true
        }
    }

    companion object {
        const val KEY_RECIPE_ID = "recipe_id"
        private const val KEY_SERVINGS = "servings"

        val Factory = viewModelFactory {
            initializer {
                RecipeViewModel(recipeBookApplication().repository, createSavedStateHandle())
            }
        }
    }
}

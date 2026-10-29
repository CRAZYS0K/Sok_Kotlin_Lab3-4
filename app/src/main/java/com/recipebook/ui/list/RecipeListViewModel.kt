package com.recipebook.ui.list

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.recipebook.data.Category
import com.recipebook.data.Recipe
import com.recipebook.data.RecipeRepository
import com.recipebook.logic.RecipeSearch
import com.recipebook.recipeBookApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class RecipeListUiState(
    val recipes: List<Recipe> = emptyList(),
    val category: Category? = null,
    val isBookEmpty: Boolean = false,
    val isLoading: Boolean = true,
)

class RecipeListViewModel(repository: RecipeRepository) : ViewModel() {
    val query = TextFieldState()
    private val category = MutableStateFlow<Category?>(null)

    val uiState: StateFlow<RecipeListUiState> = combine(
        repository.observeRecipes(),
        snapshotFlow { query.text.toString() },
        category,
    ) { recipes, text, selected ->
        RecipeListUiState(
            recipes = RecipeSearch.filter(recipes, text, selected),
            category = selected,
            isBookEmpty = recipes.isEmpty(),
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RecipeListUiState())

    fun onCategorySelect(value: Category?) {
        category.value = value
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { RecipeListViewModel(recipeBookApplication().repository) }
        }
    }
}

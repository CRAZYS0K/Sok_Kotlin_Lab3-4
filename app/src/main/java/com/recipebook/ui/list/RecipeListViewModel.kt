package com.recipebook.ui.list

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.recipebook.data.Recipe
import com.recipebook.data.RecipeRepository
import com.recipebook.logic.RecipeFilter
import com.recipebook.logic.RecipeSearch
import com.recipebook.recipeBookApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RecipeListUiState(
    val recipes: List<Recipe> = emptyList(),
    val filter: RecipeFilter = RecipeFilter.All,
    val isBookEmpty: Boolean = false,
    val isLoading: Boolean = true,
)

class RecipeListViewModel(private val repository: RecipeRepository) : ViewModel() {
    val query = TextFieldState()
    private val filter = MutableStateFlow<RecipeFilter>(RecipeFilter.All)

    val uiState: StateFlow<RecipeListUiState> = combine(
        repository.observeRecipes(),
        snapshotFlow { query.text.toString() },
        filter,
    ) { recipes, text, selected ->
        RecipeListUiState(
            recipes = RecipeSearch.filter(recipes, text, selected),
            filter = selected,
            isBookEmpty = recipes.isEmpty(),
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RecipeListUiState())

    fun onFilterSelect(value: RecipeFilter) {
        filter.value = value
    }

    fun onFavoriteToggle(recipe: Recipe) {
        viewModelScope.launch { repository.setFavorite(recipe.id, !recipe.isFavorite) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { RecipeListViewModel(recipeBookApplication().repository) }
        }
    }
}

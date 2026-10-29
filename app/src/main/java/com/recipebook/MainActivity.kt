package com.recipebook

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.recipebook.ui.list.RecipeListScreen
import com.recipebook.ui.list.RecipeListViewModel
import com.recipebook.ui.theme.Sok_Kotlin_Lab34Theme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sok_Kotlin_Lab34Theme {
                val viewModel: RecipeListViewModel = viewModel(factory = RecipeListViewModel.Factory)
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                RecipeListScreen(
                    state = state,
                    query = viewModel.query,
                    onCategorySelect = viewModel::onCategorySelect,
                    onRecipeClick = { id -> startActivity(RecipeActivity.newIntent(this, id)) },
                )
            }
        }
    }
}

package com.recipebook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.recipebook.ui.recipe.RecipeScreen
import com.recipebook.ui.recipe.RecipeViewModel
import com.recipebook.ui.theme.Sok_Kotlin_Lab34Theme

class RecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sok_Kotlin_Lab34Theme {
                val viewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory)
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(viewModel.isDeleted) {
                    if (viewModel.isDeleted) {
                        setResult(RESULT_DELETED)
                        finish()
                    }
                }

                RecipeScreen(
                    state = state,
                    onServingsChange = viewModel::changeServings,
                    onFavoriteToggle = viewModel::toggleFavorite,
                    onEdit = { id -> startActivity(EditRecipeActivity.newIntent(this, id)) },
                    onDelete = viewModel::delete,
                    onBack = ::finish,
                )
            }
        }
    }

    companion object {
        const val RESULT_DELETED = Activity.RESULT_FIRST_USER

        fun newIntent(context: Context, recipeId: Long): Intent =
            Intent(context, RecipeActivity::class.java)
                .putExtra(RecipeViewModel.KEY_RECIPE_ID, recipeId)
    }
}

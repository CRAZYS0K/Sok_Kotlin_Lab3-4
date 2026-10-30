package com.recipebook

import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.recipebook.ui.list.RecipeListScreen
import com.recipebook.ui.list.RecipeListViewModel
import com.recipebook.ui.theme.Sok_Kotlin_Lab34Theme
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sok_Kotlin_Lab34Theme {
                val viewModel: RecipeListViewModel = viewModel(factory = RecipeListViewModel.Factory)
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val savedMessage = stringResource(R.string.recipe_saved)
                val deletedMessage = stringResource(R.string.recipe_deleted)

                val recipeLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == RecipeActivity.RESULT_DELETED) {
                        scope.launch { snackbarHostState.showSnackbar(deletedMessage) }
                    }
                }

                val addLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == RESULT_OK) {
                        scope.launch { snackbarHostState.showSnackbar(savedMessage) }
                    }
                }

                RecipeListScreen(
                    state = state,
                    query = viewModel.query,
                    snackbarHostState = snackbarHostState,
                    onCategorySelect = viewModel::onCategorySelect,
                    onRecipeClick = { id -> recipeLauncher.launch(RecipeActivity.newIntent(this, id)) },
                    onAddClick = { addLauncher.launch(EditRecipeActivity.newIntent(this, recipeId = null)) },
                )
            }
        }
    }
}

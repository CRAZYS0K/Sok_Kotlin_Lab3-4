package com.recipebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.recipebook.ui.edit.EditRecipeScreen
import com.recipebook.ui.edit.EditRecipeViewModel
import com.recipebook.ui.theme.Sok_Kotlin_Lab34Theme

class EditRecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sok_Kotlin_Lab34Theme {
                val viewModel: EditRecipeViewModel = viewModel(factory = EditRecipeViewModel.Factory)

                LaunchedEffect(viewModel.isSaved) {
                    if (viewModel.isSaved) {
                        setResult(RESULT_OK)
                        finish()
                    }
                }

                EditRecipeScreen(
                    form = viewModel.form,
                    isNew = viewModel.isNew,
                    onSave = viewModel::save,
                    onClose = ::finish,
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context, recipeId: Long?): Intent =
            Intent(context, EditRecipeActivity::class.java).apply {
                if (recipeId != null) putExtra(EditRecipeViewModel.KEY_RECIPE_ID, recipeId)
            }
    }
}

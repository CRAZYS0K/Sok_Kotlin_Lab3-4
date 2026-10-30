package com.recipebook.ui.edit

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.recipebook.data.Category
import com.recipebook.data.Ingredient
import com.recipebook.data.Recipe
import com.recipebook.data.Step
import com.recipebook.logic.InputError
import com.recipebook.logic.ParsedNumber
import com.recipebook.logic.RecipeInput
import com.recipebook.logic.Servings
import com.recipebook.logic.errorOrNull

class IngredientFields(val key: Long, name: String = "", amount: String = "", unit: String = "") {
    val name = TextFieldState(name)
    val amount = TextFieldState(amount)
    val unit = TextFieldState(unit)

    val isBlank: Boolean
        get() = name.text.isBlank() && amount.text.isBlank() && unit.text.isBlank()
}

class StepFields(val key: Long, text: String = "") {
    val text = TextFieldState(text)
}

data class IngredientErrors(val name: InputError?, val amount: InputError?)

data class RecipeDraft(val recipe: Recipe, val ingredients: List<Ingredient>, val steps: List<Step>)

class RecipeForm {
    val name = TextFieldState()
    val minutes = TextFieldState()
    val servings = TextFieldState()
    var category by mutableStateOf(Category.MAIN)
    val ingredients = mutableStateListOf<IngredientFields>()
    val steps = mutableStateListOf<StepFields>()

    private var showErrors by mutableStateOf(false)
    private var nextKey = 0L

    val nameError: InputError?
        get() = if (showErrors) RecipeInput.checkName(name.text.toString()) else null
    val minutesError: InputError?
        get() = if (showErrors) parseMinutes().errorOrNull() else null
    val servingsError: InputError?
        get() = if (showErrors) parseServings().errorOrNull() else null

    fun ingredientErrors(row: IngredientFields): IngredientErrors? {
        if (!showErrors || row.isBlank) return null
        val errors = IngredientErrors(
            name = RecipeInput.checkName(row.name.text.toString()),
            amount = RecipeInput.parseAmount(row.amount.text.toString()).errorOrNull(),
        )
        return if (errors.name == null && errors.amount == null) null else errors
    }

    fun addIngredient(name: String = "", amount: String = "", unit: String = "") {
        ingredients += IngredientFields(nextKey++, name, amount, unit)
    }

    fun removeIngredient(row: IngredientFields) {
        ingredients.remove(row)
    }

    fun addStep(text: String = "") {
        steps += StepFields(nextKey++, text)
    }

    fun removeStep(row: StepFields) {
        steps.remove(row)
    }

    fun toDraft(id: Long, isFavorite: Boolean): RecipeDraft? {
        showErrors = true
        val minutes = (parseMinutes() as? ParsedNumber.Valid)?.value
        val servings = (parseServings() as? ParsedNumber.Valid)?.value
        val filledIngredients = ingredients.filterNot { it.isBlank }
        if (nameError != null || minutes == null || servings == null ||
            filledIngredients.any { ingredientErrors(it) != null }
        ) {
            return null
        }
        val recipe = Recipe(
            id = id,
            name = name.text.toString().trim(),
            category = category,
            cookingMinutes = minutes,
            servings = servings,
            isFavorite = isFavorite,
        )
        val ingredientRows = filledIngredients.map { row ->
            Ingredient(
                recipeId = id,
                position = 0,
                name = row.name.text.toString().trim(),
                amount = (RecipeInput.parseAmount(row.amount.text.toString()) as ParsedNumber.Valid).value,
                unit = row.unit.text.toString().trim(),
            )
        }
        val stepRows = steps
            .map { it.text.text.toString().trim() }
            .filter { it.isNotEmpty() }
            .map { Step(recipeId = id, position = 0, text = it) }
        return RecipeDraft(recipe, ingredientRows, stepRows)
    }

    private fun parseMinutes() = RecipeInput.parseWholeNumber(minutes.text.toString(), RecipeInput.MAX_MINUTES)

    private fun parseServings() = RecipeInput.parseWholeNumber(servings.text.toString(), Servings.MAX)
}

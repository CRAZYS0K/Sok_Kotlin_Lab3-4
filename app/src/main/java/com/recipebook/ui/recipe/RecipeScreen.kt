package com.recipebook.ui.recipe

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.recipebook.R
import com.recipebook.data.Category
import com.recipebook.data.Ingredient
import com.recipebook.data.Recipe
import com.recipebook.data.RecipeWithDetails
import com.recipebook.data.Step
import com.recipebook.logic.Servings
import com.recipebook.ui.IconText
import com.recipebook.ui.PhotoPlaceholder
import com.recipebook.ui.currentLocale
import com.recipebook.ui.labelRes
import com.recipebook.ui.theme.Sok_Kotlin_Lab34Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    state: RecipeUiState,
    onServingsChange: (Int) -> Unit,
    onEdit: (Long) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    val recipe = state.details?.recipe
                    if (recipe != null) {
                        IconButton(onClick = { onEdit(recipe.id) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = stringResource(R.string.edit),
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        val details = state.details
        if (details != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
            ) {
                recipeHeader(details.recipe)
                item {
                    ServingsPanel(
                        servings = state.servings,
                        onDecrease = { onServingsChange(-1) },
                        onIncrease = { onServingsChange(+1) },
                    )
                }
                ingredientsSection(details.ingredients, details.recipe.servings, state.servings)
                stepsSection(details.steps)
            }
        }
    }
}

private fun LazyListScope.recipeHeader(recipe: Recipe) {
    item {
        PhotoPlaceholder(
            iconSize = 64.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
        )
        Spacer(Modifier.height(16.dp))
        Text(recipe.name, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Text(
                    text = stringResource(recipe.category.labelRes()),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
            Spacer(Modifier.width(16.dp))
            IconText(R.drawable.ic_schedule, stringResource(R.string.minutes_short, recipe.cookingMinutes))
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ServingsPanel(servings: Int, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.servings_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            OutlinedIconButton(onClick = onDecrease, enabled = servings > Servings.MIN) {
                Icon(
                    painter = painterResource(R.drawable.ic_remove),
                    contentDescription = stringResource(R.string.decrease_servings),
                )
            }
            Text(
                text = servings.toString(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 40.dp),
            )
            FilledIconButton(onClick = onIncrease, enabled = servings < Servings.MAX) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.increase_servings),
                )
            }
        }
    }
}

private fun LazyListScope.sectionTitle(@StringRes title: Int) {
    item {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
    }
}

private fun LazyListScope.ingredientsSection(
    ingredients: List<Ingredient>,
    recipeServings: Int,
    servings: Int,
) {
    sectionTitle(R.string.ingredients)
    if (ingredients.isEmpty()) {
        item { EmptySectionText(stringResource(R.string.no_ingredients)) }
    }
    itemsIndexed(ingredients, key = { _, item -> "ingredient-${item.id}" }) { index, ingredient ->
        val amount = Servings.scale(ingredient.amount, recipeServings, servings)
        Row(Modifier.padding(vertical = 12.dp)) {
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = amountText(amount, ingredient.unit),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (index < ingredients.lastIndex) HorizontalDivider()
    }
}

private fun LazyListScope.stepsSection(steps: List<Step>) {
    sectionTitle(R.string.cooking)
    if (steps.isEmpty()) {
        item { EmptySectionText(stringResource(R.string.no_steps)) }
    }
    itemsIndexed(steps, key = { _, item -> "step-${item.id}" }) { index, step ->
        Row(Modifier.padding(vertical = 8.dp)) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(32.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text((index + 1).toString(), style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = step.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun EmptySectionText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun amountText(amount: Double, unit: String): String {
    val number = Servings.format(amount, currentLocale())
    return if (unit.isBlank()) number else "$number $unit"
}

@Preview(showBackground = true)
@Composable
private fun RecipeScreenPreview() {
    val details = RecipeWithDetails(
        recipe = Recipe(id = 1, name = "Сырники", category = Category.BREAKFAST, cookingMinutes = 25, servings = 3),
        ingredients = listOf(
            Ingredient(id = 1, recipeId = 1, position = 0, name = "Творог 5%", amount = 500.0, unit = "г"),
            Ingredient(id = 2, recipeId = 1, position = 1, name = "Яйцо", amount = 1.0, unit = "шт."),
        ),
        steps = listOf(Step(id = 1, recipeId = 1, position = 0, text = "Размять творог вилкой.")),
    )
    Sok_Kotlin_Lab34Theme {
        RecipeScreen(
            state = RecipeUiState(details = details, servings = 3, isLoading = false),
            onServingsChange = {},
            onEdit = {},
            onBack = {},
        )
    }
}

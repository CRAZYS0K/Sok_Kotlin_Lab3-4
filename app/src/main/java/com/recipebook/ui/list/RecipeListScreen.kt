package com.recipebook.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.recipebook.R
import com.recipebook.data.Category
import com.recipebook.data.Recipe
import com.recipebook.logic.RecipeFilter
import com.recipebook.ui.FavoriteButton
import com.recipebook.ui.IconText
import com.recipebook.ui.PhotoPlaceholder
import com.recipebook.ui.labelRes
import com.recipebook.ui.theme.Sok_Kotlin_Lab34Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    state: RecipeListUiState,
    query: TextFieldState,
    snackbarHostState: SnackbarHostState,
    onFilterSelect: (RecipeFilter) -> Unit,
    onRecipeClick: (Long) -> Unit,
    onFavoriteToggle: (Recipe) -> Unit,
    onAddClick: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.add_recipe),
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SearchField(
                query = query,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(12.dp))
            FilterChips(selected = state.filter, onSelect = onFilterSelect)
            when {
                state.isLoading -> Unit
                state.isBookEmpty -> EmptyMessage(stringResource(R.string.empty_book))
                state.recipes.isEmpty() -> EmptyMessage(stringResource(R.string.nothing_found))
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) },
                            onFavoriteToggle = { onFavoriteToggle(recipe) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchField(query: TextFieldState, modifier: Modifier = Modifier) {
    val keyboard = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        state = query,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
        trailingIcon = if (query.text.isNotEmpty()) {
            {
                IconButton(onClick = { query.clearText() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.clear_search),
                    )
                }
            }
        } else {
            null
        },
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        onKeyboardAction = { keyboard?.hide() },
        shape = CircleShape,
    )
}

@Composable
private fun FilterChips(selected: RecipeFilter, onSelect: (RecipeFilter) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Chip(stringResource(R.string.filter_all), selected = selected == RecipeFilter.All) {
                onSelect(RecipeFilter.All)
            }
        }
        item {
            Chip(stringResource(R.string.filter_favorites), selected = selected == RecipeFilter.Favorites) {
                onSelect(RecipeFilter.Favorites)
            }
        }
        items(Category.entries) { category ->
            val filter = RecipeFilter.InCategory(category)
            Chip(stringResource(category.labelRes()), selected = selected == filter) { onSelect(filter) }
        }
    }
}

@Composable
private fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        leadingIcon = if (selected) {
            {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        } else {
            null
        },
    )
}

@Composable
private fun RecipeCard(recipe: Recipe, onClick: () -> Unit, onFavoriteToggle: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            PhotoPlaceholder(
                iconSize = 32.dp,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(recipe.category.labelRes()),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    IconText(R.drawable.ic_schedule, stringResource(R.string.minutes_short, recipe.cookingMinutes))
                    IconText(
                        R.drawable.ic_group,
                        pluralStringResource(R.plurals.servings_count, recipe.servings, recipe.servings),
                    )
                }
            }
            FavoriteButton(
                isFavorite = recipe.isFavorite,
                onToggle = onFavoriteToggle,
                modifier = Modifier.align(Alignment.Top),
            )
        }
    }
}

@Composable
private fun EmptyMessage(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun RecipeListScreenPreview() {
    val recipes = listOf(
        Recipe(id = 1, name = "Сырники", category = Category.BREAKFAST, cookingMinutes = 25, servings = 3, isFavorite = true),
        Recipe(id = 2, name = "Борщ", category = Category.SOUP, cookingMinutes = 90, servings = 6),
    )
    Sok_Kotlin_Lab34Theme {
        RecipeListScreen(
            state = RecipeListUiState(recipes = recipes, isLoading = false),
            query = rememberTextFieldState(),
            snackbarHostState = remember { SnackbarHostState() },
            onFilterSelect = {},
            onRecipeClick = {},
            onFavoriteToggle = {},
            onAddClick = {},
        )
    }
}

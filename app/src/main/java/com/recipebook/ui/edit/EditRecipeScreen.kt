package com.recipebook.ui.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.recipebook.R
import com.recipebook.data.Category
import com.recipebook.logic.InputError
import com.recipebook.ui.RecipePhoto
import com.recipebook.ui.amountMessageRes
import com.recipebook.ui.labelRes
import com.recipebook.ui.messageRes
import com.recipebook.ui.theme.Sok_Kotlin_Lab34Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    form: RecipeForm,
    isNew: Boolean,
    isPhotoLoading: Boolean,
    photoFailed: Boolean,
    onPhotoPicked: (Uri) -> Unit,
    onPhotoRemove: () -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit,
) {
    val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onPhotoPicked(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (isNew) R.string.new_recipe else R.string.edit_recipe)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.close),
                        )
                    }
                },
            )
        },
        bottomBar = { SaveBar(onSave) },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PhotoField(
                photo = form.photo,
                isLoading = isPhotoLoading,
                failed = photoFailed,
                onPick = { pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onRemove = onPhotoRemove,
            )
            OutlinedTextField(
                state = form.name,
                label = { Text(stringResource(R.string.name)) },
                isError = form.nameError != null,
                supportingText = errorText(form.nameError?.messageRes()),
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            CategoryField(selected = form.category, onSelect = { form.category = it })
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(
                    state = form.minutes,
                    label = stringResource(R.string.time_minutes),
                    error = form.minutesError,
                    modifier = Modifier.weight(1f),
                )
                NumberField(
                    state = form.servings,
                    label = stringResource(R.string.servings_label),
                    error = form.servingsError,
                    modifier = Modifier.weight(1f),
                )
            }

            SectionHeader(
                title = stringResource(R.string.ingredients),
                addDescription = stringResource(R.string.add_ingredient),
                onAdd = { form.addIngredient() },
            )
            form.ingredients.forEach { row ->
                key(row.key) {
                    IngredientRow(
                        row = row,
                        errors = form.ingredientErrors(row),
                        onRemove = { form.removeIngredient(row) },
                    )
                }
            }

            SectionHeader(
                title = stringResource(R.string.cooking_steps),
                addDescription = stringResource(R.string.add_step),
                onAdd = { form.addStep() },
            )
            form.steps.forEachIndexed { index, row ->
                key(row.key) {
                    StepRow(number = index + 1, row = row, onRemove = { form.removeStep(row) })
                }
            }
        }
    }
}

@Composable
private fun PhotoField(
    photo: String?,
    isLoading: Boolean,
    failed: Boolean,
    onPick: () -> Unit,
    onRemove: () -> Unit,
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onPick),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                photo != null -> {
                    RecipePhoto(photo, iconSize = 48.dp, maxSide = 1080, modifier = Modifier.fillMaxSize())
                    FilledTonalIconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.remove_photo),
                        )
                    }
                }
                else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_photo),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                    )
                    Text(stringResource(R.string.add_photo), style = MaterialTheme.typography.titleSmall)
                }
            }
        }
        if (failed) {
            Text(
                text = stringResource(R.string.photo_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }
}

@Composable
private fun SaveBar(onSave: () -> Unit) {
    Surface(tonalElevation = 3.dp) {
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text(stringResource(R.string.save))
        }
    }
}

private fun errorText(messageRes: Int?): (@Composable () -> Unit)? =
    if (messageRes != null) {
        { Text(stringResource(messageRes)) }
    } else {
        null
    }

@Composable
private fun NumberField(state: TextFieldState, label: String, error: InputError?, modifier: Modifier) {
    OutlinedTextField(
        state = state,
        label = { Text(label) },
        isError = error != null,
        supportingText = errorText(error?.messageRes()),
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryField(selected: Category, onSelect: (Category) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = stringResource(selected.labelRes()),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.category)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Category.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text(stringResource(category.labelRes())) },
                    onClick = {
                        onSelect(category)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, addDescription: String, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        IconButton(onClick = onAdd) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = addDescription,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun IngredientRow(row: IngredientFields, errors: IngredientErrors?, onRemove: () -> Unit) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                state = row.name,
                placeholder = { Placeholder(stringResource(R.string.ingredient_name)) },
                isError = errors?.name != null,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.weight(2f),
            )
            OutlinedTextField(
                state = row.amount,
                placeholder = { Placeholder(stringResource(R.string.ingredient_amount)) },
                isError = errors?.amount != null,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                contentPadding = NarrowFieldPadding,
                modifier = Modifier.weight(1.1f),
            )
            OutlinedTextField(
                state = row.unit,
                placeholder = { Placeholder(stringResource(R.string.ingredient_unit)) },
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                contentPadding = NarrowFieldPadding,
                modifier = Modifier.weight(1.1f),
            )
            IconButton(onClick = onRemove) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.remove_ingredient),
                )
            }
        }
        if (errors != null) {
            val message = if (errors.name != null) R.string.error_ingredient_name else errors.amount?.amountMessageRes()
            if (message != null) {
                Text(
                    text = stringResource(message),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                )
            }
        }
    }
}

private val NarrowFieldPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp)

@Composable
private fun Placeholder(text: String) {
    Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis)
}

@Composable
private fun StepRow(number: Int, row: StepFields, onRemove: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            state = row.text,
            label = { Text(stringResource(R.string.step_number, number)) },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onRemove) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = stringResource(R.string.remove_step),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditRecipeScreenPreview() {
    val form = remember {
        RecipeForm().apply {
            addIngredient("Творог 5%", "500", "г")
            addIngredient("Яйцо", "1", "шт.")
            addStep("Размять творог вилкой, добавить яйцо, сахар и соль.")
        }
    }
    Sok_Kotlin_Lab34Theme {
        EditRecipeScreen(
            form = form,
            isNew = true,
            isPhotoLoading = false,
            photoFailed = false,
            onPhotoPicked = {},
            onPhotoRemove = {},
            onSave = {},
            onClose = {},
        )
    }
}

package com.recipebook.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.recipebook.R

@Composable
fun FavoriteButton(isFavorite: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onToggle() },
        colors = IconButtonDefaults.iconToggleButtonColors(
            checkedContentColor = MaterialTheme.colorScheme.tertiary,
        ),
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite),
            contentDescription = stringResource(R.string.favorite),
        )
    }
}

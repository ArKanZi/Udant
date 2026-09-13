package com.arkanzi.udant.feature.feed.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CategoryItem(category: String, selected: Boolean, action: () -> Unit) {

    Button(
        onClick = action,
        modifier = Modifier.scale(0.90f),
        colors = ButtonColors(
            contentColor = if (selected) {
                MaterialTheme.colorScheme.surfaceContainerLowest
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.Transparent
            },
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Box {
            Text(
                text = category,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alpha(0f)
            )

            Text(
                text = category,
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                }
            )
        }
    }
}

@Preview
@Composable
fun CategoryItemPreview() {
    CategoryItem("My Feed", true, {})
}
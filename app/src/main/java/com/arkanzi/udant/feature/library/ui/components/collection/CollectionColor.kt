package com.arkanzi.udant.feature.library.ui.components.collection


import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlin.math.absoluteValue
import kotlin.random.Random

@Composable
fun collectionColor(name: String): Color {
    val colors = listOf(
        Color(0xFFD8B8F5), // Lavender
        Color(0xFFB8D9F5), // Blue
        Color(0xFFF5B8CB), // Pink
        Color(0xFFB8E5C0), // Green
        Color(0xFFF5D0A8), // Peach
        Color(0xFFBBC5F2), // Periwinkle
        Color(0xFFF2C5A8), // Orange
        Color(0xFFB5E1DC), // Mint

        Color(0xFFE2B8F0), // Purple
        Color(0xFFAEDCF0), // Sky
        Color(0xFFF0B8B8), // Rose
        Color(0xFFC5E8B5), // Lime Green
        Color(0xFFF0D1B8), // Apricot
        Color(0xFFC4C8F0), // Violet Blue
        Color(0xFFE8B8D8), // Mauve
        Color(0xFFB8E8E0), // Aqua

        Color(0xFFD0B8F0), // Orchid
        Color(0xFFB8D0F0), // Cornflower
        Color(0xFFF0B8C0), // Coral Pink
        Color(0xFFB8E0C8), // Sea Green
        Color(0xFFF0D8B0), // Golden Peach
        Color(0xFFC8C0F0), // Soft Violet
        Color(0xFFE8B8C0), // Dusty Rose
        Color(0xFFB8DCD8)  // Teal
    )

    return colors[
        name.hashCode().absoluteValue % colors.size
    ]
}


fun generateCollectionColor(): Color {
    val hue = Random.nextFloat() * 360f


    return Color.hsl(
        hue = hue,
        saturation = 0.60f,
        lightness = 0.80f
    )
}
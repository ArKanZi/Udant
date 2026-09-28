package com.arkanzi.udant.core.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

fun folderIcon(
    fillColor: Color,
    strokeColor: Color,
): ImageVector {
    return ImageVector.Builder(
        name = "Folder",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {

        addPath(
            pathData = PathParser().parsePathString(
                "M8,7H16.75C18.857,7 19.91,7 20.667,7.506C20.994,7.724 21.275,8.006 21.494,8.333C22,9.09 22,10.143 22,12.25C22,15.761 22,17.517 21.157,18.778C20.793,19.324 20.324,19.793 19.778,20.157C18.517,21 16.761,21 13.25,21H12C7.286,21 4.929,21 3.464,19.535C2,18.071 2,15.714 2,11V7.944C2,6.128 2,5.22 2.38,4.538C2.651,4.052 3.052,3.651 3.538,3.38C4.22,3 5.128,3 6.944,3C8.108,3 8.69,3 9.199,3.191C10.362,3.627 10.842,4.684 11.367,5.733L12,7"
            ).toNodes(),
            fill = SolidColor(fillColor),
            stroke = SolidColor(strokeColor),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round
        )
    }.build()
}
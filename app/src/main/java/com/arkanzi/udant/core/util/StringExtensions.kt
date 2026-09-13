package com.arkanzi.udant.core.util

fun String.toSafeFileName(): String {

    return replace(
        Regex("[<>:\"/\\\\|?*]"),
        "_"
    )
}

fun String.toTitleCase(): String =
    lowercase()
        .split(" ")
        .joinToString(" ") {
            it.replaceFirstChar { char -> char.uppercase() }
        }
package com.arkanzi.udant.core.util

fun String.toSafeFileName(): String {

    return replace(
        Regex("[<>:\"/\\\\|?*]"),
        "_"
    )
}
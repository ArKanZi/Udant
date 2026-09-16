package com.arkanzi.udant.core.util

import java.security.MessageDigest

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

fun String.toGenerateId(): String {
    return MessageDigest
        .getInstance("SHA-256")
        .digest(toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }
}
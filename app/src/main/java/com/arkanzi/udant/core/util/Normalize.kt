package com.arkanzi.udant.core.util

fun normalize(string: String): String{
    return string.replace(Regex("\\s+"), "").lowercase()
}
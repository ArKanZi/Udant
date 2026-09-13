package com.arkanzi.udant.core.util

fun formatRelativeTime(timestamp: Long): String {

    val diff = System.currentTimeMillis() - timestamp

    val seconds = diff / 1_000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val months = days / 30

    return when {
        seconds < 60 -> "$seconds sec ago"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours hours ago"
        days < 30 -> "$days days ago"
        else -> "$months months ago"
    }
}
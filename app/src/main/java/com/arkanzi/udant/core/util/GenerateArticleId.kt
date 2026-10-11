package com.arkanzi.udant.core.util

fun generateArticleId(
    sourceId: String,
    articleUrl: String
): String {
    return "$sourceId:$articleUrl".toGenerateId()
}
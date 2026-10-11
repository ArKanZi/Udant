package com.arkanzi.udant.core.model

data class Article(

    val articleId: String,

    val title: String,

    val summary: String,

    val imageUrl: String?,

    val articleUrl: String,

    val publishedAt: Long,

    val sourceId: String,

    val sourceName: String,

    val author: String?,

    val category: String = "Default",

    val savedAt: Long? = null,

    val archiveStatus: ArchiveStatus =
        ArchiveStatus.NOT_ARCHIVED,

    val archiveUri: String? = null
)
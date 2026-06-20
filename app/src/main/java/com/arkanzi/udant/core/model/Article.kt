package com.arkanzi.udant.core.model

data class Article(

    val articleId: Long = 0,

    val title: String,

    val summary: String,

    val imageUrl: String?,

    val articleUrl: String,

    val publishedAt: Long,

    val sourceName: String,

    val author: String?,

    val category: String = "Default",

    val savedAt: Long? = null,

    val archiveStatus: ArchiveStatus =
        ArchiveStatus.NOT_ARCHIVED,

    val archiveUri: String? = null
)
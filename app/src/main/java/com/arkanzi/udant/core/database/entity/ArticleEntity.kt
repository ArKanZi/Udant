package com.arkanzi.udant.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "articles",
    indices = [
        Index(
            value = ["articleUrl"],
            unique = true
        )
    ])
data class ArticleEntity(

    @PrimaryKey(autoGenerate = true)
    val articleId: Long = 0,

    val title: String,

    val summary: String,

    val author: String?,

    val imageUrl: String?,

    val sourceName: String,

    val articleUrl: String,

    val publishedAt: Long,

    val category: String,

)
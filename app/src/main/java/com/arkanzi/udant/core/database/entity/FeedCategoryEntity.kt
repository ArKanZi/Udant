package com.arkanzi.udant.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_categories")
data class FeedCategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val extensionId: String
)
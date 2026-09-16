package com.arkanzi.udant.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "feed_categories",
    foreignKeys = [
        ForeignKey(
            entity = ExtensionEntity::class,
            parentColumns = ["id"],
            childColumns = ["extensionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FeedCategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val extensionId: String
)
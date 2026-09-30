package com.arkanzi.udant.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "collection",
    indices = [
        Index(value = ["name"], unique = true)
    ])
data class CollectionEntity(
    @PrimaryKey()
    val id: String,

    val name: String,

    val color: Long,

    val isPinned: Boolean = false,

    val articleCount: Long = 0,

    val sortOrder: Int = 0,

    val createdAt: Long,

    val updatedAt: Long
)
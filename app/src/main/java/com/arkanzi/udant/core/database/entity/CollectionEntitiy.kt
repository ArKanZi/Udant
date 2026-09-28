package com.arkanzi.udant.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "collection",
    indices = [
        Index(value = ["name"], unique = true)
    ])
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val color: Long,

    val isPinned: Boolean = false,

    val sortOrder: Int = 0,

    val createdAt: Long,

    val updatedAt: Long
)
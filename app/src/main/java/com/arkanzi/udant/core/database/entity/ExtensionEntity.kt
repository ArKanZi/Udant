package com.arkanzi.udant.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extensions")
data class ExtensionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
)
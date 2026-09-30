package com.arkanzi.udant.core.model


data class CollectionModel(

    val id: String,

    val name: String,

    val color: Long,

    val isPinned: Boolean = false,

    val sortOrder: Int = 0,

    val articleCount: Long = 0,

    val createdAt: Long,

    val updatedAt: Long
)
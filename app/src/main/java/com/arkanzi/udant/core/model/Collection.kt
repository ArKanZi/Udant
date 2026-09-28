package com.arkanzi.udant.core.model


data class Collection(

    val id: Long = 0,

    val name: String,

    val color: Long,

    val isPinned: Boolean = false,

    val sortOrder: Int = 0,

    val createdAt: Long,

    val updatedAt: Long
)
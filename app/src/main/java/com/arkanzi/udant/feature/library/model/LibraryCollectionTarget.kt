package com.arkanzi.udant.feature.library.model

import kotlinx.serialization.Serializable
@Serializable
sealed interface LibraryCollectionTarget {

    @Serializable
    data object Default : LibraryCollectionTarget

    @Serializable
    data class User(
        val collectionId: String
    ) : LibraryCollectionTarget
}
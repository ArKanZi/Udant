package com.arkanzi.udant.feature.library.model

sealed interface LibraryCollectionTarget {
    data object Default : LibraryCollectionTarget

    data class User(
        val collectionId: String
    ) : LibraryCollectionTarget
}
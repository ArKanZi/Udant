package com.arkanzi.udant.feature.library.model

import com.arkanzi.udant.core.model.Collection

sealed interface LibraryCollection {

    data class Default(
        val articleCount: Int
    ) : LibraryCollection

    data class User(
        val collection: Collection,
        val articleCount: Int
    ) : LibraryCollection
}
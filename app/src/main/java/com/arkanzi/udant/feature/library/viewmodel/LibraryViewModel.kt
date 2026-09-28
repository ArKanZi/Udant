package com.arkanzi.udant.feature.library.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.arkanzi.udant.feature.library.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {

    var viewBy by mutableStateOf(LibraryView.COLLECTION)
        private set

    var order by mutableStateOf(LibraryOrder.LATEST)
        private set

    fun selectView(view: LibraryView) {
        viewBy = view
    }

    fun selectOrder(order: LibraryOrder) {
        this.order = order
    }
}

enum class LibraryView {
    COLLECTION,
    HISTORY,
    RECENTLY_SAVED,
    DOWNLOADED;

    fun displayName(): String = when (this) {
        COLLECTION -> "Collection"
        HISTORY -> "History"
        RECENTLY_SAVED -> "Recently Saved"
        DOWNLOADED -> "Downloaded"
    }

}

enum class LibraryOrder {
    LATEST,
    OLDEST,
    A_TO_Z,
    Z_TO_A,
    RECENT;

    fun displayName(): String = when (this) {
        LATEST -> "Latest"
        OLDEST -> "Oldest"
        A_TO_Z -> "A-Z"
        Z_TO_A -> "Z-A"
        RECENT -> "Recently Updated"
    }
}
package com.arkanzi.udant.feature.library.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.arkanzi.udant.core.model.Collection
import com.arkanzi.udant.feature.library.model.LibraryCollection
import com.arkanzi.udant.feature.library.model.LibraryCollectionTarget
import com.arkanzi.udant.feature.library.ui.components.collection.AddCollectionButton
import com.arkanzi.udant.feature.library.ui.components.collection.CollectionDialog
import com.arkanzi.udant.feature.library.ui.components.collection.CollectionItem
import com.arkanzi.udant.feature.library.viewmodel.CollectionViewModel
import com.arkanzi.udant.feature.library.viewmodel.LibraryOrder

@Composable
fun CollectionView(
    query: String,
    order: LibraryOrder,
    onCollectionClick: (LibraryCollectionTarget) -> Unit,
) {
    val viewModelStoreOwner = rememberViewModelStoreOwner()

    val viewModel: CollectionViewModel = hiltViewModel(
        viewModelStoreOwner = viewModelStoreOwner
    )

    val collections by viewModel.collections.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    val collectionNameExists by
    viewModel.collectionNameExists.collectAsStateWithLifecycle()

    var showAddCollectionDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val displayedCollections = getDisplayedCollections(
        collections = collections,
        query = query,
        order = order
    )

    var editingCollection by rememberSaveable {
        mutableStateOf<Collection?>(null)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Collections",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(
                top = 10.dp,
                bottom = 100.dp
            )
        ) {
            items(
                items = displayedCollections,
                key = { collection ->
                    when (collection) {
                        is LibraryCollection.Default -> "default"
                        is LibraryCollection.User ->
                            collection.collection.id
                    }
                }
            ) { collection ->

                when (collection) {

                    is LibraryCollection.Default -> {
                        CollectionItem(
                            name = "Default",
                            articleCount = collection.articleCount,
                            isDefault = true,
                            onClick = {
                                onCollectionClick(
                                    LibraryCollectionTarget.Default
                                )
                            }
                        )
                    }

                    is LibraryCollection.User -> {
                        CollectionItem(
                            name = collection.collection.name,
                            articleCount = collection.articleCount,
                            onClick = {
                                onCollectionClick(
                                    LibraryCollectionTarget.User(
                                        collectionId = collection.collection.id
                                    )
                                )
                            },
                            onEditClick = {
                                editingCollection= collection.collection
                            },
                            onMoreClick = {
                                // later
                            }
                        )
                    }
                }
            }

            item {
                AddCollectionButton(
                    onClick = { showAddCollectionDialog = true }
                )
            }
        }
    }

    if (showAddCollectionDialog) {
        CollectionDialog(
            title = "New Collection",
            confirmText = "Create",
            onDismiss = {
                showAddCollectionDialog = false
                viewModel.resetCollectionNameCheck()
            },
            onConfirm = { name ->
                viewModel.createCollection(name)
                showAddCollectionDialog = false
            },
            collectionNameExists = collectionNameExists,
            onResetNameCheck = { viewModel.resetCollectionNameCheck() },
            onNameChanged = { viewModel.onCollectionNameChanged(it) }
        )

    }

    editingCollection?.let { collection ->
        CollectionDialog(
            title = "Edit Collection",
            initialName = collection.name,
            confirmText = "Update",
            onDismiss = {
                editingCollection = null
                viewModel.resetCollectionNameCheck()
            },
            onConfirm = { newName ->
                viewModel.editCollection(newName, collection)
                editingCollection = null
            },
            collectionNameExists = collectionNameExists,
            onResetNameCheck = {
                viewModel.resetCollectionNameCheck()
            },
            onNameChanged = {
                viewModel.onCollectionNameChanged(it)
            }
        )
    }
}

private fun getDisplayedCollections(
    collections: List<LibraryCollection>,
    query: String,
    order: LibraryOrder
): List<LibraryCollection> {
    val defaultCollection =
        collections.filterIsInstance<LibraryCollection.Default>()

    val userCollections =
        collections
            .filterIsInstance<LibraryCollection.User>()
            .filter {
                it.collection.name.contains(
                    query.trim(),
                    ignoreCase = true
                )
            }

    val sortedCollections = when (order) {
        LibraryOrder.LATEST ->
            userCollections.sortedByDescending { it.collection.createdAt }

        LibraryOrder.OLDEST ->
            userCollections.sortedBy { it.collection.createdAt }

        LibraryOrder.A_TO_Z ->
            userCollections.sortedBy { it.collection.name }

        LibraryOrder.Z_TO_A ->
            userCollections.sortedByDescending { it.collection.name }

        LibraryOrder.RECENT ->
            userCollections.sortedByDescending { it.collection.updatedAt }
    }

    return defaultCollection + sortedCollections
}
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.arkanzi.udant.core.model.CollectionModel
import com.arkanzi.udant.core.ui.components.ConfirmationDialog
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

    val defaultCollectionId by viewModel.defaultSaveCollectionId
        .collectAsStateWithLifecycle(
            initialValue = null
        )

    val defaultArticleCount by viewModel.defaultArticleCount.collectAsStateWithLifecycle(
        initialValue = 0
    )

    val collectionNameExists by
    viewModel.collectionNameExists.collectAsStateWithLifecycle()

    var showAddCollectionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val now = System.currentTimeMillis()

    val defaultCollection = CollectionModel(
        id = "default",
        name = "Default",
        color = MaterialTheme.colorScheme.primary.toArgb().toLong(),
        isPinned = false,
        sortOrder = 0,
        articleCount = defaultArticleCount,
        createdAt = now ,
        updatedAt = now,
    )

    val displayedCollections = getDisplayedCollections(
        collections = collections,
        query = query,
        order = order
    )

    var editingCollectionModel by rememberSaveable {
        mutableStateOf<CollectionModel?>(null)
    }

    var deleteCollectionModel by rememberSaveable {
        mutableStateOf<CollectionModel?>(null)
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
            if(defaultArticleCount>0){
                item {
                    CollectionItem(
                        collection = defaultCollection,
                        defaultCollectionId = defaultCollectionId,
                        onClick = {
                            onCollectionClick(
                                LibraryCollectionTarget.Default
                            )
                        },
                        onSetDefaultClick = {
                            viewModel.updateDefaultCollection(null)
                        }
                    )
                }
            }
            items(
                items = displayedCollections,
                key = { it.id }
            ) { collection ->

                        CollectionItem(
                            collection = collection,
                            defaultCollectionId = defaultCollectionId,
                            onClick = {
                                onCollectionClick(
                                    LibraryCollectionTarget.User(
                                        collectionId = collection.id
                                    )
                                )
                            },
                            onEditClick = {
                                editingCollectionModel= collection
                            },
                            onPinClick = {
                                viewModel.updatePinCollection(!collection.isPinned,collection)
                            },
                            onSetDefaultClick = {
                                if(defaultCollectionId==collection.id){
                                viewModel.updateDefaultCollection(null)
                            }else{
                                viewModel.updateDefaultCollection(collection.id)
                            }

                            },
                            onDeleteClick = {
                                deleteCollectionModel = collection
                            }
                        )


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

    editingCollectionModel?.let { collection ->
        CollectionDialog(
            title = "Edit Collection",
            initialName = collection.name,
            confirmText = "Update",
            onDismiss = {
                editingCollectionModel = null
                viewModel.resetCollectionNameCheck()
            },
            onConfirm = { newName ->
                viewModel.updateNameCollection(newName, collection)
                editingCollectionModel = null
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

    deleteCollectionModel?.let { collection ->
        ConfirmationDialog(
            title = "Delete Collection",
            message = "Are you sure you want to delete this collection?",
            confirmText = "Delete",
            onDismiss = {
                deleteCollectionModel = null
            },
            onConfirm = {
                viewModel.deleteCollection(collection)
                deleteCollectionModel = null
            },
        )
    }
}

private fun getDisplayedCollections(
    collections: List<CollectionModel>,
    query: String,
    order: LibraryOrder
): List<CollectionModel> {
    val userCollections =
        collections
            .filter {
                it.name.contains(
                    query.trim(),
                    ignoreCase = true
                )
            }

    val sortedCollections = when (order) {
        LibraryOrder.LATEST ->
            userCollections.sortedByDescending { it.createdAt }

        LibraryOrder.OLDEST ->
            userCollections.sortedBy { it.createdAt }

        LibraryOrder.A_TO_Z ->
            userCollections.sortedBy { it.name }

        LibraryOrder.Z_TO_A ->
            userCollections.sortedByDescending { it.name }

        LibraryOrder.RECENT ->
            userCollections.sortedByDescending { it.updatedAt }
    }

    return sortedCollections
}
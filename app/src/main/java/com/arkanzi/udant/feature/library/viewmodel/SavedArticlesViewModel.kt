package com.arkanzi.udant.feature.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkanzi.udant.core.job.JobManager
import com.arkanzi.udant.core.job.model.JobRequest
import com.arkanzi.udant.core.job.model.JobType
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.feature.archive.model.ArchiveRequest
import com.arkanzi.udant.feature.archive.model.ArchiveRequestPayload
import com.arkanzi.udant.feature.archive.usecase.DeleteArchiveUseCase
import com.arkanzi.udant.feature.library.model.LibraryCollectionTarget
import com.arkanzi.udant.feature.library.model.SavedArticleUiState
import com.arkanzi.udant.feature.library.repository.SavedArticleRepository
import com.arkanzi.udant.feature.library.usecase.collection.CollectionApplicationService
import com.arkanzi.udant.feature.library.usecase.savedArticles.SavedArticleApplicationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedArticlesViewModel @Inject constructor(
    private val repository: SavedArticleRepository,
    private val savedArticleUseCase: SavedArticleApplicationService,
    private val collectionUseCase: CollectionApplicationService,
    private val jobManager: JobManager,
    private val deleteArchiveUseCase: DeleteArchiveUseCase

) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedArticleUiState())

    val uiState = _uiState.asStateFlow()

    private var collectionTarget: LibraryCollectionTarget =
        LibraryCollectionTarget.Default

    private var observeJob: Job? = null

    init {
        observeCollections()
    }

    private fun observeCollections() {
        viewModelScope.launch {
            collectionUseCase
                .getCollections()
                .collectLatest { collections ->
                    _uiState.update {
                        it.copy(
                            collections = collections
                        )
                    }
                }
        }
    }

    fun setCollectionTarget(target: LibraryCollectionTarget) {
        collectionTarget = target

        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            repository
                .getSavedArticles(collectionTarget)
                .collectLatest { articles ->
                    _uiState.update {
                        it.copy(
                            articles = articles
                        )
                    }
                }
        }
    }

    fun selectSortBy(sortBy: SavedArticleSort) {
        _uiState.update {
            it.copy(
                sortBy = sortBy
            )
        }
    }

    fun selectOrderBy(order: SavedArticleOrder) {
        _uiState.update {
            it.copy(
                orderBy = order
            )
        }
    }

    fun deleteArchive(articleId: String) {
        viewModelScope.launch {
            deleteArchiveUseCase(articleId)
        }
    }

    fun archiveSavedArticle(archiveRequest: ArchiveRequest) {
        viewModelScope.launch {
            jobManager.enqueue(
                jobRequest = JobRequest.Execute(
                    title = archiveRequest.articleTitle,
                    jobType = JobType.DOWNLOAD,
                    referenceId = archiveRequest.savedArticleId,
                    payload = ArchiveRequestPayload(
                        articleTitle = archiveRequest.articleTitle,
                        articleUrl = archiveRequest.articleUrl
                    )
                )
            )
        }
    }

    fun openCollectionDialog(article: Article) {
        viewModelScope.launch {
            val collectionIds =
                savedArticleUseCase.getCollectionIds(article.articleId)

            _uiState.update {
                it.copy(
                    showCollectionDialog = true,
                    collectionDialogArticle = article,
                    selectedCollectionIds = collectionIds.toSet(),
                    isUncategorized = collectionIds.isEmpty()
                )
            }
        }
    }

    fun toggleCollection(collectionId: String) {
        _uiState.update { state ->
            val selected = state.selectedCollectionIds.toMutableSet()

            if (collectionId in selected) {
                selected.remove(collectionId)
            } else {
                selected.add(collectionId)
            }

            state.copy(
                selectedCollectionIds = selected,
                isUncategorized = false
            )
        }
    }

    fun toggleUncategorized() {
        _uiState.update { state ->
            if (state.isUncategorized) {
                // Uncheck Uncategorized.
                // Nothing else is selected.
                state.copy(
                    isUncategorized = false
                )
            } else {
                // Check Uncategorized.
                // Clear all collections.
                state.copy(
                    isUncategorized = true,
                    selectedCollectionIds = emptySet()
                )
            }
        }
    }

    fun dismissCollectionDialog() {
        _uiState.update {
            it.copy(
                showCollectionDialog = false,
                collectionDialogArticle = null,
                selectedCollectionIds = emptySet(),
                isUncategorized = false
            )
        }
    }

    fun confirmCollectionSelection() {
        val state = _uiState.value
        val article = state.collectionDialogArticle ?: return

        viewModelScope.launch {

            if (state.selectedCollectionIds.isNotEmpty()) {

                savedArticleUseCase.updateCollections(
                    articleId = article.articleId,
                    collectionIds = state.selectedCollectionIds.toList()
                )

            } else if (state.isUncategorized) {

                savedArticleUseCase.updateCollections(
                    articleId = article.articleId,
                    collectionIds = emptyList()
                )

            } else {

                savedArticleUseCase.deleteSavedArticleFromAllCollection(article)
            }

            dismissCollectionDialog()
        }
    }

    fun deleteSavedArticle(article: Article,collectionId:String?) {
        viewModelScope.launch {
            val collectionCount =
                savedArticleUseCase
                    .getCollectionIds(article.articleId).size

                _uiState.update {
                    it.copy(
                        showDeleteDialog = true,
                        articleToDelete = article,
                        collectionIdToDelete = collectionId,
                        collectionCountToDelete = collectionCount
                    )
                }
        }
    }

    fun confirmDeleteSavedArticle(deleteFromAllCollections: Boolean,removeDownloadedArchive: Boolean) {
        viewModelScope.launch {

            val state = _uiState.value

            val article =
                state.articleToDelete
                    ?: return@launch

            if (removeDownloadedArchive){
                deleteArchiveUseCase(article.articleId)
            }

            val collectionIds =
                state.collectionIdToDelete

            if (deleteFromAllCollections || collectionIds == null) {
                savedArticleUseCase.deleteSavedArticleFromAllCollection(article)
            } else {
                savedArticleUseCase.deleteSavedArticleFromCollection(
                    article,
                    collectionIds
                )
            }
            _uiState.update {
                it.copy(
                    showDeleteDialog = false,
                    articleToDelete = null,
                    collectionIdToDelete = null,
                    collectionCountToDelete = null
                )
            }
        }
    }

    fun cancelDeleteSavedArticle() {
        _uiState.update {
            it.copy(
                showDeleteDialog = false,
                articleToDelete = null,
                collectionIdToDelete = null,
                collectionCountToDelete = null
            )
        }
    }
}

enum class SavedArticleSort {
        SAVED_DATE,
        PUBLISHED_DATE,
        TITLE,
        SOURCE;

    fun displayName(): String = when (this) {
        SAVED_DATE -> "Saved Date"
        PUBLISHED_DATE -> "Published Date"
        TITLE -> "Title"
        SOURCE -> "Source"
    }

}

enum class SavedArticleOrder {
    NEWEST_FIRST,
    OLDEST_FIRST,
    A_TO_Z,
    Z_TO_A;

    fun displayName(): String = when (this) {
        NEWEST_FIRST -> "Newest First"
        OLDEST_FIRST -> "Oldest First"
        A_TO_Z -> "A - Z"
        Z_TO_A -> "Z - A"
    }
}
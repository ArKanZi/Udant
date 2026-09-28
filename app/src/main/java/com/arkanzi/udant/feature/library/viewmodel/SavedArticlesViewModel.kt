package com.arkanzi.udant.feature.library.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.arkanzi.udant.feature.library.repository.SavedArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedArticlesViewModel @Inject constructor(

    private val repository: SavedArticleRepository,
    private val jobManager: JobManager,
    private val deleteArchiveUseCase: DeleteArchiveUseCase

) : ViewModel() {

    var sortBy by mutableStateOf(SavedArticleSort.SAVED_DATE)
        private set

    var orderBy by mutableStateOf(SavedArticleOrder.NEWEST_FIRST)
        private set

    private var collectionTarget: LibraryCollectionTarget =
        LibraryCollectionTarget.Default

    private var observeJob: Job? = null

    fun setCollectionTarget(target: LibraryCollectionTarget) {
        collectionTarget = target

        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            repository
                .getSavedArticles(collectionTarget)
                .collectLatest { articles ->
                    _articles.value = articles
                }
        }
    }

    fun selectSortBy(sortBy: SavedArticleSort) {
        this.sortBy = sortBy
    }

    fun selectOrderBy(order: SavedArticleOrder) {
        this.orderBy = order
    }

    private val _articles =
        MutableStateFlow<List<Article>>(emptyList())

    val articles = _articles.asStateFlow()

    private fun observeSavedArticles() {
        viewModelScope.launch {
            repository
                .getSavedArticles(collectionTarget)
                .collectLatest { articles ->
                    _articles.value = articles
                }
        }
    }

    fun removeSavedArticle(articleUrl: String) {
        viewModelScope.launch {
            repository.removeSavedArticle(
                articleUrl = articleUrl
            )
        }
    }

    fun deleteArchive(articleId: Long) {
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
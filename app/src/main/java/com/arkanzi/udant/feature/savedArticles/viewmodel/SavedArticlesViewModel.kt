package com.arkanzi.udant.feature.savedArticles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkanzi.udant.core.job.JobManager
import com.arkanzi.udant.core.job.model.JobRequest
import com.arkanzi.udant.core.job.model.JobType
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.feature.archive.ArchiveManager
import com.arkanzi.udant.feature.archive.model.ArchiveRequest
import com.arkanzi.udant.feature.archive.model.ArchiveRequestPayload
import com.arkanzi.udant.feature.savedArticles.repository.SavedArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedArticlesViewModel @Inject constructor(

    private val repository: SavedArticleRepository,
    private val archiveManager: ArchiveManager,
    private val jobManager: JobManager

    ) : ViewModel() {

    private val _articles =
        MutableStateFlow<List<Article>>(emptyList())

    val articles = _articles.asStateFlow()

    init {

        observeSavedArticles()
    }

    private fun observeSavedArticles() {

        viewModelScope.launch {

            repository
                .getSavedArticles()
                .collectLatest { articles ->

                    _articles.value = articles
                }
        }
    }

    fun saveArticle(article: Article) {

        viewModelScope.launch {

            repository.saveArticle(article)
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
            archiveManager.deleteArchive(articleId)
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
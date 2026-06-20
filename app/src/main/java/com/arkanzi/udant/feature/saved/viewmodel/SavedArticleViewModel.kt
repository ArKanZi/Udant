package com.arkanzi.udant.feature.saved.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.feature.archive.manager.ArchiveManager
import com.arkanzi.udant.feature.saved.repository.SavedArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedArticleViewModel @Inject constructor(

    private val repository: SavedArticleRepository,
    private val archiveManager: ArchiveManager

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

    private val _savedUrls =
        MutableStateFlow<Set<String>>(emptySet())

    val savedUrls = _savedUrls.asStateFlow()

    private fun observeSavedUrls() {

        viewModelScope.launch {

            repository
                .getSavedUrls()
                .collectLatest { urls ->

                    _savedUrls.value = urls
                }
        }
    }

    fun saveArticle(
        article: Article
    ) {

        viewModelScope.launch {

            repository.saveArticle(article)
        }
    }

    fun removeSavedArticle(
        articleUrl: String
    ) {

        viewModelScope.launch {

            repository.removeSavedArticle(
                articleUrl = articleUrl
            )
        }
    }

    fun archive(articleId:Long){
     viewModelScope.launch {
         archiveManager.archive(articleId)
     }
    }

    fun deleteArchive(articleId: Long){
        viewModelScope.launch {
            archiveManager.deleteArchive(articleId)
        }
    }


}
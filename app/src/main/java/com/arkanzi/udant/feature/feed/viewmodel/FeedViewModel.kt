package com.arkanzi.udant.feature.feed.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.feature.feed.model.FeedUiState
import com.arkanzi.udant.feature.feed.repository.FeedRepository
import com.arkanzi.udant.feature.savedArticles.repository.SavedArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(

    private val feedRepository: FeedRepository,
    private val savedArticleRepository: SavedArticlesRepository

) : ViewModel() {

    // latest articles
    private var latestArticles =
        emptyList<Article>()

    // UI State

    private val _uiState =
        MutableStateFlow(
            FeedUiState(
                isLoading = true
            )
        )

    val uiState =
        _uiState.asStateFlow()

    // Feed State

    private val visibleOrder =
        mutableListOf<String>()

    // Saved Articles State

    private val _savedUrls =
        MutableStateFlow<Set<String>>(emptySet())

    val savedUrls =
        _savedUrls.asStateFlow()

    init {
        observeSavedUrls()
        startupRefresh()
    }

    // Feed Startup

    private fun startupRefresh() {

        viewModelScope.launch {

            val result =
                feedRepository
                    .fetchFirstFeedAndReplaceCache()
            observeArticles()
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
        }
    }

    // Feed Observation

    private fun observeArticles() {

        viewModelScope.launch {

            feedRepository
                .getArticles()
                .collectLatest { articles ->

                    latestArticles = articles

                    updateVisibleFeed(
                        articles = articles
                    )
                }
        }
    }

    private fun updateVisibleFeed(
        articles: List<Article>
    ) {

        if (visibleOrder.isEmpty()) {

            visibleOrder.addAll(
                articles.map {
                    it.articleUrl
                }
            )

        } else {

            val newUrls =
                articles
                    .map {
                        it.articleUrl
                    }
                    .filter { url ->

                        url !in visibleOrder
                    }

            visibleOrder.addAll(
                newUrls
            )
        }

        val articleMap =
            articles.associateBy {
                it.articleUrl
            }

        val visibleArticles =
            visibleOrder.mapNotNull { url ->

                articleMap[url]
            }

        _uiState.value =
            _uiState.value.copy(
                articles = visibleArticles
            )
    }

    // Feed Actions

    fun fetchNextFeed() {

        if (_uiState.value.isLoading) return

        if (_uiState.value.isLoadingMore) return

        _uiState.update {
            it.copy(isLoadingMore = true)
        }

        viewModelScope.launch {

            try {

                val result =
                    feedRepository.fetchNextFeed()

                result.onFailure {

                    Log.e(
                        "FETCH_FEED_ERROR",
                        result.exceptionOrNull().toString()
                    )
                }

            } finally {

                _uiState.update {
                    it.copy(isLoadingMore = false)
                }
            }
        }
    }

    fun refreshFeed() {

        visibleOrder.clear()

        updateVisibleFeed(latestArticles)
    }

    // Saved Articles

    private fun observeSavedUrls() {

        viewModelScope.launch {

            savedArticleRepository
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

            savedArticleRepository
                .saveArticle(article)
        }
    }

    fun removeSavedArticle(
        articleUrl: String
    ) {

        viewModelScope.launch {

            savedArticleRepository
                .removeSavedArticle(articleUrl)
        }
    }
}
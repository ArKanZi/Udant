package com.arkanzi.udant.feature.feed.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.feature.feed.model.FeedUiState
import com.arkanzi.udant.feature.feed.repository.FeedRepository
import com.arkanzi.udant.feature.savedArticles.repository.SavedArticleRepository
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
    private val savedArticleRepository: SavedArticleRepository

) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("My Feed")
    val selectedCategory = _selectedCategory.asStateFlow()

    fun selectCategory(category: String) {
        _selectedCategory.value = category

        val hasArticles = latestArticles.any {
            it.category == category
        }

        if (category != "My Feed" && !hasArticles) {
            viewModelScope.launch {
                feedRepository.fetchCategory(category)
            }
        }

        updateVisibleFeed(latestArticles)
    }

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
        observeCategories()
        observeSavedUrls()
        startupRefresh()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            feedRepository
                .getCategories()
                .collectLatest { categories ->
                    _uiState.update {
                        it.copy(
                            categories = categories
                        )
                    }
                }
        }
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
            val newUrls = articles
                .map { it.articleUrl }
                .filter { url ->
                    url !in visibleOrder
                }

            visibleOrder.addAll(newUrls)
        }

        val articleMap = articles.associateBy {
            it.articleUrl
        }

        val visibleArticles = visibleOrder
            .mapNotNull { url ->
                articleMap[url]
            }
            .filter { article ->
                _selectedCategory.value == "My Feed" ||
                        article.category == _selectedCategory.value
            }

        _uiState.update {
            it.copy(
                articles = visibleArticles
            )
        }
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
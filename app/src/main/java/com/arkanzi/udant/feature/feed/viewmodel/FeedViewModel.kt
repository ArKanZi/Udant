package com.arkanzi.udant.feature.feed.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkanzi.udant.core.model.Article
import com.arkanzi.udant.feature.feed.model.FeedTarget
import com.arkanzi.udant.feature.feed.model.FeedUiState
import com.arkanzi.udant.feature.feed.repository.FeedRepository
import com.arkanzi.udant.feature.library.repository.SavedArticleRepository
import com.arkanzi.udant.feature.library.usecase.collection.CollectionApplicationService
import com.arkanzi.udant.feature.library.usecase.savedArticles.SavedArticleApplicationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(

    private val feedRepository: FeedRepository,
    private val savedArticleRepository: SavedArticleRepository,
    private val savedArticleUseCase: SavedArticleApplicationService,
    private val collectionUseCase: CollectionApplicationService,

) : ViewModel() {

    // Feed Target

    private val _selectedTarget =
        MutableStateFlow<FeedTarget>(FeedTarget.All)

    val selectedTarget =
        _selectedTarget.asStateFlow()

    fun selectTarget(target: FeedTarget) {
        _selectedTarget.value = target

        when (target) {
            FeedTarget.All -> {
                updateVisibleFeed(latestArticles)
            }

            is FeedTarget.Category -> {
                viewModelScope.launch {
                val category = _uiState.value.categories
                    .firstOrNull { it.id == target.categoryId }
                    ?: return@launch

                val extensionIds = feedRepository
                    .getExtensionIds()
                    .first()

                    feedRepository.fetchCategory(
                        extensionIds = extensionIds,
                    categoryName = category.name
                    )
                }
            }
        }
    }

    // Latest Articles

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
        observeCollections()
        observeSavedUrls()
        startupRefresh()
    }

    // Categories

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

    // Collections

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

    // Feed Startup

    private fun startupRefresh() {
        viewModelScope.launch {

            val extensionIds = feedRepository
                .getExtensionIds()
                .first()

            feedRepository.fetchFirstFeedAndReplaceCache(extensionIds)

            observeArticles()

            _uiState.update {
                it.copy(isLoading = false)
            }
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


                when (val target = _selectedTarget.value) {

                    FeedTarget.All -> true

                    is FeedTarget.Category -> {

                        val category = _uiState.value.categories
                            .firstOrNull {
                                it.id == target.categoryId
                            }

                        article.category == category?.name?.lowercase()
                    }
                }
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

                when (val target = _selectedTarget.value) {

                    FeedTarget.All -> {
                        val extensionIds = feedRepository
                            .getExtensionIds()
                            .first()

                        feedRepository.fetchNextFeed(
                            extensionIds = extensionIds
                        )
                    }

                    is FeedTarget.Category -> {
                        val category = _uiState.value.categories
                            .firstOrNull { it.id == target.categoryId }
                            ?: return@launch

                        feedRepository.fetchCategory(
                            extensionIds = listOf(category.extensionId),
                            categoryName = category.name
                        )
                    }
                }

            } finally {
                _uiState.update {
                    it.copy(
                        isLoadingMore = false
                    )
                }
            }
        }
    }

    fun refreshFeed() {

        visibleOrder.clear()

        updateVisibleFeed(
            latestArticles
        )
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
            savedArticleUseCase.insertArticle(article)
        }
    }

    fun deleteSavedArticle(
        article: Article
    ) {
        viewModelScope.launch {

            val collectionIds =
                savedArticleUseCase
                    .getCollectionIds(article.articleId)

            if (collectionIds.size > 1) {

                _uiState.update {
                    it.copy(
                        showDeleteDialog = true,
                        articleToDelete = article
                    )
                }

            } else {

                savedArticleUseCase
                    .deleteSavedArticleFromAllCollection(article)
            }
        }
    }

    fun openCollectionDialog(
        article: Article
    ) {
        viewModelScope.launch {

            val collectionIds =
                savedArticleUseCase
                    .getCollectionIds(article.articleId)

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

    fun toggleCollection(
        collectionId: String
    ) {
        _uiState.update { state ->

            val selected =
                state.selectedCollectionIds.toMutableSet()

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

                state.copy(
                    isUncategorized = false
                )

            } else {

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
        val article = state.collectionDialogArticle
            ?: return

        viewModelScope.launch {

            if (state.selectedCollectionIds.isNotEmpty()) {

                savedArticleUseCase.updateCollections(
                    articleId = article.articleId,
                    collectionIds =
                        state.selectedCollectionIds.toList()
                )

            } else if (state.isUncategorized) {

                savedArticleUseCase.updateCollections(
                    articleId = article.articleId,
                    collectionIds = emptyList()
                )

            } else {

                savedArticleUseCase
                    .deleteSavedArticleFromAllCollection(article)
            }

            dismissCollectionDialog()
        }
    }

    fun confirmDeleteSavedArticle() {

        viewModelScope.launch {

            val article =
                _uiState.value.articleToDelete
                    ?: return@launch

            savedArticleUseCase
                .deleteSavedArticleFromAllCollection(article)

            _uiState.update {
                it.copy(
                    showDeleteDialog = false,
                    articleToDelete = null
                )
            }
        }
    }

    fun cancelDeleteSavedArticle() {
        _uiState.update {
            it.copy(
                showDeleteDialog = false,
                articleToDelete = null
            )
        }
    }
}
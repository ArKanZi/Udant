package com.arkanzi.udant.core.model

interface UdantExtension {
    val name: String
    val developerName:String
    val version: String
    val baseUrl: String

    fun getCategories(): List<ExtensionCategory>

    suspend fun fetchHome(page:Int): FeedResult

    suspend fun fetchCategory(categoryName: String,page:Int): FeedResult

    suspend fun enrich(article: Article): Article
}
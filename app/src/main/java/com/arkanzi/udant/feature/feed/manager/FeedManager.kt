//package com.arkanzi.udant.feature.feed.manager
//
//import com.arkanzi.udant.core.database.dao.ArticleDao
//import com.arkanzi.udant.core.database.dao.ExtensionDao
//import com.arkanzi.udant.core.database.dao.FeedCategoryDao
//import com.arkanzi.udant.core.extension.ExtensionManager
//import com.arkanzi.udant.core.model.Article
//import com.arkanzi.udant.extension.theindianexpress.TheIndianExpress
//import com.arkanzi.udant.extension.thetimesofindia.TheTimesOfIndiaMain
//import javax.inject.Inject
//
//class FeedManager @Inject constructor(
//    private val theTimesOfIndiaMain: TheTimesOfIndiaMain,
//    private val theIndianExpress: TheIndianExpress,
//    private val extensionDao: ExtensionDao,
//    private val feedCategoryDao: FeedCategoryDao,
//    private val articleDao: ArticleDao,
//) {
//
//    val extensions = extensionDao.getExtensions()
//
//    val categories = feedCategoryDao.getCategories()
//
//    suspend fun fetchCategory(
//        extensionId: String,
//        categoryId: String
//    ) {
//        val articles = when (extensionId) {
//            theTimesOfIndiaMain::class.java.simpleName ->
//                theTimesOfIndiaMain.fetchCategory(categoryId)
//
//            theIndianExpress::class.java.simpleName ->
//                theIndianExpress.fetchCategory(categoryId)
//
//            else -> return
//        }
//
//        addArticles(articles)
//    }
//
//    suspend fun addArticles(articles: List<Article>) {
//        // get current max feedOrder
//        // assign orders
//        // save to Room
//    }
//}
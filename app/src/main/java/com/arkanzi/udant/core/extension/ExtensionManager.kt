package com.arkanzi.udant.core.extension

import android.util.Log
import com.arkanzi.udant.core.database.dao.ExtensionDao
import com.arkanzi.udant.core.database.dao.FeedCategoryDao
import com.arkanzi.udant.core.database.entity.ExtensionEntity
import com.arkanzi.udant.core.database.entity.FeedCategoryEntity
import com.arkanzi.udant.core.mapper.toEntity
import com.arkanzi.udant.core.util.toGenerateId
import com.arkanzi.udant.extension.theindianexpress.TheIndianExpress
import com.arkanzi.udant.extension.thetimesofindia.TheTimesOfIndiaMain
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExtensionManager @Inject constructor(
    private val theTimesOfIndiaMain: TheTimesOfIndiaMain,
    private val theIndianExpress: TheIndianExpress,
    private val extensionDao: ExtensionDao,
    private val feedCategoryDao: FeedCategoryDao,

) {

    suspend fun syncExtensions() {
        val extension = mutableListOf<ExtensionEntity>()
        extension.add(
            ExtensionEntity(
                id = "udant",
                name = "Udant"
            )
        )
        extension.add(
            ExtensionEntity(
                id = theTimesOfIndiaMain::class.java.simpleName,
                name = "The Times of India"
            )
        )
        extension.add(
            ExtensionEntity(
                id = theIndianExpress::class.java.simpleName,
                name = "The Indian Express"
            )
        )
        extensionDao.upsertExtensions(extension)
        syncCategories()
    }

    suspend fun syncCategories() {
        val extensions = extensionDao.getExtensions().first()

        val categories = mutableListOf(
            FeedCategoryEntity(
                id = ("Udant"+"My Feed").toGenerateId(),
                name = "My Feed",
                extensionId = "udant"
            )
        )

        extensions.forEach { extension ->
            val extensionCategories = when (extension.id) {
                theTimesOfIndiaMain::class.java.simpleName ->
                    theTimesOfIndiaMain.getCategories()

                theIndianExpress::class.java.simpleName ->
                    theIndianExpress.getCategories()

                else -> emptyList()
            }

            categories.addAll(
                extensionCategories.map {
                    it.toEntity(extension.id)
                }
            )
        }

        Log.d("ExtensionManager", "Final categories: $categories")

        feedCategoryDao.upsertCategories(categories)
    }
}
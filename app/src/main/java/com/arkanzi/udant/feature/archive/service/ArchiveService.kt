package com.arkanzi.udant.feature.archive.service

import android.app.Service
import android.content.Intent
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.arkanzi.udant.core.database.entity.SavedArticleEntity
import com.arkanzi.udant.core.notification.NotificationController
import com.arkanzi.udant.core.util.toSafeFileName
import com.arkanzi.udant.core.webview.WebViewConfig
import com.arkanzi.udant.core.webview.WebViewProvider
import com.arkanzi.udant.feature.archive.repository.ArchiveRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ArchiveService : Service() {

    @Inject
    lateinit var notificationController: NotificationController

    @Inject
    lateinit var archiveRepository:
            ArchiveRepository

    private val serviceScope =
        CoroutineScope(
            SupervisorJob() +
                    Dispatchers.IO
        )

    override fun onCreate() {

        super.onCreate()

        val foregroundNotification =
            notificationController
                .getArchiveForegroundNotification()

        startForeground(
            foregroundNotification.notificationId,
            foregroundNotification.notification
        )
    }

    override fun onDestroy() {

        super.onDestroy()

        serviceScope.cancel()

        Log.d(
            TAG,
            "Service destroyed"
        )
    }

    private fun archiveArticle(
        savedArticle: SavedArticleEntity,
        safFolderUri: String?
    ) {


        val webView =
            WebViewProvider().create(context = applicationContext, config = WebViewConfig())

        val archivePath =
            File(
                filesDir,
                "temp_archive.mht"
            ).absolutePath

        webView.webViewClient =
            object : WebViewClient() {

                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {
                    Log.d(
                        TAG,
                        Thread.currentThread().name
                    )

                    Log.d(
                        TAG,
                        "Page finished loading: $url"
                    )

                    view?.saveWebArchive(
                        archivePath,
                        false
                    ) { savedPath ->

                        Log.d(
                            TAG,
                            Thread.currentThread().name
                        )

                        Log.d(
                            TAG,
                            "Archive saved: $savedPath"
                        )

                        savedPath?.let { path ->

                            val tempFile = File(path)

                            Log.d(
                                TAG,
                                "Exists = ${tempFile.exists()}"
                            )

                            Log.d(
                                TAG,
                                "Size = ${tempFile.length()}"
                            )

                            try {

                                val folder =
                                    safFolderUri?.toUri()?.let {
                                        DocumentFile.fromTreeUri(
                                            this@ArchiveService,
                                            it
                                        )
                                    }
                                val fileName =
                                    savedArticle.title
                                        .toSafeFileName()

                                val archiveFile =
                                    folder?.createFile(
                                        "message/rfc822",
                                        "${fileName}.mht"
                                    )

                                if (archiveFile == null) {
                                    serviceScope.launch {
                                        archiveRepository
                                            .setFailed(
                                                savedArticle.savedArticleId
                                            )
                                        notificationController.showArchiveFailed()
                                    }

                                    Log.e(
                                        TAG,
                                        "Failed to create archive file"
                                    )
                                    stopSelf()
                                    return@let
                                }

                                archiveFile.uri.let { archiveUri ->

                                    contentResolver
                                        .openOutputStream(
                                            archiveUri
                                        )
                                        ?.use { output ->

                                            tempFile
                                                .inputStream()
                                                .use { input ->
                                                    Log.d(
                                                        TAG,
                                                        Thread.currentThread().name
                                                    )
                                                    input.copyTo(
                                                        output
                                                    )
                                                }
                                        }

                                    Log.d(
                                        TAG,
                                        "Copied to SAF: $archiveUri"
                                    )
                                    serviceScope.launch {

                                        archiveRepository
                                            .setCompleted(
                                                savedArticleId =
                                                    savedArticle.savedArticleId,

                                                archiveUri =
                                                    archiveUri.toString()
                                            )
                                        notificationController.showArchiveCompleted()

                                        Log.d(
                                            TAG, archiveRepository
                                                .getSavedArticleById(
                                                    savedArticle.savedArticleId
                                                ).toString()
                                        )
                                        stopSelf()
                                    }
                                }

                                val deleted =
                                    tempFile.delete()

                                Log.d(
                                    TAG,
                                    "Temp file deleted = $deleted"
                                )

                            } catch (e: Exception) {
                                serviceScope.launch {

                                    archiveRepository
                                        .setFailed(
                                            savedArticle.savedArticleId
                                        )
                                    notificationController.showArchiveFailed()
                                    stopSelf()
                                }

                                Log.e(
                                    TAG,
                                    "SAF copy failed",
                                    e
                                )
                            }
                        }

                    }
                }
            }

        webView.loadUrl(savedArticle.articleUrl)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        val savedArticleId =
            intent?.getLongExtra(
                "saved_article_id",
                -1L
            ) ?: return START_NOT_STICKY

        Log.d(
            TAG,
            "Received article id = $savedArticleId"
        )

        serviceScope.launch {
            val safFolderUri = archiveRepository.getArchiveFolderUri().firstOrNull()

            val article =
                archiveRepository
                    .getSavedArticleById(
                        savedArticleId
                    )

            article?.let {

                archiveRepository
                    .setArchiving(
                        it.savedArticleId
                    )

                withContext(
                    Dispatchers.Main
                ) {
                    archiveArticle(
                        savedArticle = it,
                        safFolderUri = safFolderUri
                    )
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(
        intent: Intent?
    ) = null

    companion object {

        private const val TAG =
            "ArchiveService"

    }
}
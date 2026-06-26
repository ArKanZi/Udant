package com.arkanzi.udant.feature.archive.service

import android.app.Service
import android.content.Intent
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.arkanzi.udant.core.storage.StorageManager
import com.arkanzi.udant.core.webview.WebViewConfig
import com.arkanzi.udant.core.webview.WebViewProvider
import com.arkanzi.udant.feature.archive.manager.ArchiveManager
import com.arkanzi.udant.feature.archive.model.ArchiveServiceResult
import com.arkanzi.udant.feature.archive.model.ArchiveUpdate
import com.arkanzi.udant.feature.archive.notification.ArchiveNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ArchiveService : Service() {

    @Inject
    lateinit var archiveNotification: ArchiveNotification

    @Inject
    lateinit var archiveManager: ArchiveManager

    @Inject
    lateinit var storageManager: StorageManager

    private val serviceScope =
        CoroutineScope(
            SupervisorJob() +
                    Dispatchers.IO
        )

    override fun onCreate() {

        super.onCreate()

        val foregroundNotification =
            archiveNotification
                .getForegroundNotification()

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
        articleId:Long,
        articleTitle: String,
        articleUrl: String,
    ) {

        val webView =
            WebViewProvider()
                .create(
                    context = applicationContext,
                    config = WebViewConfig()
                )

        webView.webChromeClient =
            object : WebChromeClient() {

                override fun onProgressChanged(
                    view: WebView?,
                    newProgress: Int
                ) {
                    archiveNotification.showArchiveProgress(
                        text = "Loading webpage",
                        progress = newProgress
                    )
                }
            }

        val archivePath = storageManager.getLocalFile("temp_archive.mht").absolutePath

        webView.webViewClient =
            object : WebViewClient() {

                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {

                    view?.saveWebArchive(
                        archivePath,
                        false
                    ) { savedPath ->

                        serviceScope.launch {

                            try {

                                if (savedPath == null) {

                                    archiveManager.onArchiveServiceResult(
                                        ArchiveServiceResult.Failure(
                                            savedArticleId = articleId,
                                            throwable = IllegalStateException(
                                                "saveWebArchive failed"
                                            )
                                        )
                                    )

                                } else {

                                    archiveManager.onArchiveServiceResult(
                                        ArchiveServiceResult.Success(
                                            savedArticleId = articleId,
                                            fileName = articleTitle,
                                            localFile = File(savedPath)
                                        )
                                    )
                                }

                            } finally {

                                withContext(Dispatchers.Main) {
                                    webView.destroy()
                                }

                                stopSelf()
                            }
                        }
                    }
                }
            }

        webView.loadUrl(articleUrl)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        val articleId =
            intent?.getLongExtra(
                "article_id",
                -1L
            ) ?: return START_NOT_STICKY

        val articleTitle = intent.getStringExtra(
            "article_title",
        ) ?: return START_NOT_STICKY

        val articleUrl = intent.getStringExtra(
            "article_url",
        ) ?: return START_NOT_STICKY


        serviceScope.launch {

            archiveManager.handleArchiveStatus(
                ArchiveUpdate.Archiving(articleId)
            )
                withContext(
                    Dispatchers.Main
                ) {
                    archiveArticle(
                        articleId = articleId,
                        articleTitle = articleTitle,
                        articleUrl = articleUrl,
                    )
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
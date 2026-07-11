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
import com.arkanzi.udant.core.job.registry.ArchiveJobRegistry
import com.arkanzi.udant.feature.archive.manager.ArchiveManager
import com.arkanzi.udant.feature.archive.model.ArchiveResponse
import com.arkanzi.udant.feature.archive.notification.ArchiveNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ArchiveService : Service() {

    @Inject
    lateinit var archiveNotification: ArchiveNotification

    @Inject
    lateinit var archiveManager: ArchiveManager

    @Inject
    lateinit var archiveJobRegistry: ArchiveJobRegistry

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
        jobId: String,
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

        val archivePath = storageManager.getTempArchiveFile(jobId).absolutePath

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

                                    archiveJobRegistry.complete(
                                        jobId,
                                        result = ArchiveResponse.Failure(
                                            jobId = jobId,
                                            throwable = IllegalStateException("saveWebArchive failed")
                                        )
                                    )

                                } else {

                                    archiveJobRegistry.complete(
                                        jobId=jobId,
                                        result = ArchiveResponse.Success(jobId = jobId)
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
        Log.d("ArchiveService", "Loading URL")
        webView.loadUrl(articleUrl)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d("ArchiveService", "Started")


        val jobId = intent?.getStringExtra(
            "job_id",
        ) ?: return START_NOT_STICKY

        val articleUrl = intent.getStringExtra(
            "article_url",
        ) ?: return START_NOT_STICKY


        serviceScope.launch {
            withContext(
                Dispatchers.Main
            ) {
                archiveArticle(
                    jobId = jobId,
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
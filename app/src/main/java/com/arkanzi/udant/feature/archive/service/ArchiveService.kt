package com.arkanzi.udant.feature.archive.service

import android.app.Service
import android.content.Intent
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.arkanzi.udant.core.job.registry.ArchiveJobRegistry
import com.arkanzi.udant.core.storage.StorageManager
import com.arkanzi.udant.core.webview.WebViewConfig
import com.arkanzi.udant.core.webview.WebViewProvider
import com.arkanzi.udant.feature.archive.model.ArchiveFailureReason
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
    lateinit var archiveJobRegistry: ArchiveJobRegistry

    @Inject
    lateinit var storageManager: StorageManager

    private val serviceScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var isCompleted = false

    override fun onCreate() {
        super.onCreate()

        val foregroundNotification =
            archiveNotification.getForegroundNotification()

        startForeground(
            foregroundNotification.notificationId,
            foregroundNotification.notification
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val jobId = intent?.getStringExtra("job_id")
            ?: return START_NOT_STICKY

        val articleUrl = intent.getStringExtra("article_url")
            ?: return START_NOT_STICKY

        serviceScope.launch {
            withContext(Dispatchers.Main) {
                archiveArticle(
                    jobId = jobId,
                    articleUrl = articleUrl
                )
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun archiveArticle(
        jobId: String,
        articleUrl: String
    ) {

        val webView = runCatching {
            WebViewProvider().create(
                context = applicationContext,
                config = WebViewConfig()
            )
        }.getOrElse { throwable ->

            serviceScope.launch {
                complete(
                    webView = null,
                    jobId = jobId,
                    result = ArchiveResponse.Failure(
                        jobId = jobId,
                        timestamp = System.currentTimeMillis(),
                        header = "WebView Creation Failed",
                        source = ArchiveService::class,
                        reason = ArchiveFailureReason.ArchiveService.WebViewCreationFailed,
                        throwable = throwable
                    )
                )
            }

            return
        }

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

        val archivePath = runCatching {
            storageManager
                .getTempArchiveFile(jobId)
                .absolutePath
        }.getOrElse { throwable ->

            serviceScope.launch {
                complete(
                    webView = webView,
                    jobId = jobId,
                    result = ArchiveResponse.Failure(
                        jobId = jobId,
                        timestamp = System.currentTimeMillis(),
                        header = "Storage Path Unknown",
                        source = ArchiveService::class,
                        reason = ArchiveFailureReason.ArchiveService.StoragePathNotFound,
                        throwable = throwable
                    )
                )
            }

            return
        }

        webView.webViewClient =
            object : WebViewClient() {

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)

                    if (request?.isForMainFrame != true) return

                    serviceScope.launch {
                        complete(
                            webView = webView,
                            jobId = jobId,
                            result = ArchiveResponse.Failure(
                                jobId = jobId,
                                timestamp = System.currentTimeMillis(),
                                header = "WebView Loading Failed",
                                source = ArchiveService::class,
                                reason = ArchiveFailureReason.ArchiveService.PageLoadFailed,
                                throwable = Exception(
                                    error?.description?.toString()
                                        ?: "Unknown page load error"
                                )
                            )
                        )
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(
                        view,
                        request,
                        errorResponse
                    )

                    if (request?.isForMainFrame != true) return

                    serviceScope.launch {
                        complete(
                            webView = webView,
                            jobId = jobId,
                            result = ArchiveResponse.Failure(
                                jobId = jobId,
                                timestamp = System.currentTimeMillis(),
                                header = "WebView Loading Failed",
                                source = ArchiveService::class,
                                reason = ArchiveFailureReason.ArchiveService.HttpError,
                                throwable = Exception(
                                    "HTTP ${errorResponse?.statusCode}"
                                )
                            )
                        )
                    }
                }

                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {

                    view?.saveWebArchive(
                        archivePath,
                        false
                    ) { savedPath ->

                        serviceScope.launch {

                            if (savedPath == null) {

                                complete(
                                    webView = webView,
                                    jobId = jobId,
                                    result = ArchiveResponse.Failure(
                                        jobId = jobId,
                                        timestamp = System.currentTimeMillis(),
                                        header = "Creating Temporary File Failed",
                                        source = ArchiveService::class,
                                        reason = ArchiveFailureReason.ArchiveService.SaveWebArchiveFailed,
                                        throwable = IllegalStateException(
                                            "saveWebArchive failed"
                                        )
                                    )
                                )

                            } else {

                                complete(
                                    webView = webView,
                                    jobId = jobId,
                                    result = ArchiveResponse.Success(
                                        jobId = jobId,
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                            }
                        }
                    }
                }
            }

        runCatching {
            webView.loadUrl(articleUrl)
        }.getOrElse { throwable ->

            serviceScope.launch {
                complete(
                    webView = webView,
                    jobId = jobId,
                    result = ArchiveResponse.Failure(
                        jobId = jobId,
                        timestamp = System.currentTimeMillis(),
                        header = "WebView Loading Failed",
                        source = ArchiveService::class,
                        reason = ArchiveFailureReason.ArchiveService.PageLoadStartFailed,
                        throwable = throwable
                    )
                )
            }

            return
        }
    }

    private suspend fun complete(
        webView: WebView?,
        jobId: String,
        result: ArchiveResponse
    ) {
        if (isCompleted) return

        isCompleted = true

        archiveJobRegistry.complete(
            jobId = jobId,
            result = result
        )

        withContext(Dispatchers.Main) {
            webView?.destroy()
        }

        stopSelf()
    }
}
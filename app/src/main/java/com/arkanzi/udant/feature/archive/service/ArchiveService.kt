package com.arkanzi.udant.feature.archive.service

import android.app.Service
import android.content.Intent
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.arkanzi.udant.core.job.download.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.download.model.DownloadProgressState
import com.arkanzi.udant.core.job.download.notification.DownloadNotification
import com.arkanzi.udant.feature.archive.registry.ArchiveRegistry
import com.arkanzi.udant.core.storage.StorageManager
import com.arkanzi.udant.core.webview.WebViewConfig
import com.arkanzi.udant.core.webview.WebViewProvider
import com.arkanzi.udant.feature.archive.model.ArchiveFailureReason
import com.arkanzi.udant.feature.archive.model.ArchiveResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class ArchiveService : Service() {
    @Inject
    lateinit var downloadNotification: DownloadNotification

    @Inject
    lateinit var downloadDispatcher: DownloadDispatcher
    @Inject
    lateinit var archiveRegistry: ArchiveRegistry
    @Inject
    lateinit var storageManager: StorageManager
    private val serviceScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var shutdownJob: Job? = null

    private var isCompleted = false
    private var isForegroundStarted = false



    override fun onCreate() {
        super.onCreate()
        Log.d("ArchiveService", "onCreate")
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        cancelShutdown()

        val jobId = intent?.getStringExtra("job_id")
            ?: return START_NOT_STICKY

        val articleUrl = intent.getStringExtra("article_url")
            ?: return START_NOT_STICKY

        if (!isForegroundStarted) {

            val foreground = downloadNotification
                .createForegroundNotification()
            startForeground(
                foreground.notificationId,
                foreground.notification
            )

            isForegroundStarted = true
        }





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

        isForegroundStarted = false
        shutdownJob?.cancel()
        serviceScope.cancel()
    }

    private fun archiveArticle(
        jobId: String,
        articleUrl: String
    ) {
        isCompleted = false

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
                    serviceScope.launch {
                        downloadDispatcher.emitProgress(
                            DownloadProgressState.Loading(
                                notificationId = jobId.hashCode(),
                                progress = newProgress
                            )
                        )
                    }
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
                    serviceScope.launch {
                        downloadDispatcher.emitProgress(
                            DownloadProgressState.Generating(notificationId = jobId.hashCode())
                        )
                    }

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
        archiveRegistry.complete(
            jobId = jobId,
            result = result
        )

        withContext(Dispatchers.Main) {
            webView?.destroy()
        }

        scheduleShutdown()
    }

    private fun cancelShutdown() {
        shutdownJob?.cancel()
        shutdownJob = null
    }

    private fun scheduleShutdown() {

        cancelShutdown()

        shutdownJob = serviceScope.launch {

            delay(IDLE_TIMEOUT_MS.milliseconds)

            withContext(Dispatchers.Main) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                isForegroundStarted = false
                stopSelf()
            }

        }
    }
    companion object{
        private const val IDLE_TIMEOUT_MS = 15_000L
    }
}
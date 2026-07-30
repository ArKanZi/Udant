package com.arkanzi.udant.feature.archive.service

import android.app.Service
import android.content.Intent
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
import com.arkanzi.udant.feature.archive.model.ArchiveResult
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

    private var currentWebView: WebView? = null

    private var shutdownJob: Job? = null
    private var isCompleted = false
    private var isForegroundStarted = false

    @Volatile
    private var cancelRequested = false


    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        cancelShutdown()

        val jobId =
            intent?.getStringExtra(ArchiveContract.EXTRA_JOB_ID)
                ?: return START_NOT_STICKY

        when (intent.action) {

            ArchiveContract.ACTION_START -> {


                val articleUrl =
                    intent.getStringExtra(ArchiveContract.EXTRA_ARTICLE_URL)
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
            }

            ArchiveContract.ACTION_STOP -> {
                stopCurrentArchive(jobId = jobId)
            }

            else -> return START_NOT_STICKY
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?) = null


    override fun onDestroy() {
        isForegroundStarted = false

        currentWebView?.destroy()
        currentWebView = null
        shutdownJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun archiveArticle(
        jobId: String,
        articleUrl: String
    ) {
        cancelRequested = false
        isCompleted = false


        val webView = runCatching {
            currentWebView ?: WebViewProvider().create(
                context = applicationContext,
                config = WebViewConfig()
            ).also { currentWebView = it }
        }.getOrElse { throwable ->

            serviceScope.launch {
                complete(
                    jobId = jobId,
                    result = ArchiveResult.Failure(
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
                    if (cancelRequested) return
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
                    jobId = jobId,
                    result = ArchiveResult.Failure(
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
                    if (cancelRequested) return
                    super.onReceivedError(view, request, error)

                    if (request?.isForMainFrame != true) return

                    serviceScope.launch {
                        complete(
                            jobId = jobId,
                            result = ArchiveResult.Failure(
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
                    if (cancelRequested) return
                    super.onReceivedHttpError(
                        view,
                        request,
                        errorResponse
                    )

                    if (request?.isForMainFrame != true) return

                    serviceScope.launch {
                        complete(
                            jobId = jobId,
                            result = ArchiveResult.Failure(
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
                    if (cancelRequested) return
                    serviceScope.launch {
                        downloadDispatcher.emitProgress(
                            DownloadProgressState.Generating(notificationId = jobId.hashCode())
                        )
                    }

                    view?.saveWebArchive(
                        archivePath,
                        false
                    ) { savedPath ->
                        if (cancelRequested) return@saveWebArchive

                        serviceScope.launch {

                            if (savedPath == null) {

                                complete(
                                    jobId = jobId,
                                    result = ArchiveResult.Failure(
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
                                    jobId = jobId,
                                    result = ArchiveResult.Success(
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
                    jobId = jobId,
                    result = ArchiveResult.Failure(
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

    private fun stopCurrentArchive(jobId: String) {

        cancelRequested = true

        currentWebView?.stopLoading()

        serviceScope.launch {
            jobId.let { jobId ->
                complete(
                    jobId = jobId,
                    result = ArchiveResult.Paused(
                        jobId = jobId,
                        timestamp = System.currentTimeMillis()
                    ),

                    )
            }
        }

    }

    private fun complete(
        jobId: String,
        result: ArchiveResult,
    ) {
        if (isCompleted) return
        isCompleted = true
        archiveRegistry.complete(
            jobId = jobId,
            result = result
        )

        cancelRequested = false

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

    companion object {
        private const val IDLE_TIMEOUT_MS = 15_000L
    }
}
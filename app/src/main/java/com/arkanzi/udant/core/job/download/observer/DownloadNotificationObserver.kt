package com.arkanzi.udant.core.job.download.observer

import com.arkanzi.udant.core.job.download.dispatcher.DownloadDispatcher
import com.arkanzi.udant.core.job.download.model.DownloadProgressState
import com.arkanzi.udant.core.job.download.notification.DownloadNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadNotificationObserver @Inject constructor(

    private val downloadDispatcher: DownloadDispatcher,
    private val downloadNotification: DownloadNotification

) {

    private val observerScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    var alreadyStarted = false

    fun start() {

        if (alreadyStarted) return

        alreadyStarted = true


        observerScope.launch {

            downloadDispatcher.downloadProgressState.collect { progress ->

                when (progress) {

                    is DownloadProgressState.Queued ->
                        downloadNotification.showQueued(
                            notificationId = progress.notificationId
                        )

                    is DownloadProgressState.Loading ->
                        downloadNotification.showProgress(
                            notificationId = progress.notificationId,
                            progress.progress
                        )

                    is DownloadProgressState.Generating ->
                        downloadNotification.showGenerating(
                            notificationId = progress.notificationId
                        )

                    is DownloadProgressState.Moving ->
                        downloadNotification.showMoving(
                            notificationId = progress.notificationId
                        )

                    is DownloadProgressState.Completed ->
                        downloadNotification.showCompleted(
                            notificationId = progress.notificationId
                        )

                    is DownloadProgressState.Failed ->
                        downloadNotification.showFailed(
                            notificationId = progress.notificationId,
                            reason = progress.reason.toString()
                        )
                }
            }
        }
    }
}
package com.arkanzi.udant.core.notification

import android.app.NotificationManager
import android.content.Context
import com.arkanzi.udant.core.model.ForegroundNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationController @Inject constructor(
    @ApplicationContext
    private val context: Context,

    private val notificationHelper: NotificationHelper

) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showArchiveQueued() {
        notificationManager.notify(
            NotificationChannels.ARCHIVE_PROGRESSION_ID,
            notificationHelper.createArchiveNotification(
                contentText = "Article in Queue",
                setOngoing = true
            )
        )
    }

    fun showArchiveCompleted() {
        notificationManager.notify(
            NotificationChannels.ARCHIVE_STATUS_ID,
            notificationHelper.createArchiveNotification(
                contentText = "Archiving Completed",
                setOngoing = false
            )
        )
    }

    fun showArchiveFailed() {
        notificationManager.notify(
            NotificationChannels.ARCHIVE_STATUS_ID,
            notificationHelper.createArchiveNotification(
                contentText = "Failed to Archive Article",
                setOngoing = false
            )
        )
    }

    fun getArchiveForegroundNotification(): ForegroundNotification =
        ForegroundNotification(
            notificationId = NotificationChannels
                .ARCHIVE_PROGRESSION_ID,
            notification = notificationHelper
                .createArchiveNotification(
                    contentText = "Saving article",
                    setOngoing = true
                )
        )


    fun cancelArchiveProgress() {
        TODO()
    }
}
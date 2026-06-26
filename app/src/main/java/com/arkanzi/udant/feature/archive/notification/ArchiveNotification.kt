package com.arkanzi.udant.feature.archive.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.arkanzi.udant.MainActivity
import com.arkanzi.udant.R
import com.arkanzi.udant.core.model.ForegroundNotification
import com.arkanzi.udant.core.notification.NotificationChannels
import com.arkanzi.udant.core.notification.NotificationController
import com.arkanzi.udant.core.notification.NotificationIds
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveNotification @Inject constructor(
    private val notificationController: NotificationController,

    @param:ApplicationContext
    private val context: Context

) {
    private val intent = Intent(context, MainActivity::class.java).apply {
        putExtra(
            "destination",
            "saved"
        )
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    private val pendingIntent: PendingIntent? = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    fun getForegroundNotification(): ForegroundNotification {

        return ForegroundNotification(
            notificationId = NotificationIds.Archive.FOREGROUND,
            notification = notificationController.createNotification(
                channelId = NotificationChannels.ARCHIVE_CHANNEL_ID,
                title = "Archiving Article",
                text = "Saving article",
                icon = R.drawable.ic_notification,
                onGoing = true,
                pendingIntent = pendingIntent
            )
        )
    }

    fun showArchiveQueued() {
        notificationController.show(
            channelId = NotificationChannels.ARCHIVE_CHANNEL_ID,
            notificationId = NotificationIds.Archive.FOREGROUND,
            title = "Archiving Article",
            text = "Article in Queue",
            icon = R.drawable.ic_notification,
            onGoing = true,
            pendingIntent = pendingIntent
        )
    }

    fun showArchiveProgress(
        text: String,
        progress: Int? = null,
        indeterminate: Boolean = false
    ) {
        notificationController.show(
            notificationId = NotificationIds.Archive.FOREGROUND,
            channelId = NotificationChannels.ARCHIVE_CHANNEL_ID,
            title = "Archiving Article",
            text = text,
            icon = R.drawable.ic_notification,
            onGoing = true,
            pendingIntent = pendingIntent,
            progress = progress,
            indeterminate = indeterminate
        )
    }


    fun showArchiveCompleted() {
        notificationController.show(
            channelId = NotificationChannels.ARCHIVE_CHANNEL_ID,
            notificationId = NotificationIds.Archive.STATUS,
            title = "Archiving Article",
            text = "Archiving Completed",
            icon = R.drawable.ic_notification,
            onGoing = false,
            pendingIntent = pendingIntent
        )
    }

    fun showArchiveFailed(error: Throwable) {
        notificationController.show(
            channelId = NotificationChannels.ARCHIVE_CHANNEL_ID,
            notificationId = NotificationIds.Archive.STATUS,
            title = "Archiving Article",
            text = error.toString(),
            icon = R.drawable.ic_notification,
            onGoing = false,
            pendingIntent = pendingIntent
        )
    }
}
package com.arkanzi.udant.core.job.download.notification

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


class DownloadNotification @Inject constructor(
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

    fun createForegroundNotification(): ForegroundNotification {

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

    fun showQueued(notificationId: Int) {
        notificationController.show(
            channelId = NotificationChannels.ARCHIVE_CHANNEL_ID,
            notificationId = notificationId,
            title = "Archiving Article",
            text = "Article in Queue",
            icon = R.drawable.ic_notification,
            onGoing = true,
            pendingIntent = pendingIntent
        )
    }
    fun showProgress(notificationId: Int,progress: Int) {
        notificationController.show(
            channelId = NotificationChannels.ARCHIVE_CHANNEL_ID,
            notificationId = notificationId,
            title = "Archiving Article",
            text = progress.toString(),
            icon = R.drawable.ic_notification,
            onGoing = true,
            pendingIntent = pendingIntent,
            progress = progress,
            indeterminate = false
        )
    }
    fun showGenerating(notificationId: Int) {
        notificationController.show(
            channelId = NotificationChannels.ARCHIVE_CHANNEL_ID,
            notificationId = notificationId,
            title = "Archiving Article",
            text = "Article Generating",
            icon = R.drawable.ic_notification,
            onGoing = true,
            pendingIntent = pendingIntent
        )
    }
    fun showMoving(notificationId: Int) {
        notificationController.show(
            channelId = NotificationChannels.ARCHIVE_CHANNEL_ID,
            notificationId = notificationId,
            title = "Archiving Article",
            text = "Moving Article from Local to SAF",
            icon = R.drawable.ic_notification,
            onGoing = true,
            pendingIntent = pendingIntent
        )
    }
    fun showCompleted(notificationId: Int) {
        notificationController.show(
            channelId = NotificationChannels.ARCHIVE_CHANNEL_ID,
            notificationId = notificationId,
            title = "Archiving Article",
            text = "Archiving Completed",
            icon = R.drawable.ic_notification,
            onGoing = false,
            pendingIntent = pendingIntent
        )
    }
    fun showFailed(notificationId: Int,reason: String) {
        notificationController.show(
            channelId = NotificationChannels.ARCHIVE_CHANNEL_ID,
            notificationId = notificationId,
            title = "Archiving Article",
            text = reason,
            icon = R.drawable.ic_notification,
            onGoing = false,
            pendingIntent = pendingIntent
        )
    }

    fun cancel(notificationId:Int){
        notificationController.cancel(notificationId)
    }
}
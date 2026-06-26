package com.arkanzi.udant.core.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(

    @param:ApplicationContext
    private val context: Context

) {

    fun createNotification(
        channelId: String,
        contentTitle: String,
        contentText: String,
        icon: Int,
        setOngoing: Boolean,
        pendingIntent: PendingIntent?,
        progress: Int? = null,
        maxProgress: Int = 100,
        indeterminate: Boolean = false
    ): Notification {

        val builder = NotificationCompat.Builder(
            context,
            channelId
        )
            .setContentTitle(
                contentTitle
            )
            .setContentText(
                contentText
            )
            .setSmallIcon(
                icon
            )
            .setOngoing(
                setOngoing
            )
            .setContentIntent(
                pendingIntent
            )

        if (progress != null || indeterminate) {
            builder.setProgress(
                maxProgress,
                progress ?: 0,
                indeterminate
            )
        }

        return builder.build()
    }
}
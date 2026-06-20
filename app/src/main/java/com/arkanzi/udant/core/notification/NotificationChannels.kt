package com.arkanzi.udant.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager

object NotificationChannels {

    const val ARCHIVE_CHANNEL_ID =
        "archive_channel"

    const val ARCHIVE_PROGRESSION_ID =
        1001

    const val ARCHIVE_STATUS_ID =
        2001

    fun createChannels(
        notificationManager: NotificationManager
    ) {

        val archiveChannel =
            NotificationChannel(
                ARCHIVE_CHANNEL_ID,
                "Article Archive",
                NotificationManager.IMPORTANCE_LOW
            )

        notificationManager
            .createNotificationChannel(
                archiveChannel
            )
    }
}
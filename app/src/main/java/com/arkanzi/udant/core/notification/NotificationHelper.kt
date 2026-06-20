package com.arkanzi.udant.core.notification

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.arkanzi.udant.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(

    @param:ApplicationContext
    private val context: Context

) {

    fun createArchiveNotification(contentText:String,setOngoing: Boolean):
            Notification {

        return NotificationCompat.Builder(
            context,
            NotificationChannels
                .ARCHIVE_CHANNEL_ID
        )
            .setContentTitle(
                "Archiving Article"
            )
            .setContentText(
                contentText
            )
            .setSmallIcon(
                R.drawable.ic_notification
            )
            .setOngoing(
                setOngoing
            )
            .build()
    }
}
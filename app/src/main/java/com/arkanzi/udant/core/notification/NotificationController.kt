package com.arkanzi.udant.core.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationController @Inject constructor(
    @param:ApplicationContext
    private val context: Context,

    private val notificationHelper: NotificationHelper

) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)


    fun createNotification(
        channelId: String,
        title: String,
        text: String,
        icon: Int,
        onGoing: Boolean,
        pendingIntent: PendingIntent?,
        progress: Int? = null,
        maxProgress: Int = 100,
        indeterminate: Boolean = false
    ) =
        notificationHelper
            .createNotification(
                channelId = channelId,
                contentTitle = title,
                contentText = text,
                icon = icon,
                setOngoing = onGoing,
                pendingIntent = pendingIntent,
                progress = progress,
                maxProgress = maxProgress,
                indeterminate = indeterminate
            )

    fun show(
        notificationId: Int,
        channelId: String,
        title: String,
        text: String,
        icon: Int,
        onGoing: Boolean,
        pendingIntent: PendingIntent?,
        progress: Int? = null,
        maxProgress: Int = 100,
        indeterminate: Boolean = false
    ) {
        notificationManager.notify(
            notificationId,
            createNotification(
                channelId = channelId,
                title = title,
                text = text,
                icon = icon,
                onGoing = onGoing,
                pendingIntent = pendingIntent,
                progress = progress,
                maxProgress = maxProgress,
                indeterminate = indeterminate

            )
        )
    }

//    update()
//
    fun cancel(notificationId: Int){
        notificationManager.cancel(notificationId)
    }
//
//    areNotificationsEnabled()

}
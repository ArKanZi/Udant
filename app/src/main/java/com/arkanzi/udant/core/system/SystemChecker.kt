package com.arkanzi.udant.core.system

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemChecker @Inject constructor(

    @param:ApplicationContext
    private val context: Context

) {

    fun hasNotificationPermission(): Boolean {
        return NotificationManagerCompat
            .from(context)
            .areNotificationsEnabled()
    }

    fun hasStoragePermission(): Boolean {
        // TODO
        return true
    }

}
package com.arkanzi.udant

import android.app.Application
import com.arkanzi.udant.core.notification.NotificationChannels
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class UdantApp : Application() {
    override fun onCreate() {
        super.onCreate()

        NotificationChannels
            .createChannels(
                getSystemService(
                    NotificationManager::class.java
                )
            )
    }
}
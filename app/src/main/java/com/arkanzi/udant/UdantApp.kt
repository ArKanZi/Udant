package com.arkanzi.udant

import android.app.Application
import com.arkanzi.udant.core.notification.NotificationChannels
import android.app.NotificationManager
import com.arkanzi.udant.core.job.download.observer.DownloadNotificationObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class UdantApp : Application() {

    @Inject
    lateinit var downloadNotificationObserver: DownloadNotificationObserver
    override fun onCreate() {
        super.onCreate()

        downloadNotificationObserver.start()

        NotificationChannels
            .createChannels(
                getSystemService(
                    NotificationManager::class.java
                )
            )
    }
}
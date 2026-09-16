package com.arkanzi.udant

import android.app.Application
import com.arkanzi.udant.core.notification.NotificationChannels
import android.app.NotificationManager
import com.arkanzi.udant.core.extension.ExtensionManager
import com.arkanzi.udant.core.job.download.observer.DownloadNotificationObserver
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class UdantApp : Application() {

    @Inject
    lateinit var downloadNotificationObserver: DownloadNotificationObserver
    @Inject
    lateinit var extensionManager: ExtensionManager
    override fun onCreate() {
        super.onCreate()

        downloadNotificationObserver.start()
        CoroutineScope(Dispatchers.IO).launch {
            extensionManager.syncExtensions()
        }

        NotificationChannels
            .createChannels(
                getSystemService(
                    NotificationManager::class.java
                )
            )
    }
}
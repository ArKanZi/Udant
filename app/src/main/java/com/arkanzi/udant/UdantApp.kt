package com.arkanzi.udant

import android.app.Application
import com.arkanzi.udant.core.notification.NotificationChannels
import android.app.NotificationManager
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.arkanzi.udant.core.extension.ExtensionManager
import com.arkanzi.udant.core.job.download.observer.DownloadNotificationObserver
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltAndroidApp
class UdantApp : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var downloadNotificationObserver: DownloadNotificationObserver
    @Inject
    lateinit var extensionManager: ExtensionManager

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun newImageLoader(context: Context): ImageLoader {
        return imageLoader
    }

    override fun onCreate() {
        super.onCreate()

        downloadNotificationObserver.start()

        CoroutineScope(Dispatchers.IO).launch {
            extensionManager.start()
        }

        NotificationChannels.createChannels(
            getSystemService(NotificationManager::class.java)
        )
    }
}
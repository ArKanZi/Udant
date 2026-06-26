package com.arkanzi.udant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.arkanzi.udant.core.navigation.AppNavigation
import com.arkanzi.udant.core.ui.theme.UdantTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var notificationDestination by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationDestination =
            intent.getStringExtra("destination")

        Log.d("MainActivity", "onCreate: ${this.hashCode()}")

        enableEdgeToEdge()
        setContent {
            Log.d("Compose", "destination = $notificationDestination")
            UdantTheme {
                AppNavigation(
                    destination = notificationDestination,
                    onDestinationConsumed = {
                        notificationDestination = null
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        notificationDestination =
            intent.getStringExtra("destination")

        Log.d("MainActivity", "onNewIntent: ${this.hashCode()}")
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UdantTheme {

    }
}
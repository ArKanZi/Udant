package com.arkanzi.udant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkanzi.udant.core.navigation.NavHost
import com.arkanzi.udant.core.ui.theme.UdantTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {
            UdantTheme {
                NavHost()
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UdantTheme {

    }
}
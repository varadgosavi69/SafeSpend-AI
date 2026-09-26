package com.safespend.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.safespend.ai.navigation.AppNavHost
import com.safespend.ai.ui.theme.SafeSpendAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            SafeSpendAITheme {
                AppNavHost()
            }
        }
    }
}

package com.safespend.ai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.safespend.ai.ui.screens.DashboardScreen
import com.safespend.ai.ui.screens.OnboardingScreen
import com.safespend.ai.ui.screens.SplashScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onSplashFinished = {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("onboarding") {
            OnboardingScreen(onGetStarted = {
                navController.navigate("dashboard") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("dashboard") {
            DashboardScreen()
        }
    }
}

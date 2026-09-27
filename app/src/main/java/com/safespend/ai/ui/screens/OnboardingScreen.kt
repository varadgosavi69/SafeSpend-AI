package com.safespend.ai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safespend.ai.R

// Replace onGetStarted's destination with wherever your nav graph should
// go next (e.g. navController.navigate("dashboard")).
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {

        // ---------- Full-bleed background image ----------
        // Put your uploaded image at: app/src/main/res/drawable/onboarding_bg.png
        Image(
            painter = painterResource(id = R.drawable.onboarding_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // ---------- Learn More / Get Started button ----------
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .fillMaxWidth(0.7f)
                .clickable { onGetStarted() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Get Started",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = Color.Black)
            }
        }
    }
}
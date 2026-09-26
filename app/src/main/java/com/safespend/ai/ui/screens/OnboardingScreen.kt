package com.safespend.ai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safespend.ai.R
import com.safespend.ai.ui.theme.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*


@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(SplashGreenLight, SplashGreenDark),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1400f)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "NEXT-GEN FINANCE",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                color = TextSecondaryGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Smarter Tools\nfor Safer\nSpending",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDarkGreen,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(Color.White.copy(alpha = 0.6f), androidx.compose.foundation.shape.RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_transparent),
                    contentDescription = "SafeSpend Logo",
                    modifier = Modifier.size(110.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { onGetStarted() }
            ) {

            Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Get Started",
                        color = TextDarkGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = TextDarkGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

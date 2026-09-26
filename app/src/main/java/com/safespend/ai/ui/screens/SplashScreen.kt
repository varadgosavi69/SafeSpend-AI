package com.safespend.ai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safespend.ai.R
import com.safespend.ai.ui.theme.SplashGreenDark
import com.safespend.ai.ui.theme.SplashGreenLight
import com.safespend.ai.ui.theme.TextDarkGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(2000L)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(SplashGreenLight, SplashGreenDark),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1400f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_transparent),
                contentDescription = "SafeSpend Logo",
                modifier = Modifier.size(140.dp)
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                        append("Safe")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append("Spend")
                    }
                },
                color = TextDarkGreen,
                fontSize = 28.sp
            )
        }
    }
}

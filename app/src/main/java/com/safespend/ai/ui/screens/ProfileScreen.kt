package com.safespend.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safespend.ai.ui.theme.*

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text("Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDarkGreen)

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- Gradient header card with avatar ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(GradientGreenStart, GradientGreenEnd),
                            start = Offset(0f, 0f),
                            end = Offset(800f, 800f)
                        )
                    )
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Bobby", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("SafeSpend AI User", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Settings", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDarkGreen)
        Spacer(modifier = Modifier.height(12.dp))

        ProfileMenuItem(icon = Icons.Filled.Shield, label = "Safety Buffer Settings")
        ProfileMenuItem(icon = Icons.Filled.Notifications, label = "Notifications")
        ProfileMenuItem(icon = Icons.Filled.Security, label = "Privacy & Security")
        ProfileMenuItem(icon = Icons.Filled.Settings, label = "App Preferences")

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { /* TODO: navigate */ },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(ChipTintGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = GradientGreenEnd, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDarkGreen)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondaryGray, modifier = Modifier.size(18.dp))
        }
    }
}
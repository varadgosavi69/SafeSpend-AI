package com.safespend.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safespend.ai.ui.theme.*

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDarkGreen)

        Spacer(modifier = Modifier.height(20.dp))

        // USER CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Brush.linearGradient(listOf(GradientGreenStart, GradientGreenEnd)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Demo User", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDarkGreen)
                    Text("Hackathon Demo Account", fontSize = 12.sp, color = TextSecondaryGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("About", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDarkGreen)
        Spacer(modifier = Modifier.height(12.dp))

        InfoRow(icon = Icons.Filled.Shield, title = "SafeSpend.AI", subtitle = "Version 1.0 (Hackathon Build)")
        InfoRow(icon = Icons.Filled.Functions, title = "Formula Engine", subtitle = "14-day liquidity + safe-to-spend calculation")
        InfoRow(icon = Icons.Filled.Info, title = "How it works", subtitle = "Balance + Weighted Income − Committed Expenses − Buffer, divided across 14 days")

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ChipTintGreen)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    "S_safe(t) = Balance + ΣIncome×P(recv) − ΣExpenses − Buffer",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDarkGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Daily Cap = max(0, S_safe(t)) / 14",
                    fontSize = 12.sp,
                    color = TextSecondaryGray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(CardSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = GradientGreenEnd, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDarkGreen)
            Text(subtitle, fontSize = 11.sp, color = TextSecondaryGray)
        }
    }
}

package com.safespend.ai.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safespend.ai.ui.components.formatRupeesShared
import com.safespend.ai.ui.theme.*

// Reuses the same DashboardState defined in DashboardScreen.kt — no new state needed.
@Composable
fun InsightsScreen(state: DashboardState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Your Balance", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDarkGreen)
                Text("Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDarkGreen)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Track spending, buffer, and safe limits", fontSize = 12.sp, color = TextSecondaryGray)
            }
            Icon(Icons.Filled.MoreVert, contentDescription = null, tint = TextDarkGreen)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------- Radial ring showing buffer usage ----------
        val used = state.committedExpenses.coerceAtLeast(0.0)
        val total = (state.weightedIncome).coerceAtLeast(1.0)
        val fraction = (used / total).toFloat().coerceIn(0f, 1f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(220.dp)) {
                val strokeWidth = 18.dp.toPx()
                // background ring
                drawArc(
                    color = ChipTintGreen,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2, strokeWidth / 2)
                )
                // progress ring
                drawArc(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(GradientGreenStart, GradientGreenEnd)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * fraction,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2, strokeWidth / 2)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Balance", fontSize = 12.sp, color = TextSecondaryGray)
                Text(
                    formatRupeesShared(state.currentBalance),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(shape = RoundedCornerShape(50), color = ChipTintGreen) {
                    Text(
                        text = "${(fraction * 100).toInt()}% used",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GradientGreenEnd,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------- Quick icon row ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Income", "Expense", "Buffer", "Trend").forEach { label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(46.dp).background(CardSurface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label.first().toString(), color = GradientGreenEnd, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(label, fontSize = 10.sp, color = TextSecondaryGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------- Trajectory card (dark, with sparkline + income figure) ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(GradientGreenStart, GradientGreenEnd)
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Projected Income", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Text("Trend", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
                Text(
                    formatRupeesShared(state.weightedIncome),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                val points = state.trajectory
                if (points.size > 1) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                        val maxV = points.max()
                        val minV = points.min()
                        val range = (maxV - minV).takeIf { it != 0f } ?: 1f
                        val stepX = size.width / (points.size - 1)
                        val path = Path()
                        points.forEachIndexed { i, v ->
                            val x = i * stepX
                            val y = size.height - ((v - minV) / range) * size.height
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(path, color = Color.White, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
package com.safespend.ai.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safespend.ai.ui.components.formatRupeesShared
import com.safespend.ai.ui.theme.*

@Composable
fun InsightsScreen(state: DashboardState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            "Insights",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDarkGreen
        )
        Text(
            "14-day liquidity projection",
            fontSize = 13.sp,
            color = TextSecondaryGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (state.shortfallRisk) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StatusWarningRed.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "⚠ Shortfall risk detected in your projection window",
                    color = StatusWarningRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Projected Balance",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDarkGreen
                )
                Spacer(modifier = Modifier.height(16.dp))

                val points = state.dailyProjection

                var animationTrigger by remember(points) { mutableStateOf(false) }
                LaunchedEffect(points) {
                    animationTrigger = false
                    animationTrigger = true
                }
                val drawProgress by animateFloatAsState(
                    targetValue = if (animationTrigger) 1f else 0f,
                    animationSpec = tween(durationMillis = 900, easing = LinearEasing),
                    label = "chartDraw"
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    if (points.isEmpty()) return@Canvas
                    val minVal = points.minOrNull() ?: 0.0
                    val maxVal = points.maxOrNull() ?: 1.0
                    val range = (maxVal - minVal).let { if (it == 0.0) 1.0 else it }
                    val stepX = size.width / (points.size - 1).coerceAtLeast(1)

                    val zeroY = size.height - ((0.0 - minVal) / range * size.height).toFloat()
                    if (minVal < 0 && maxVal > 0) {
                        drawLine(
                            color = TextSecondaryGray.copy(alpha = 0.3f),
                            start = androidx.compose.ui.geometry.Offset(0f, zeroY),
                            end = androidx.compose.ui.geometry.Offset(size.width, zeroY),
                            strokeWidth = 2f
                        )
                    }

                    val fullPath = Path()
                    points.forEachIndexed { index, value ->
                        val x = stepX * index
                        val y = size.height - ((value - minVal) / range * size.height).toFloat()
                        if (index == 0) fullPath.moveTo(x, y) else fullPath.lineTo(x, y)
                    }

                    val pathMeasure = PathMeasure()
                    pathMeasure.setPath(fullPath, false)
                    val totalLength = pathMeasure.length
                    val animatedPath = Path()
                    pathMeasure.getSegment(0f, totalLength * drawProgress, animatedPath, true)

                    val lineColor = if (state.shortfallRisk) StatusWarningRed else GradientGreenEnd
                    drawPath(
                        path = animatedPath,
                        color = lineColor,
                        style = Stroke(width = 6f, cap = StrokeCap.Round)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Day 1", fontSize = 11.sp, color = TextSecondaryGray)
                    Text("Day 14", fontSize = 11.sp, color = TextSecondaryGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Breakdown",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextDarkGreen
        )
        Spacer(modifier = Modifier.height(12.dp))

        BreakdownRow("Current Balance", state.currentBalance, StatusSafeGreen)
        BreakdownRow("Expected Income (weighted)", state.weightedIncome, StatusSafeGreen)
        BreakdownRow("Committed Expenses", -state.committedExpenses, StatusWarningRed)
        BreakdownRow("Safety Buffer (reserved)", -state.safetyBuffer, AccentGold)

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ChipTintGreen)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Usable Liquidity Pool", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDarkGreen)
                Text(
                    formatRupeesShared(state.usableLiquidityPool),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun BreakdownRow(label: String, amount: Double, dotColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(dotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, fontSize = 13.sp, color = TextSecondaryGray)
        }
        Text(
            text = (if (amount >= 0) "+" else "-") + formatRupeesShared(kotlin.math.abs(amount)),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (amount >= 0) TextDarkGreen else StatusWarningRed
        )
    }
}
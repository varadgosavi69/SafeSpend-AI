package com.safespend.ai.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

fun formatRupeesShared(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    formatter.maximumFractionDigits = 0
    return "₹${formatter.format(max(0.0, amount))}"
}

@Composable
fun Modifier.pressScale(interactionSource: MutableInteractionSource): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "pressScale"
    )
    return this.graphicsLayer(scaleX = scale, scaleY = scale)
}

@Composable
fun AnimatedRupeeText(
    target: Double,
    suffix: String = "",
    fontSize: androidx.compose.ui.unit.TextUnit = 34.sp,
    color: Color = Color.White
) {
    val animatedValue by animateFloatAsState(
        targetValue = target.toFloat(),
        animationSpec = tween(durationMillis = 600),
        label = "rupeeCounter"
    )
    Text(
        text = "${formatRupeesShared(animatedValue.toDouble())}$suffix",
        color = color,
        fontSize = fontSize,
        fontWeight = FontWeight.ExtraBold
    )
}

package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BkashPink
import com.example.ui.theme.BkashPinkDark
import com.example.ui.theme.CoinGold
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.IncomeGreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpinWheelDialog(
    spinsLeft: Int,
    isSpinning: Boolean,
    spinAngle: Float,
    spinResultCoins: Int?,
    isBn: Boolean,
    onSpinClick: () -> Unit,
    onDismissResult: () -> Unit,
    onClose: () -> Unit
) {
    val animatedAngle by animateFloatAsState(
        targetValue = spinAngle,
        animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
        label = "spin_angle"
    )

    val prizes = listOf(20, 50, 30, 80, 40, 100, 60, 150)
    val sliceColors = listOf(
        BkashPink,
        Color(0xFF3B82F6),
        IncomeGreen,
        CoinGold,
        Color(0xFF8B5CF6),
        Color(0xFFF97316),
        Color(0xFF06B6D4),
        Color(0xFFEC4899)
    )

    Dialog(onDismissRequest = { if (!isSpinning) onClose() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkNavyBg),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("spin_wheel_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "ভাগ্যের চাকা (Lucky Spin)" else "Lucky Spin Wheel",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(
                        onClick = onClose,
                        enabled = !isSpinning,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Text(
                    text = if (isBn) "আজ বাকি আছে: $spinsLeft বার" else "Spins left today: $spinsLeft",
                    color = CoinGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Wheel container with Top Pointer Needle
                Box(
                    modifier = Modifier.size(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating wheel
                    Canvas(
                        modifier = Modifier
                            .size(240.dp)
                            .rotate(animatedAngle)
                    ) {
                        val canvasSize = size.minDimension
                        val radius = canvasSize / 2f
                        val center = Offset(radius, radius)
                        val sliceCount = prizes.size
                        val sweepAngle = 360f / sliceCount

                        // Draw slices
                        for (i in 0 until sliceCount) {
                            val startAngle = i * sweepAngle - 90f // Start pointing up
                            drawArc(
                                color = sliceColors[i],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                topLeft = Offset.Zero,
                                size = Size(canvasSize, canvasSize)
                            )
                        }

                        // Outer rim border
                        drawCircle(
                            color = Color(0xFFFFD700),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 8.dp.toPx())
                        )

                        // Draw prize numbers
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 34f
                            isFakeBoldText = true
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        for (i in 0 until sliceCount) {
                            val midAngleDeg = i * sweepAngle + (sweepAngle / 2f) - 90f
                            val midAngleRad = (midAngleDeg * PI / 180f).toFloat()
                            val textRadius = radius * 0.65f
                            val x = center.x + textRadius * cos(midAngleRad)
                            val y = center.y + textRadius * sin(midAngleRad) + 12f

                            drawContext.canvas.nativeCanvas.drawText(
                                "${prizes[i]}",
                                x,
                                y,
                                paint
                            )
                        }
                    }

                    // Center Hub Button
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Center",
                                tint = BkashPink,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Top Needle indicator
                    Canvas(
                        modifier = Modifier
                            .size(30.dp, 36.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, size.height) // tip pointing down into wheel
                            lineTo(0f, 0f)
                            lineTo(size.width, 0f)
                            close()
                        }
                        drawPath(path = path, color = Color(0xFFFF1744))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Spin Action Button
                Button(
                    onClick = onSpinClick,
                    enabled = !isSpinning && spinsLeft > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_spin_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BkashPink,
                        disabledContainerColor = Color.DarkGray
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Spin",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSpinning) {
                            if (isBn) "চাকা ঘুরছে..." else "Spinning..."
                        } else {
                            if (isBn) "চাকা ঘোরান (Spin Now)" else "Spin Now"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Win Result Dialog Overlay
                if (spinResultCoins != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = IncomeGreen.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isBn) "🎉 অভিনন্দন! আপনি জিতেছেন:" else "🎉 Congratulations! You won:",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+$spinResultCoins কয়েন (৳${spinResultCoins / 100.0})",
                                color = CoinGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onDismissResult,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen)
                            ) {
                                Text(
                                    text = if (isBn) "সংগ্রহ করুন" else "Collect",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

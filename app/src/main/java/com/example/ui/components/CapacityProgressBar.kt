package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate300

@Composable
fun CapacityProgressBar(
    acceptedCount: Int,
    capacity: Int,
    modifier: Modifier = Modifier
) {
    val progress = (acceptedCount.toFloat() / capacity.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
    val spotsRemaining = (capacity - acceptedCount).coerceAtLeast(0)
    val isAlmostFull = spotsRemaining <= 3 && spotsRemaining > 0
    val isFull = spotsRemaining == 0

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Capacity: $acceptedCount / $capacity filled",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = when {
                    isFull -> "Full"
                    isAlmostFull -> "$spotsRemaining spots left!"
                    else -> "$spotsRemaining spots open"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = when {
                    isFull -> Rose500
                    isAlmostFull -> MaterialTheme.colorScheme.tertiary
                    else -> Emerald600
                }
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = when {
                isFull -> Rose500
                isAlmostFull -> MaterialTheme.colorScheme.tertiary
                else -> Emerald600
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

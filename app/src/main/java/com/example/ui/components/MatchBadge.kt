package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchResult
import com.example.ui.theme.*

@Composable
fun MatchBadge(
    matchResult: MatchResult,
    modifier: Modifier = Modifier,
    expandable: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    val (bgColor, textColor) = when {
        matchResult.scorePercentage >= 80 -> Emerald100 to Emerald800
        matchResult.scorePercentage >= 60 -> Teal100 to Teal700
        else -> Amber100 to Amber600
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .background(bgColor, RoundedCornerShape(12.dp))
                .clickable(enabled = expandable) { expanded = !expanded }
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .testTag("match_badge_${matchResult.scorePercentage}"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Smart Match",
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "${matchResult.scorePercentage}% Match",
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            if (expandable && matchResult.matchReasons.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Match details",
                    tint = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        AnimatedVisibility(visible = expanded && matchResult.matchReasons.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Why this matches you:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    matchResult.matchReasons.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "•", color = textColor, fontSize = 11.sp)
                            Text(
                                text = reason,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

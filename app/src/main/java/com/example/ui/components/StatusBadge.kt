package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApplicationStatus
import com.example.ui.theme.*

@Composable
fun StatusBadge(
    status: ApplicationStatus,
    modifier: Modifier = Modifier
) {
    val (bg, fg, icon) = when (status) {
        ApplicationStatus.APPLIED -> Triple(Slate200, Slate700, Icons.Default.Send)
        ApplicationStatus.REVIEWING -> Triple(Amber100, Amber600, Icons.Default.HourglassEmpty)
        ApplicationStatus.ACCEPTED -> Triple(Emerald100, Emerald700, Icons.Default.CheckCircle)
        ApplicationStatus.COMPLETED -> Triple(Teal100, Teal700, Icons.Default.Verified)
        ApplicationStatus.DECLINED -> Triple(Rose100, Rose500, Icons.Default.Cancel)
    }

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = status.displayName(),
            tint = fg,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = status.displayName(),
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

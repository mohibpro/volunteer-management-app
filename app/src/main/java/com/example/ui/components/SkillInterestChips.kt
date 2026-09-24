package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkMode
import com.example.ui.theme.*

@Composable
fun CauseChip(
    cause: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val bg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    val icon: ImageVector = when (cause.lowercase()) {
        "environment" -> Icons.Default.Forest
        "education" -> Icons.Default.School
        "healthcare" -> Icons.Default.Favorite
        "animal welfare" -> Icons.Default.Pets
        "disaster relief" -> Icons.Default.Emergency
        "food security" -> Icons.Default.Restaurant
        "homeless support" -> Icons.Default.Home
        "arts & culture" -> Icons.Default.Palette
        "youth mentorship" -> Icons.Default.Groups
        else -> Icons.Default.VolunteerActivism
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .testTag("cause_chip_${cause.replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = cause,
                color = fg,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun WorkModeChip(
    workMode: WorkMode,
    modifier: Modifier = Modifier
) {
    val (bg, fg, icon) = when (workMode) {
        WorkMode.ONSITE -> Triple(Teal100, Teal700, Icons.Default.LocationOn)
        WorkMode.REMOTE -> Triple(Emerald100, Emerald800, Icons.Default.Videocam)
        WorkMode.HYBRID -> Triple(Amber100, Amber600, Icons.Default.Devices)
    }

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = workMode.displayName(),
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SkillChip(
    skill: String,
    modifier: Modifier = Modifier,
    isMatched: Boolean = false,
    onRemove: (() -> Unit)? = null
) {
    val bg = if (isMatched) Emerald100 else Slate100
    val fg = if (isMatched) Emerald800 else Slate700

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (isMatched) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Matched",
                tint = fg,
                modifier = Modifier.size(12.dp)
            )
        }
        Text(
            text = skill,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        if (onRemove != null) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = fg,
                modifier = Modifier
                    .size(12.dp)
                    .clickable { onRemove() }
            )
        }
    }
}

@Composable
fun PerkChip(
    perk: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CardGiftcard,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = perk,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

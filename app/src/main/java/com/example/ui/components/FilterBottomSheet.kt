package com.example.ui.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommitmentType
import com.example.data.model.FilterCriteria
import com.example.data.model.MatchingEngine
import com.example.data.model.SortOption
import com.example.data.model.WorkMode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    currentFilter: FilterCriteria,
    onApplyFilter: (FilterCriteria) -> Unit,
    onDismiss: () -> Unit,
    onReset: () -> Unit
) {
    var tempCause by remember { mutableStateOf(currentFilter.selectedCause) }
    var tempWorkMode by remember { mutableStateOf(currentFilter.selectedWorkMode) }
    var tempCommitment by remember { mutableStateOf(currentFilter.selectedCommitment) }
    var tempCity by remember { mutableStateOf(currentFilter.selectedCity) }
    var tempDistance by remember { mutableStateOf(currentFilter.maxDistanceKm.toFloat()) }
    var tempSort by remember { mutableStateOf(currentFilter.sortBy) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Opportunities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // City / Region Filter (Pakistani Cities)
            Text(
                text = "City / Region (Pakistan)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = tempCity == null,
                    onClick = { tempCity = null },
                    label = { Text("All Cities") },
                    modifier = Modifier.testTag("filter_city_all")
                )
                MatchingEngine.PAKISTANI_CITIES.forEach { city ->
                    FilterChip(
                        selected = tempCity == city,
                        onClick = { tempCity = if (tempCity == city) null else city },
                        label = { Text(city) },
                        modifier = Modifier.testTag("filter_city_$city")
                    )
                }
            }

            // Work Mode Filter
            Text(
                text = "Work Mode",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = tempWorkMode == null,
                    onClick = { tempWorkMode = null },
                    label = { Text("All") },
                    modifier = Modifier.testTag("filter_mode_all")
                )
                WorkMode.values().forEach { mode ->
                    FilterChip(
                        selected = tempWorkMode == mode,
                        onClick = { tempWorkMode = if (tempWorkMode == mode) null else mode },
                        label = { Text(mode.displayName()) },
                        modifier = Modifier.testTag("filter_mode_${mode.name}")
                    )
                }
            }

            // Causes Filter
            Text(
                text = "Cause / Category",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = tempCause == null,
                    onClick = { tempCause = null },
                    label = { Text("All Causes") }
                )
                MatchingEngine.ALL_CAUSES.forEach { cause ->
                    FilterChip(
                        selected = tempCause == cause,
                        onClick = { tempCause = if (tempCause == cause) null else cause },
                        label = { Text(cause) },
                        modifier = Modifier.testTag("filter_cause_${cause.replace(" ", "_")}")
                    )
                }
            }

            // Commitment Type
            Text(
                text = "Commitment Type",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = tempCommitment == null,
                    onClick = { tempCommitment = null },
                    label = { Text("Any") }
                )
                CommitmentType.values().forEach { comm ->
                    FilterChip(
                        selected = tempCommitment == comm,
                        onClick = { tempCommitment = if (tempCommitment == comm) null else comm },
                        label = { Text(comm.displayName()) },
                        modifier = Modifier.testTag("filter_comm_${comm.name}")
                    )
                }
            }

            // Search Radius Slider
            Text(
                text = "Preferred Distance Radius: ${tempDistance.toInt()} km",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Slider(
                value = tempDistance,
                onValueChange = { tempDistance = it },
                valueRange = 5f..100f,
                steps = 18,
                modifier = Modifier.fillMaxWidth()
            )

            // Sort By
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SortOption.values().forEach { option ->
                    val label = when (option) {
                        SortOption.SMART_MATCH -> "★ Smart Match Score (Highest first)"
                        SortOption.NEAREST -> "Nearby Location"
                        SortOption.SOONEST -> "Upcoming Date"
                        SortOption.HOURS_LOW_TO_HIGH -> "Shortest Commitment (Hours)"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tempSort == option,
                            onClick = { tempSort = option }
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        tempCause = null
                        tempWorkMode = null
                        tempCommitment = null
                        tempCity = null
                        tempDistance = 50f
                        tempSort = SortOption.SMART_MATCH
                        onReset()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("reset_filters_button")
                ) {
                    Text("Reset")
                }

                Button(
                    onClick = {
                        onApplyFilter(
                            currentFilter.copy(
                                selectedCause = tempCause,
                                selectedWorkMode = tempWorkMode,
                                selectedCommitment = tempCommitment,
                                selectedCity = tempCity,
                                maxDistanceKm = tempDistance.toInt(),
                                sortBy = tempSort
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("apply_filters_button")
                ) {
                    Text("Apply Filters")
                }
            }
        }
    }
}

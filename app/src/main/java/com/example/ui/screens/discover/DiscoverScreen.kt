package com.example.ui.screens.discover

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchingEngine
import com.example.ui.components.CauseChip
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.OpportunityCard
import com.example.viewmodel.VolunteerViewModel

@Composable
fun DiscoverScreen(
    volunteerViewModel: VolunteerViewModel,
    onNavigateToOpportunity: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by volunteerViewModel.uiState.collectAsState()
    val filterCriteria by volunteerViewModel.filterCriteria.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Smart Feed, 1: Saved
    var showFilterSheet by remember { mutableStateOf(false) }

    val displayedList = if (activeTab == 0) {
        uiState.opportunities
    } else {
        uiState.savedOpportunities
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search & Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = filterCriteria.query,
                        onValueChange = { volunteerViewModel.setQuery(it) },
                        placeholder = { Text("Search causes, skills, Alkhidmat, Saylani...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        trailingIcon = {
                            if (filterCriteria.query.isNotBlank()) {
                                IconButton(onClick = { volunteerViewModel.setQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_opportunities_input")
                    )

                    // Filter Button with indicator badge if filters active
                    val hasActiveFilters = filterCriteria.selectedCause != null ||
                            filterCriteria.selectedWorkMode != null ||
                            filterCriteria.selectedCommitment != null ||
                            filterCriteria.selectedCity != null

                    BadgedBox(
                        badge = {
                            if (hasActiveFilters) {
                                Badge { Text("!") }
                            }
                        }
                    ) {
                        FilledTonalIconButton(
                            onClick = { showFilterSheet = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .size(52.dp)
                                .testTag("open_filter_button")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Filter")
                        }
                    }
                }

                // Causes Quick Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CauseChip(
                        cause = "All Causes",
                        isSelected = filterCriteria.selectedCause == null,
                        onClick = { volunteerViewModel.selectCause(null) }
                    )
                    MatchingEngine.ALL_CAUSES.forEach { cause ->
                        CauseChip(
                            cause = cause,
                            isSelected = filterCriteria.selectedCause == cause,
                            onClick = { volunteerViewModel.selectCause(cause) }
                        )
                    }
                }

                // Tabs: Smart Feed vs Saved
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Smart Feed (${uiState.opportunities.size})", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_smart_feed")
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Saved (${uiState.savedOpportunities.size})", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_saved")
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (displayedList.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (activeTab == 0) Icons.Default.SearchOff else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = if (activeTab == 0) "No matching opportunities" else "No saved opportunities yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (activeTab == 0)
                            "Try clearing your search query or loosening your filter criteria."
                        else
                            "Bookmark opportunities you're interested in by tapping the bookmark icon.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                    if (activeTab == 0) {
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = { volunteerViewModel.resetFilters() },
                            modifier = Modifier.testTag("empty_reset_filters_button")
                        ) {
                            Text("Reset All Filters")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(displayedList, key = { it.opportunity.id }) { item ->
                        OpportunityCard(
                            item = item,
                            onClick = { onNavigateToOpportunity(item.opportunity.id) },
                            onToggleSave = { volunteerViewModel.toggleSave(item.opportunity.id) }
                        )
                    }
                }
            }
        }

        // Filter bottom sheet
        if (showFilterSheet) {
            FilterBottomSheet(
                currentFilter = filterCriteria,
                onApplyFilter = { newFilter ->
                    volunteerViewModel.selectCause(newFilter.selectedCause)
                    volunteerViewModel.selectWorkMode(newFilter.selectedWorkMode)
                    volunteerViewModel.selectCommitment(newFilter.selectedCommitment)
                    volunteerViewModel.selectCity(newFilter.selectedCity)
                    volunteerViewModel.setMaxDistance(newFilter.maxDistanceKm)
                    volunteerViewModel.setSortBy(newFilter.sortBy)
                },
                onDismiss = { showFilterSheet = false },
                onReset = { volunteerViewModel.resetFilters() }
            )
        }
    }
}

package com.example.ui.screens.organizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommitmentType
import com.example.data.model.MatchingEngine
import com.example.data.model.WorkMode
import com.example.ui.components.CauseChip
import com.example.viewmodel.OrganizerViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateOpportunityScreen(
    organizerViewModel: OrganizerViewModel,
    onBack: () -> Unit
) {
    val uiState by organizerViewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCause by remember { mutableStateOf(MatchingEngine.ALL_CAUSES.first()) }
    var selectedWorkMode by remember { mutableStateOf(WorkMode.ONSITE) }
    var locationName by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var timeSlot by remember { mutableStateOf("9:00 AM - 1:00 PM") }
    var commitmentType by remember { mutableStateOf(CommitmentType.ONE_TIME) }
    var dailyHours by remember { mutableStateOf("4.0") }
    var capacity by remember { mutableStateOf("15") }
    var contactPerson by remember { mutableStateOf("") }
    var selectedSkills by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedPerks by remember { mutableStateOf(listOf("Verified Certificate", "Chai & Refreshments")) }

    LaunchedEffect(uiState.creationSuccess) {
        if (uiState.creationSuccess) {
            organizerViewModel.clearMessages()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post New Opportunity", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            val hoursVal = dailyHours.toDoubleOrNull() ?: 4.0
                            val capVal = capacity.toIntOrNull() ?: 10
                            organizerViewModel.createOpportunity(
                                title = title,
                                description = description,
                                cause = selectedCause,
                                workMode = selectedWorkMode,
                                locationName = locationName,
                                startDate = startDate,
                                timeSlot = timeSlot,
                                commitmentType = commitmentType,
                                dailyHours = hoursVal,
                                totalHours = hoursVal,
                                capacity = capVal,
                                requiredSkills = selectedSkills,
                                perks = selectedPerks,
                                contactPerson = contactPerson
                            )
                        },
                        enabled = title.isNotBlank() && description.isNotBlank() && locationName.isNotBlank() && !uiState.isCreatingOpportunity,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("publish_opportunity_button")
                    ) {
                        if (uiState.isCreatingOpportunity) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Publish Opportunity", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // General Info
            Text("Role & Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Opportunity Title *") },
                placeholder = { Text("e.g. Tree Plantation Drive or Ration Distribution") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("opp_title_input")
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Task Description & Responsibilities *") },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("opp_desc_input")
            )

            // Cause Category
            Text("Cause / Focus Area", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MatchingEngine.ALL_CAUSES.forEach { cause ->
                    CauseChip(
                        cause = cause,
                        isSelected = selectedCause == cause,
                        onClick = { selectedCause = cause }
                    )
                }
            }

            // Work Mode
            Text("Work Mode", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WorkMode.values().forEach { mode ->
                    FilterChip(
                        selected = selectedWorkMode == mode,
                        onClick = { selectedWorkMode = mode },
                        label = { Text(mode.displayName()) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Location
            OutlinedTextField(
                value = locationName,
                onValueChange = { locationName = it },
                label = { Text("Location Name / Virtual Link *") },
                placeholder = { Text("e.g. Saylani Dastarkhwan, Karachi or Online") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("opp_location_input")
            )

            // Pakistani Cities Quick Chips
            Text("Select Major City", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MatchingEngine.PAKISTANI_CITIES.forEach { city ->
                    FilterChip(
                        selected = locationName.contains(city, ignoreCase = true),
                        onClick = {
                            if (!locationName.contains(city, ignoreCase = true)) {
                                locationName = if (locationName.isBlank()) city else "$locationName, $city"
                            }
                        },
                        label = { Text(city, fontSize = 11.sp) }
                    )
                }
            }

            // Date & Commitment
            Text("Schedule & Logistics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Date(s)") },
                    placeholder = { Text("e.g. Sat, Nov 14") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = timeSlot,
                    onValueChange = { timeSlot = it },
                    label = { Text("Shift Time") },
                    placeholder = { Text("e.g. 9am - 1pm") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = dailyHours,
                    onValueChange = { dailyHours = it },
                    label = { Text("Daily Hours") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Volunteer Capacity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // Required Skills
            Text("Skills Needed", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MatchingEngine.ALL_SKILLS.forEach { skill ->
                    val isSelected = selectedSkills.contains(skill)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedSkills = if (isSelected) selectedSkills - skill else selectedSkills + skill
                        },
                        label = { Text(skill, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        } else null
                    )
                }
            }

            // Volunteer Perks
            Text("Volunteer Perks & Recognition", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MatchingEngine.ALL_PERKS.forEach { perk ->
                    val isSelected = selectedPerks.contains(perk)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedPerks = if (isSelected) selectedPerks - perk else selectedPerks + perk
                        },
                        label = { Text(perk, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        } else null
                    )
                }
            }

            OutlinedTextField(
                value = contactPerson,
                onValueChange = { contactPerson = it },
                label = { Text("Point of Contact (Name & Phone / Email)") },
                placeholder = { Text("e.g. Dr. Tariq Mansoor (+92 321 4455667)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

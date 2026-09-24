package com.example.ui.screens.organizer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.ApplicationStatus
import com.example.data.model.OpportunityEntity
import com.example.data.model.UserRole
import com.example.ui.components.CapacityProgressBar
import com.example.ui.components.CauseChip
import com.example.ui.components.StatusBadge
import com.example.ui.components.WorkModeChip
import com.example.ui.theme.Emerald600
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.OrganizerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerDashboardScreen(
    organizerViewModel: OrganizerViewModel,
    authViewModel: AuthViewModel,
    onCreateOpportunity: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by organizerViewModel.uiState.collectAsState()
    val org = uiState.organization
    val opportunities = uiState.myOpportunities

    var showApplicantSheet by remember { mutableStateOf(false) }
    var selectedOppForApplicants by remember { mutableStateOf<OpportunityEntity?>(null) }
    var showEditOrgDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            organizerViewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = org?.name ?: "Organizer Portal",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (org?.isVerified == true) {
                                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Emerald600, modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(
                            text = org?.tagline ?: "Organization Admin Dashboard",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { showEditOrgDialog = true },
                        modifier = Modifier.testTag("edit_org_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Organization")
                    }
                }

                // Stats Dashboard
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${opportunities.size}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Listings", style = MaterialTheme.typography.labelSmall)
                        }

                        VerticalDivider(modifier = Modifier.height(32.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val totalAccepted = opportunities.sumOf { it.acceptedCount }
                            Text(
                                text = "$totalAccepted",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Volunteers Active", style = MaterialTheme.typography.labelSmall)
                        }

                        VerticalDivider(modifier = Modifier.height(32.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val totalSpots = opportunities.sumOf { it.capacity }
                            Text(
                                text = "$totalSpots",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Total Capacity", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                // Role Switcher / Quick Action Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Listings (${opportunities.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(
                        onClick = { authViewModel.quickSwitchRole(UserRole.VOLUNTEER) },
                        modifier = Modifier.testTag("switch_to_volunteer_view")
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Switch to Volunteer")
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateOpportunity,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Post Opportunity") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("create_opportunity_fab")
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        if (opportunities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PostAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No opportunities posted yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tap 'Post Opportunity' to publish a new call for volunteers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(opportunities, key = { it.id }) { opp ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedOppForApplicants = opp
                                organizerViewModel.selectOpportunity(opp)
                                showApplicantSheet = true
                            }
                            .testTag("organizer_opp_${opp.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    CauseChip(cause = opp.cause)
                                    WorkModeChip(workMode = opp.workMode)
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                        Text(
                                            text = "Manage Applicants",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Text(
                                text = opp.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "${opp.startDate} • ${opp.timeSlot}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            CapacityProgressBar(
                                acceptedCount = opp.acceptedCount,
                                capacity = opp.capacity
                            )
                        }
                    }
                }
            }
        }

        // Applicant Review Bottom Sheet
        if (showApplicantSheet && selectedOppForApplicants != null) {
            val opp = selectedOppForApplicants!!
            val applicants = uiState.applicationsForSelectedOpp

            ModalBottomSheet(
                onDismissRequest = { showApplicantSheet = false },
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Applicants for Role",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = opp.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { showApplicantSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    if (applicants.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No applications received yet for this opportunity.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(applicants, key = { it.application.id }) { appItem ->
                                val volunteer = appItem.volunteerUser
                                val app = appItem.application

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = volunteer?.fullName ?: "Volunteer Applicant",
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Text(
                                                    text = volunteer?.email ?: "",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            StatusBadge(status = app.status)
                                        }

                                        if (app.applicationNote.isNotBlank()) {
                                            Text(
                                                text = "\"${app.applicationNote}\"",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                            )
                                        }

                                        if (volunteer?.skills?.isNotEmpty() == true) {
                                            Text(
                                                text = "Skills: ${volunteer.skills.joinToString()}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        // Status action buttons
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            if (app.status != ApplicationStatus.ACCEPTED) {
                                                Button(
                                                    onClick = {
                                                        organizerViewModel.updateApplicationStatus(
                                                            app.id,
                                                            ApplicationStatus.ACCEPTED,
                                                            "Welcome to the volunteer team!"
                                                        )
                                                    },
                                                    modifier = Modifier.weight(1f).height(36.dp)
                                                ) {
                                                    Text("Accept", fontSize = 11.sp)
                                                }
                                            }

                                            if (app.status == ApplicationStatus.ACCEPTED) {
                                                Button(
                                                    onClick = {
                                                        organizerViewModel.updateApplicationStatus(
                                                            app.id,
                                                            ApplicationStatus.COMPLETED,
                                                            "Thank you for your valuable contribution!",
                                                            hours = opp.dailyHours
                                                        )
                                                    },
                                                    modifier = Modifier.weight(1f).height(36.dp)
                                                ) {
                                                    Text("Log Hours (${opp.dailyHours}h)", fontSize = 11.sp)
                                                }
                                            }

                                            if (app.status != ApplicationStatus.DECLINED && app.status != ApplicationStatus.COMPLETED) {
                                                OutlinedButton(
                                                    onClick = {
                                                        organizerViewModel.updateApplicationStatus(
                                                            app.id,
                                                            ApplicationStatus.DECLINED,
                                                            "Thank you for your interest. Positions have been filled."
                                                        )
                                                    },
                                                    modifier = Modifier.weight(1f).height(36.dp)
                                                ) {
                                                    Text("Decline", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Edit Organization Dialog
        if (showEditOrgDialog && org != null) {
            var orgName by remember { mutableStateOf(org.name) }
            var orgTagline by remember { mutableStateOf(org.tagline) }
            var orgDesc by remember { mutableStateOf(org.description) }
            var orgWebsite by remember { mutableStateOf(org.website) }
            var orgEmail by remember { mutableStateOf(org.email) }
            var orgPhone by remember { mutableStateOf(org.phone) }
            var orgCity by remember { mutableStateOf(org.city) }

            AlertDialog(
                onDismissRequest = { showEditOrgDialog = false },
                title = { Text("Edit Organization Profile") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(value = orgName, onValueChange = { orgName = it }, label = { Text("Organization Name") }, singleLine = true)
                        OutlinedTextField(value = orgTagline, onValueChange = { orgTagline = it }, label = { Text("Tagline / Mission") }, singleLine = true)
                        OutlinedTextField(value = orgDesc, onValueChange = { orgDesc = it }, label = { Text("About") }, minLines = 2)
                        OutlinedTextField(value = orgWebsite, onValueChange = { orgWebsite = it }, label = { Text("Website") }, singleLine = true)
                        OutlinedTextField(value = orgPhone, onValueChange = { orgPhone = it }, label = { Text("Phone (+92)") }, placeholder = { Text("+92 21 111 000 000") }, singleLine = true)
                        OutlinedTextField(value = orgEmail, onValueChange = { orgEmail = it }, label = { Text("Contact Email") }, singleLine = true)
                        OutlinedTextField(value = orgCity, onValueChange = { orgCity = it }, label = { Text("City (Pakistan)") }, singleLine = true)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                        ) {
                            com.example.data.model.MatchingEngine.PAKISTANI_CITIES.forEach { pCity ->
                                FilterChip(
                                    selected = orgCity.equals(pCity, ignoreCase = true),
                                    onClick = { orgCity = pCity },
                                    label = { Text(pCity, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            organizerViewModel.updateOrganizationDetails(
                                name = orgName,
                                tagline = orgTagline,
                                description = orgDesc,
                                website = orgWebsite,
                                phone = orgPhone,
                                email = orgEmail,
                                city = orgCity
                            )
                            showEditOrgDialog = false
                        }
                    ) {
                        Text("Save Changes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditOrgDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

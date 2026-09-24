package com.example.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.model.UserRole
import com.example.ui.screens.applications.MyApplicationsScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.opportunity.OpportunityDetailScreen
import com.example.ui.screens.organizer.CreateOpportunityScreen
import com.example.ui.screens.organizer.OrganizerDashboardScreen
import com.example.ui.screens.profile.VolunteerProfileScreen
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.OrganizerViewModel
import com.example.viewmodel.VolunteerViewModel

@Composable
fun ServeSyncApp(
    authViewModel: AuthViewModel,
    volunteerViewModel: VolunteerViewModel,
    organizerViewModel: OrganizerViewModel
) {
    val navController = rememberNavController()
    val authUiState by authViewModel.uiState.collectAsState()
    val currentUser = authUiState.currentUser

    // Determine initial destination: if user is logged in go to Main, otherwise Auth
    val startDestination = Screen.Main.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                authViewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            if (currentUser?.role == UserRole.ORGANIZER) {
                // Organizer Portal View
                OrganizerDashboardScreen(
                    organizerViewModel = organizerViewModel,
                    authViewModel = authViewModel,
                    onCreateOpportunity = {
                        navController.navigate(Screen.CreateOpportunity.route)
                    }
                )
            } else {
                // Volunteer Flow with Bottom Bar Navigation
                var selectedTab by remember { mutableStateOf(VolunteerTab.DISCOVER) }

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.navigationBars),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == VolunteerTab.DISCOVER,
                                onClick = { selectedTab = VolunteerTab.DISCOVER },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == VolunteerTab.DISCOVER) Icons.Default.Explore else Icons.Outlined.Explore,
                                        contentDescription = "Discover"
                                    )
                                },
                                label = { Text(VolunteerTab.DISCOVER.title) },
                                modifier = Modifier.testTag("nav_item_discover")
                            )

                            NavigationBarItem(
                                selected = selectedTab == VolunteerTab.APPLICATIONS,
                                onClick = { selectedTab = VolunteerTab.APPLICATIONS },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == VolunteerTab.APPLICATIONS) Icons.Default.VolunteerActivism else Icons.Outlined.VolunteerActivism,
                                        contentDescription = "My Journey"
                                    )
                                },
                                label = { Text(VolunteerTab.APPLICATIONS.title) },
                                modifier = Modifier.testTag("nav_item_applications")
                            )

                            NavigationBarItem(
                                selected = selectedTab == VolunteerTab.PROFILE,
                                onClick = { selectedTab = VolunteerTab.PROFILE },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == VolunteerTab.PROFILE) Icons.Default.Person else Icons.Outlined.Person,
                                        contentDescription = "Profile"
                                    )
                                },
                                label = { Text(VolunteerTab.PROFILE.title) },
                                modifier = Modifier.testTag("nav_item_profile")
                            )
                        }
                    }
                ) { innerPadding ->
                    when (selectedTab) {
                        VolunteerTab.DISCOVER -> {
                            DiscoverScreen(
                                volunteerViewModel = volunteerViewModel,
                                onNavigateToOpportunity = { oppId ->
                                    navController.navigate(Screen.OpportunityDetail.createRoute(oppId))
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        VolunteerTab.APPLICATIONS -> {
                            MyApplicationsScreen(
                                volunteerViewModel = volunteerViewModel,
                                onNavigateToOpportunity = { oppId ->
                                    navController.navigate(Screen.OpportunityDetail.createRoute(oppId))
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        VolunteerTab.PROFILE -> {
                            VolunteerProfileScreen(
                                volunteerViewModel = volunteerViewModel,
                                authViewModel = authViewModel,
                                onLogout = {
                                    authViewModel.clearMessages()
                                    navController.navigate(Screen.Auth.route) {
                                        popUpTo(Screen.Main.route) { inclusive = true }
                                    }
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }

        composable(
            route = Screen.OpportunityDetail.route,
            arguments = listOf(navArgument("oppId") { type = NavType.LongType })
        ) { backStackEntry ->
            val oppId = backStackEntry.arguments?.getLong("oppId") ?: 1L
            OpportunityDetailScreen(
                opportunityId = oppId,
                volunteerViewModel = volunteerViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateOpportunity.route) {
            CreateOpportunityScreen(
                organizerViewModel = organizerViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

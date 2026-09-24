package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Main : Screen("main")
    object OpportunityDetail : Screen("opportunity_detail/{oppId}") {
        fun createRoute(oppId: Long) = "opportunity_detail/$oppId"
    }
    object CreateOpportunity : Screen("create_opportunity")
}

enum class VolunteerTab(val title: String) {
    DISCOVER("Discover"),
    APPLICATIONS("My Journey"),
    PROFILE("Profile")
}

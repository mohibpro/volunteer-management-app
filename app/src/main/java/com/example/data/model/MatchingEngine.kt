package com.example.data.model

data class FilterCriteria(
    val query: String = "",
    val selectedCause: String? = null,
    val selectedWorkMode: WorkMode? = null,
    val selectedCommitment: CommitmentType? = null,
    val selectedCity: String? = null,
    val maxDistanceKm: Int = 50,
    val onlyOpen: Boolean = true,
    val sortBy: SortOption = SortOption.SMART_MATCH
)

enum class SortOption {
    SMART_MATCH,
    NEAREST,
    SOONEST,
    HOURS_LOW_TO_HIGH
}

data class MatchResult(
    val scorePercentage: Int,
    val matchReasons: List<String>,
    val matchingSkills: List<String>,
    val causeMatched: Boolean,
    val workModeMatched: Boolean,
    val locationMatched: Boolean
)

object MatchingEngine {

    fun calculateMatch(volunteer: UserEntity, opportunity: OpportunityEntity, distanceKm: Double = 10.0): MatchResult {
        var totalPoints = 0.0
        val maxPoints = 100.0
        val reasons = mutableListOf<String>()
        val matchedSkills = mutableListOf<String>()

        // 1. Cause Alignment (up to 35 pts)
        val causeMatch = volunteer.causes.any { it.equals(opportunity.cause, ignoreCase = true) }
        if (causeMatch) {
            totalPoints += 35.0
            reasons.add("Cause matches your interest in ${opportunity.cause}")
        } else if (volunteer.causes.isEmpty()) {
            totalPoints += 20.0
        }

        // 2. Skills Match (up to 35 pts)
        if (opportunity.requiredSkills.isEmpty()) {
            totalPoints += 30.0
            reasons.add("No specialized prerequisites required")
        } else {
            val userSkillLower = volunteer.skills.map { it.lowercase().trim() }
            val common = opportunity.requiredSkills.filter { req ->
                userSkillLower.contains(req.lowercase().trim())
            }
            matchedSkills.addAll(common)
            val skillRatio = common.size.toDouble() / opportunity.requiredSkills.size.toDouble()
            val skillScore = skillRatio * 35.0
            totalPoints += skillScore
            if (common.isNotEmpty()) {
                reasons.add("Matches ${common.size} of your skills (${common.take(2).joinToString()})")
            }
        }

        // 3. Work Mode Preference (up to 15 pts)
        val modeMatch = when {
            opportunity.workMode == WorkMode.REMOTE -> true
            volunteer.workModePreference == WorkMode.HYBRID -> true
            volunteer.workModePreference == opportunity.workMode -> true
            else -> false
        }
        if (modeMatch) {
            totalPoints += 15.0
            reasons.add("Matches your ${opportunity.workMode.displayName()} preference")
        } else {
            totalPoints += 5.0
        }

        // 4. Proximity / Location (up to 15 pts)
        val isNearby = opportunity.workMode == WorkMode.REMOTE || distanceKm <= volunteer.searchRadiusKm
        if (isNearby) {
            totalPoints += 15.0
            if (opportunity.workMode != WorkMode.REMOTE) {
                reasons.add("Within your ${volunteer.searchRadiusKm} km radius")
            }
        } else {
            val ratio = (volunteer.searchRadiusKm / (distanceKm + 0.1)).coerceIn(0.1, 1.0)
            totalPoints += ratio * 10.0
        }

        val finalScore = totalPoints.toInt().coerceIn(25, 99)
        return MatchResult(
            scorePercentage = finalScore,
            matchReasons = reasons,
            matchingSkills = matchedSkills,
            causeMatched = causeMatch,
            workModeMatched = modeMatch,
            locationMatched = isNearby
        )
    }

    val ALL_CAUSES = listOf(
        "Environment",
        "Education",
        "Healthcare",
        "Animal Welfare",
        "Disaster Relief",
        "Food Security",
        "Homeless Support",
        "Arts & Culture",
        "Senior Care",
        "Youth Mentorship"
    )

    val ALL_SKILLS = listOf(
        "Teaching",
        "First Aid & CPR",
        "Event Management",
        "Web Development",
        "Food Preparation",
        "Translation",
        "Logistics & Driving",
        "Photography",
        "Social Media",
        "Counseling",
        "Gardening",
        "Fundraising"
    )

    val PAKISTANI_CITIES = listOf(
        "Karachi",
        "Lahore",
        "Islamabad",
        "Rawalpindi",
        "Peshawar",
        "Multan",
        "Quetta"
    )

    val ALL_PERKS = listOf(
        "Verified Certificate",
        "Chai & Refreshments",
        "Travel Allowance (PKR 1,500/day)",
        "ServeSync Volunteer Badge",
        "Letter of Recommendation",
        "Free Event T-shirt"
    )
}

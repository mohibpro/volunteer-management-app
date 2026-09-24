package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    VOLUNTEER,
    ORGANIZER
}

enum class WorkMode {
    ONSITE,
    REMOTE,
    HYBRID;

    fun displayName(): String = when (this) {
        ONSITE -> "Onsite"
        REMOTE -> "Remote / Virtual"
        HYBRID -> "Hybrid"
    }
}

enum class CommitmentType {
    ONE_TIME,
    RECURRING,
    FLEXIBLE;

    fun displayName(): String = when (this) {
        ONE_TIME -> "One-time"
        RECURRING -> "Recurring"
        FLEXIBLE -> "Flexible"
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val passwordHash: String = "password123",
    val role: UserRole = UserRole.VOLUNTEER,
    val fullName: String,
    val phone: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val city: String = "San Francisco, CA",
    val latitude: Double = 37.7749,
    val longitude: Double = -122.4194,
    val searchRadiusKm: Int = 25,
    val workModePreference: WorkMode = WorkMode.HYBRID,
    val hoursPerWeek: Int = 6,
    val availableDays: List<String> = listOf("Saturday", "Sunday"), // e.g. "Monday", "Saturday", "Weekends"
    val skills: List<String> = emptyList(),
    val causes: List<String> = emptyList(),
    val completedHours: Double = 0.0,
    val badges: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

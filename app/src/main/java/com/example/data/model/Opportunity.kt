package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "opportunities")
data class OpportunityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orgId: Long,
    val title: String,
    val description: String,
    val cause: String,
    val workMode: WorkMode = WorkMode.ONSITE,
    val locationName: String,
    val latitude: Double = 37.7749,
    val longitude: Double = -122.4194,
    val startDate: String,
    val endDate: String = "",
    val timeSlot: String = "9:00 AM - 1:00 PM",
    val commitmentType: CommitmentType = CommitmentType.ONE_TIME,
    val dailyHours: Double = 4.0,
    val totalEstimatedHours: Double = 4.0,
    val requiredSkills: List<String> = emptyList(),
    val perks: List<String> = emptyList(),
    val capacity: Int = 10,
    val acceptedCount: Int = 0,
    val isOpen: Boolean = true,
    val imageUrl: String = "",
    val contactPerson: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

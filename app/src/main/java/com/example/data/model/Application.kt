package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ApplicationStatus {
    APPLIED,
    REVIEWING,
    ACCEPTED,
    COMPLETED,
    DECLINED;

    fun displayName(): String = when (this) {
        APPLIED -> "Applied"
        REVIEWING -> "In Review"
        ACCEPTED -> "Accepted"
        COMPLETED -> "Completed"
        DECLINED -> "Declined"
    }
}

@Entity(tableName = "applications")
data class ApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val opportunityId: Long,
    val volunteerUserId: Long,
    val status: ApplicationStatus = ApplicationStatus.APPLIED,
    val applicationNote: String = "",
    val appliedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val hoursLogged: Double = 0.0,
    val organizerFeedback: String = ""
)

@Entity(tableName = "saved_opportunities", primaryKeys = ["volunteerUserId", "opportunityId"])
data class SavedOpportunityEntity(
    val volunteerUserId: Long,
    val opportunityId: Long,
    val savedAt: Long = System.currentTimeMillis()
)

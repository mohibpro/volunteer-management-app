package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "organizations")
data class OrganizationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val adminUserId: Long = 0,
    val name: String,
    val tagline: String = "",
    val description: String,
    val logoUrl: String = "",
    val website: String = "",
    val email: String,
    val phone: String = "",
    val city: String = "San Francisco, CA",
    val isVerified: Boolean = true,
    val causes: List<String> = emptyList(),
    val volunteerCount: Int = 0,
    val rating: Double = 4.9,
    val createdAt: Long = System.currentTimeMillis()
)

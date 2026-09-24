package com.example.data.local

import androidx.room.*
import com.example.data.model.OrganizationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizationDao {
    @Query("SELECT * FROM organizations WHERE id = :id")
    fun getOrganizationById(id: Long): Flow<OrganizationEntity?>

    @Query("SELECT * FROM organizations WHERE adminUserId = :adminUserId LIMIT 1")
    fun getOrganizationByAdminId(adminUserId: Long): Flow<OrganizationEntity?>

    @Query("SELECT * FROM organizations")
    fun getAllOrganizations(): Flow<List<OrganizationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganization(org: OrganizationEntity): Long

    @Update
    suspend fun updateOrganization(org: OrganizationEntity)
}

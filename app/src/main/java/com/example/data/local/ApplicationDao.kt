package com.example.data.local

import androidx.room.*
import com.example.data.model.ApplicationEntity
import com.example.data.model.ApplicationStatus
import com.example.data.model.SavedOpportunityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications WHERE volunteerUserId = :userId ORDER BY appliedAt DESC")
    fun getApplicationsByVolunteer(userId: Long): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE opportunityId = :opportunityId ORDER BY appliedAt DESC")
    fun getApplicationsByOpportunity(opportunityId: Long): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE opportunityId = :opportunityId AND volunteerUserId = :userId LIMIT 1")
    fun getApplication(opportunityId: Long, userId: Long): Flow<ApplicationEntity?>

    @Query("SELECT * FROM applications WHERE id = :id")
    fun getApplicationById(id: Long): Flow<ApplicationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: ApplicationEntity): Long

    @Update
    suspend fun updateApplication(application: ApplicationEntity)

    @Query("UPDATE applications SET status = :status, updatedAt = :updatedAt, hoursLogged = :hours WHERE id = :id")
    suspend fun updateStatus(id: Long, status: ApplicationStatus, updatedAt: Long, hours: Double)

    @Query("DELETE FROM applications WHERE id = :id")
    suspend fun deleteApplication(id: Long)
}

@Dao
interface SavedOpportunityDao {
    @Query("SELECT opportunityId FROM saved_opportunities WHERE volunteerUserId = :userId")
    fun getSavedOpportunityIds(userId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOpportunity(saved: SavedOpportunityEntity)

    @Query("DELETE FROM saved_opportunities WHERE volunteerUserId = :userId AND opportunityId = :opportunityId")
    suspend fun removeSavedOpportunity(userId: Long, opportunityId: Long)
}

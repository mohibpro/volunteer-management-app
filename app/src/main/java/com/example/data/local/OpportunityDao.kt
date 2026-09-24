package com.example.data.local

import androidx.room.*
import com.example.data.model.OpportunityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OpportunityDao {
    @Query("SELECT * FROM opportunities ORDER BY createdAt DESC")
    fun getAllOpportunities(): Flow<List<OpportunityEntity>>

    @Query("SELECT * FROM opportunities WHERE id = :id")
    fun getOpportunityById(id: Long): Flow<OpportunityEntity?>

    @Query("SELECT * FROM opportunities WHERE orgId = :orgId ORDER BY createdAt DESC")
    fun getOpportunitiesByOrg(orgId: Long): Flow<List<OpportunityEntity>>

    @Query("""
        SELECT * FROM opportunities 
        WHERE (:cause IS NULL OR cause = :cause)
        AND (:workMode IS NULL OR workMode = :workMode)
        AND (
            title LIKE '%' || :query || '%' 
            OR description LIKE '%' || :query || '%' 
            OR locationName LIKE '%' || :query || '%'
            OR cause LIKE '%' || :query || '%'
        )
        ORDER BY createdAt DESC
    """)
    fun filterOpportunities(
        query: String,
        cause: String?,
        workMode: String?
    ): Flow<List<OpportunityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpportunity(opportunity: OpportunityEntity): Long

    @Update
    suspend fun updateOpportunity(opportunity: OpportunityEntity)

    @Delete
    suspend fun deleteOpportunity(opportunity: OpportunityEntity)

    @Query("UPDATE opportunities SET acceptedCount = acceptedCount + 1 WHERE id = :id AND acceptedCount < capacity")
    suspend fun incrementAcceptedCount(id: Long)

    @Query("UPDATE opportunities SET acceptedCount = CASE WHEN acceptedCount > 0 THEN acceptedCount - 1 ELSE 0 END WHERE id = :id")
    suspend fun decrementAcceptedCount(id: Long)
}

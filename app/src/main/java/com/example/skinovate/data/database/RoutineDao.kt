package com.example.skinovate.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk Routine operations
 */
@Dao
interface RoutineDao {
    
    /**
     * Get routine by composite ID
     */
    @Query("SELECT * FROM routines WHERE compositeId = :compositeId")
    suspend fun getRoutineByCompositeId(compositeId: String): RoutineEntity?
    
    /**
     * Get routine by ID (morning or evening) for a user
     */
    @Query("SELECT * FROM routines WHERE id = :routineId AND userId = :userId")
    suspend fun getRoutineById(routineId: String, userId: String): RoutineEntity?
    
    /**
     * Get all routines for a user
     */
    @Query("SELECT * FROM routines WHERE userId = :userId ORDER BY id ASC")
    fun getAllRoutines(userId: String): Flow<List<RoutineEntity>>
    
    /**
     * Insert or update routine
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRoutine(routine: RoutineEntity)
    
    /**
     * Update routine
     */
    @Update
    suspend fun updateRoutine(routine: RoutineEntity)
    
    /**
     * Delete routine (cascade will delete steps)
     */
    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)
    
    /**
     * Delete all routines for a user
     */
    @Query("DELETE FROM routines WHERE userId = :userId")
    suspend fun deleteAllRoutines(userId: String)
}


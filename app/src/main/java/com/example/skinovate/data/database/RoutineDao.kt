package com.example.skinovate.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk Routine operations
 */
@Dao
interface RoutineDao {
    
    /**
     * Get routine by ID (morning or evening)
     */
    @Query("SELECT * FROM routines WHERE id = :routineId")
    suspend fun getRoutineById(routineId: String): RoutineEntity?
    
    /**
     * Get all routines
     */
    @Query("SELECT * FROM routines ORDER BY id ASC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>
    
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
}


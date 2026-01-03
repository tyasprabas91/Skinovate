package com.example.skinovate.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk RoutineStep operations
 */
@Dao
interface RoutineStepDao {
    
    /**
     * Get all steps for a routine
     */
    @Query("SELECT * FROM routine_steps WHERE routineId = :routineId ORDER BY time ASC")
    fun getStepsByRoutineId(routineId: String): Flow<List<RoutineStepEntity>>
    
    /**
     * Get step by ID
     */
    @Query("SELECT * FROM routine_steps WHERE id = :stepId")
    suspend fun getStepById(stepId: String): RoutineStepEntity?
    
    /**
     * Insert step
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: RoutineStepEntity)
    
    /**
     * Insert multiple steps
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<RoutineStepEntity>)
    
    /**
     * Update step
     */
    @Update
    suspend fun updateStep(step: RoutineStepEntity)
    
    /**
     * Delete step
     */
    @Delete
    suspend fun deleteStep(step: RoutineStepEntity)
    
    /**
     * Delete step by ID
     */
    @Query("DELETE FROM routine_steps WHERE id = :stepId")
    suspend fun deleteStepById(stepId: String)
    
    /**
     * Delete all steps for a routine
     */
    @Query("DELETE FROM routine_steps WHERE routineId = :routineId")
    suspend fun deleteStepsByRoutineId(routineId: String)
    
    /**
     * Delete all steps
     */
    @Query("DELETE FROM routine_steps")
    suspend fun deleteAllSteps()
}


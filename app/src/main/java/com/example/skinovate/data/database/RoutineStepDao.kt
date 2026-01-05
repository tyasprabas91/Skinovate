package com.example.skinovate.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk RoutineStep operations
 */
@Dao
interface RoutineStepDao {
    
    /**
     * Get all steps for a routine composite ID
     */
    @Query("SELECT * FROM routine_steps WHERE routineCompositeId = :routineCompositeId")
    fun getStepsByRoutineCompositeId(routineCompositeId: String): Flow<List<RoutineStepEntity>>
    
    /**
     * Get all steps for a routine and user
     */
    @Query("SELECT * FROM routine_steps WHERE routineId = :routineId AND userId = :userId")
    fun getStepsByRoutineId(routineId: String, userId: String): Flow<List<RoutineStepEntity>>
    
    /**
     * Get step by composite ID
     */
    @Query("SELECT * FROM routine_steps WHERE compositeId = :compositeId")
    suspend fun getStepByCompositeId(compositeId: String): RoutineStepEntity?
    
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
     * Delete all steps for a routine composite ID
     */
    @Query("DELETE FROM routine_steps WHERE routineCompositeId = :routineCompositeId")
    suspend fun deleteStepsByRoutineCompositeId(routineCompositeId: String)
    
    /**
     * Delete all steps for a routine and user
     */
    @Query("DELETE FROM routine_steps WHERE routineId = :routineId AND userId = :userId")
    suspend fun deleteStepsByRoutineId(routineId: String, userId: String)
    
    /**
     * Delete all steps for a user
     */
    @Query("DELETE FROM routine_steps WHERE userId = :userId")
    suspend fun deleteAllSteps(userId: String)
}


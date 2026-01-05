package com.example.skinovate.data

import android.content.Context
import com.example.skinovate.auth.AuthRepository
import com.example.skinovate.data.database.DatabaseModule
import com.example.skinovate.data.database.RoutineEntity
import com.example.skinovate.data.database.RoutineStepEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object RoutineRepository {

    private var databaseInitialized = false
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // --- 1. Empty Default Data ---
    private val emptyMorning = Routine(
        title = "Morning Routine",
        time = "08:00 AM",
        steps = emptyList()
    )

    private val emptyEvening = Routine(
        title = "Evening Routine",
        time = "09:00 PM",
        steps = emptyList()
    )

    // --- 2. Live Data Streams ---
    private val _morningRoutine = MutableStateFlow(emptyMorning)
    val morningRoutine: StateFlow<Routine> = _morningRoutine.asStateFlow()

    private val _eveningRoutine = MutableStateFlow(emptyEvening)
    val eveningRoutine: StateFlow<Routine> = _eveningRoutine.asStateFlow()
    
    /**
     * Clear user data (called on logout)
     */
    fun clearUserData() {
        _morningRoutine.value = emptyMorning
        _eveningRoutine.value = emptyEvening
    }

    /**
     * Initialize repository dengan context
     * Load routines dari database
     */
    fun init(context: Context) {
        if (databaseInitialized) return
        
        val database = DatabaseModule.getDatabase(context)
        databaseInitialized = true
        
        // Load routines from database
        repositoryScope.launch {
            try {
                val userId = AuthRepository.currentUser.value?.id ?: return@launch
                
                // Load morning routine
                val morningEntity = database.routineDao().getRoutineById("morning", userId)
                if (morningEntity != null) {
                    // Get steps using first() from Flow
                    val stepEntities = database.routineStepDao().getStepsByRoutineId("morning", userId)
                    val stepList = stepEntities.first()
                    
                    val routineSteps = stepList.map { it.toRoutineStep() }
                    _morningRoutine.value = Routine(
                        title = morningEntity.title,
                        time = morningEntity.time,
                        steps = routineSteps
                    )
                } else {
                    // Keep empty - don't create default data
                    _morningRoutine.value = emptyMorning
                }
                
                // Load evening routine  
                val eveningEntity = database.routineDao().getRoutineById("evening", userId)
                if (eveningEntity != null) {
                    val stepEntities = database.routineStepDao().getStepsByRoutineId("evening", userId)
                    val stepList = stepEntities.first()
                    
                    val routineSteps = stepList.map { it.toRoutineStep() }
                    _eveningRoutine.value = Routine(
                        title = eveningEntity.title,
                        time = eveningEntity.time,
                        steps = routineSteps
                    )
                } else {
                    // Keep empty - don't create default data
                    _eveningRoutine.value = emptyEvening
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // On error, use empty routines
                _morningRoutine.value = emptyMorning
                _eveningRoutine.value = emptyEvening
            }
        }
    }

    /**
     * Save routine to database
     */
    private fun saveRoutineToDatabase(routine: Routine, routineId: String, context: Context) {
        repositoryScope.launch {
            try {
                val userId = AuthRepository.currentUser.value?.id ?: return@launch
                val database = DatabaseModule.getDatabase(context)
                
                val compositeId = "${userId}_${routineId}"
                
                // Save routine entity
                val routineEntity = RoutineEntity(
                    compositeId = compositeId,
                    id = routineId,
                    userId = userId,
                    title = routine.title,
                    time = routine.time
                )
                database.routineDao().insertOrUpdateRoutine(routineEntity)
                
                // Delete old steps
                database.routineStepDao().deleteStepsByRoutineCompositeId(compositeId)
                
                // Save steps
                val stepEntities = routine.steps.map { step ->
                    RoutineStepEntity.fromRoutineStep(step, routineId, userId, compositeId)
                }
                database.routineStepDao().insertSteps(stepEntities)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Update routine (helper function)
     */
    private fun updateRoutine(routine: Routine, routineId: String, context: Context) {
        saveRoutineToDatabase(routine, routineId, context)
        // Reschedule notifications if routine time changed
        com.example.skinovate.notifications.RoutineNotificationManager.rescheduleRoutineNotifications(context)
    }

    // --- 3. Functions to Edit Data ---

    fun addStepToMorning(step: RoutineStep, context: Context) {
        val current = _morningRoutine.value
        val updated = current.copy(steps = current.steps + step)
        _morningRoutine.value = updated
        updateRoutine(updated, "morning", context)
    }

    fun removeStepFromMorning(stepId: String, context: Context) {
        val current = _morningRoutine.value
        val updated = current.copy(steps = current.steps.filter { it.id != stepId })
        _morningRoutine.value = updated
        updateRoutine(updated, "morning", context)
    }

    fun addStepToEvening(step: RoutineStep, context: Context) {
        val current = _eveningRoutine.value
        val updated = current.copy(steps = current.steps + step)
        _eveningRoutine.value = updated
        updateRoutine(updated, "evening", context)
    }

    fun removeStepFromEvening(stepId: String, context: Context) {
        val current = _eveningRoutine.value
        val updated = current.copy(steps = current.steps.filter { it.id != stepId })
        _eveningRoutine.value = updated
        updateRoutine(updated, "evening", context)
    }
    
    fun updateStepInMorning(updatedStep: RoutineStep, context: Context) {
        val current = _morningRoutine.value
        val updated = current.copy(
            steps = current.steps.map { if (it.id == updatedStep.id) updatedStep else it }
        )
        _morningRoutine.value = updated
        updateRoutine(updated, "morning", context)
    }
    
    fun updateStepInEvening(updatedStep: RoutineStep, context: Context) {
        val current = _eveningRoutine.value
        val updated = current.copy(
            steps = current.steps.map { if (it.id == updatedStep.id) updatedStep else it }
        )
        _eveningRoutine.value = updated
        updateRoutine(updated, "evening", context)
    }
    
    /**
     * Update routine time
     */
    fun updateMorningRoutineTime(newTime: String, context: Context) {
        val current = _morningRoutine.value
        val updated = current.copy(time = newTime)
        _morningRoutine.value = updated
        updateRoutine(updated, "morning", context)
    }
    
    fun updateEveningRoutineTime(newTime: String, context: Context) {
        val current = _eveningRoutine.value
        val updated = current.copy(time = newTime)
        _eveningRoutine.value = updated
        updateRoutine(updated, "evening", context)
    }
    
    /**
     * Apply recommendation to routines
     */
    fun applyRecommendation(morningRoutine: Routine, eveningRoutine: Routine, context: Context) {
        try {
            // Update state first (synchronous) - this is safe
            _morningRoutine.value = morningRoutine
            _eveningRoutine.value = eveningRoutine
            
            // Save to database asynchronously
            repositoryScope.launch {
                try {
                    // Ensure database is initialized
                    val database = DatabaseModule.getDatabase(context)
                    if (database != null) {
                        saveRoutineToDatabase(morningRoutine, "morning", context)
                        saveRoutineToDatabase(eveningRoutine, "evening", context)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Even if database save fails, state is already updated
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If state update fails, at least try to update state
            try {
                _morningRoutine.value = morningRoutine
                _eveningRoutine.value = eveningRoutine
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }
    
    // Backward compatibility: functions without context (will use last context or skip DB)
    @Deprecated("Use version with context parameter", ReplaceWith("addStepToMorning(step, context)"))
    fun addStepToMorning(step: RoutineStep) {
        val current = _morningRoutine.value
        _morningRoutine.value = current.copy(steps = current.steps + step)
    }
    
    @Deprecated("Use version with context parameter", ReplaceWith("removeStepFromMorning(stepId, context)"))
    fun removeStepFromMorning(stepId: String) {
        val current = _morningRoutine.value
        _morningRoutine.value = current.copy(steps = current.steps.filter { it.id != stepId })
    }
    
    @Deprecated("Use version with context parameter", ReplaceWith("addStepToEvening(step, context)"))
    fun addStepToEvening(step: RoutineStep) {
        val current = _eveningRoutine.value
        _eveningRoutine.value = current.copy(steps = current.steps + step)
    }
    
    @Deprecated("Use version with context parameter", ReplaceWith("removeStepFromEvening(stepId, context)"))
    fun removeStepFromEvening(stepId: String) {
        val current = _eveningRoutine.value
        _eveningRoutine.value = current.copy(steps = current.steps.filter { it.id != stepId })
    }
}

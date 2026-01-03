package com.example.skinovate.data

import android.content.Context
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
    
    // Helper to make quick steps
    private fun createStep(type: SkincareStep, time: String? = null) =
        RoutineStep(type = type, time = time)

    // --- 1. Default Data ---
    private val defaultMorning = Routine(
        title = "Morning Routine",
        time = "08:00 AM",
        steps = listOf(
            createStep(SkincareStep.CLEANSER, "08:00 AM"),
            createStep(SkincareStep.TONER, "08:05 AM"),
            createStep(SkincareStep.MOISTURIZER, "08:10 AM"),
            createStep(SkincareStep.SUNSCREEN, "08:15 AM")
        )
    )

    private val defaultEvening = Routine(
        title = "Evening Routine",
        time = "09:30 PM",
        steps = listOf(
            createStep(SkincareStep.CLEANSER, "09:30 PM"),
            createStep(SkincareStep.RETINOL, "09:35 PM"),
            createStep(SkincareStep.MOISTURIZER, "09:40 PM")
        )
    )

    // --- 2. Live Data Streams ---
    private val _morningRoutine = MutableStateFlow(defaultMorning)
    val morningRoutine: StateFlow<Routine> = _morningRoutine.asStateFlow()

    private val _eveningRoutine = MutableStateFlow(defaultEvening)
    val eveningRoutine: StateFlow<Routine> = _eveningRoutine.asStateFlow()

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
                // Load morning routine
                val morningEntity = database.routineDao().getRoutineById("morning")
                if (morningEntity != null) {
                    // Get steps using first() from Flow
                    val stepEntities = database.routineStepDao().getStepsByRoutineId("morning")
                    val stepList = stepEntities.first()
                    
                    val routineSteps = stepList.map { it.toRoutineStep() }
                    _morningRoutine.value = Routine(
                        title = morningEntity.title,
                        time = morningEntity.time,
                        steps = routineSteps
                    )
                } else {
                    // Initialize with default data
                    saveRoutineToDatabase(defaultMorning, "morning", context)
                }
                
                // Load evening routine  
                val eveningEntity = database.routineDao().getRoutineById("evening")
                if (eveningEntity != null) {
                    val stepEntities = database.routineStepDao().getStepsByRoutineId("evening")
                    val stepList = stepEntities.first()
                    
                    val routineSteps = stepList.map { it.toRoutineStep() }
                    _eveningRoutine.value = Routine(
                        title = eveningEntity.title,
                        time = eveningEntity.time,
                        steps = routineSteps
                    )
                } else {
                    // Initialize with default data
                    saveRoutineToDatabase(defaultEvening, "evening", context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // On error, use defaults
                _morningRoutine.value = defaultMorning
                _eveningRoutine.value = defaultEvening
            }
        }
    }

    /**
     * Save routine to database
     */
    private fun saveRoutineToDatabase(routine: Routine, routineId: String, context: Context) {
        repositoryScope.launch {
            try {
                val database = DatabaseModule.getDatabase(context)
                
                // Save routine entity
                val routineEntity = RoutineEntity(
                    id = routineId,
                    title = routine.title,
                    time = routine.time
                )
                database.routineDao().insertOrUpdateRoutine(routineEntity)
                
                // Delete old steps
                database.routineStepDao().deleteStepsByRoutineId(routineId)
                
                // Save steps
                val stepEntities = routine.steps.map { step ->
                    RoutineStepEntity.fromRoutineStep(step, routineId)
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

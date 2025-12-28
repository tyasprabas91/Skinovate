package com.example.skinovate.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RoutineRepository {

    // Helper to make quick steps
    private fun createStep(type: SkincareStep, time: String? = null) =
        RoutineStep(type = type, time = time)

    // --- 1. Initial Data ---
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

    // --- 3. Functions to Edit Data ---

    // Updated to accept RoutineStep
    fun addStepToMorning(step: RoutineStep) {
        val current = _morningRoutine.value
        _morningRoutine.value = current.copy(steps = current.steps + step)
    }

    fun removeStepFromMorning(stepId: String) {
        val current = _morningRoutine.value
        _morningRoutine.value = current.copy(steps = current.steps.filter { it.id != stepId })
    }

    fun addStepToEvening(step: RoutineStep) {
        val current = _eveningRoutine.value
        _eveningRoutine.value = current.copy(steps = current.steps + step)
    }

    fun removeStepFromEvening(stepId: String) {
        val current = _eveningRoutine.value
        _eveningRoutine.value = current.copy(steps = current.steps.filter { it.id != stepId })
    }
}
package com.example.skinovate.data

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for RoutineRepository
 * 
 * Note: RoutineRepository requires Android Context and Room Database.
 * These tests are for basic data validation and logic.
 * For full integration tests, see androidTest directory.
 */
class RoutineRepositoryTest {

    @Test
    fun `Routine data class should have correct properties`() {
        val step = RoutineStep(
            id = "1",
            type = SkincareStep.CLEANSER,
            productName = "Test Cleanser",
            time = "08:00 AM"
        )
        
        val routine = Routine(
            title = "Morning Routine",
            time = "08:00 AM",
            steps = listOf(step)
        )
        
        assertEquals("Morning Routine", routine.title)
        assertEquals("08:00 AM", routine.time)
        assertEquals(1, routine.steps.size)
        assertEquals(step, routine.steps[0])
    }

    @Test
    fun `RoutineStep data class should have correct properties`() {
        val step = RoutineStep(
            id = "1",
            type = SkincareStep.CLEANSER,
            productName = "Test Cleanser",
            time = "08:00 AM"
        )
        
        assertEquals("1", step.id)
        assertEquals(SkincareStep.CLEANSER, step.type)
        assertEquals("Test Cleanser", step.productName)
        assertEquals("08:00 AM", step.time)
    }

    @Test
    fun `RoutineStep with default values should work`() {
        val step = RoutineStep(
            type = SkincareStep.CLEANSER
        )
        
        assertNotNull(step.id) // UUID generated
        assertEquals(SkincareStep.CLEANSER, step.type)
        assertEquals("", step.productName) // Default empty string
        assertNull(step.time) // Default null
    }

    @Test
    fun `RoutineStep with null time should work`() {
        val step = RoutineStep(
            id = "1",
            type = SkincareStep.CLEANSER,
            productName = "Test Cleanser",
            time = null
        )
        
        assertNull(step.time)
        assertEquals(SkincareStep.CLEANSER, step.type)
    }

    @Test
    fun `Routine with empty steps list should work`() {
        val routine = Routine(
            title = "Empty Routine",
            time = "08:00 AM",
            steps = emptyList()
        )
        
        assertTrue(routine.steps.isEmpty())
        assertEquals("Empty Routine", routine.title)
    }

    @Test
    fun `SkincareStep enum should have expected values`() {
        // Verify all expected SkincareStep values exist
        assertNotNull(SkincareStep.CLEANSER)
        assertNotNull(SkincareStep.TONER)
        assertNotNull(SkincareStep.MOISTURIZER)
        assertNotNull(SkincareStep.SUNSCREEN)
        assertNotNull(SkincareStep.SERUM)
        assertNotNull(SkincareStep.RETINOL)
        assertNotNull(SkincareStep.EYE_CREAM)
        assertNotNull(SkincareStep.FACE_MASK)
        
        // Verify display names
        assertEquals("Cleanser", SkincareStep.CLEANSER.displayName)
        assertEquals("Toner", SkincareStep.TONER.displayName)
        assertEquals("Moisturizer", SkincareStep.MOISTURIZER.displayName)
    }
}

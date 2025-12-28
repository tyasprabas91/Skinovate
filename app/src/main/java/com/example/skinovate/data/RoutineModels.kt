package com.example.skinovate.data

import java.util.UUID

// Keep your Enum (The Types)
enum class SkincareStep(val displayName: String) {
    CLEANSER("Cleanser"),
    TONER("Toner"),
    EXFOLIATOR("Exfoliator"),
    SERUM("Serum"),
    MOISTURIZER("Moisturizer"),
    SUNSCREEN("Sunscreen"),
    RETINOL("Retinol"),
    EYE_CREAM("Eye Cream"),
    FACE_MASK("Face Mask")
}

// NEW: The Wrapper that holds the details
data class RoutineStep(
    val id: String = UUID.randomUUID().toString(), // Unique ID for deleting later
    val type: SkincareStep,      // The Category (Enum)
    val productName: String = "", // User's custom name (Optional)
    val time: String? = null      // Specific time for this step (Optional)
)

// UPDATE: Routine now uses RoutineStep
data class Routine(
    val title: String,
    val time: String, // The Routine's overall start time
    val steps: List<RoutineStep> // <--- Changed from SkincareStep to RoutineStep
)
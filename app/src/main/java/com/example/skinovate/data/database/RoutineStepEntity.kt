package com.example.skinovate.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.skinovate.data.RoutineStep
import com.example.skinovate.data.SkincareStep

/**
 * Entity untuk RoutineStep dalam Room Database
 * Foreign key ke RoutineEntity
 */
@Entity(
    tableName = "routine_steps",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["compositeId"],
            childColumns = ["routineCompositeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoutineStepEntity(
    @PrimaryKey
    val compositeId: String, // Format: "userId_stepId" e.g., "user123_step1"
    val id: String,
    val userId: String, // User ID for data isolation
    val routineId: String, // "morning" or "evening"
    val routineCompositeId: String, // Foreign key to RoutineEntity.compositeId
    val stepType: String, // Stored as String, converted by TypeConverter
    val productName: String = "",
    val duration: Int = 60 // Timer duration in seconds (default: 60 seconds = 1 minute)
) {
    /**
     * Convert Entity to Domain Model
     */
    fun toRoutineStep(): RoutineStep {
        return RoutineStep(
            id = id,
            type = ConversionHelpers.toSkincareStep(stepType),
            productName = productName,
            duration = duration
        )
    }
    
    companion object {
        /**
         * Convert Domain Model to Entity
         */
        fun fromRoutineStep(routineStep: RoutineStep, routineId: String, userId: String, routineCompositeId: String): RoutineStepEntity {
            return RoutineStepEntity(
                compositeId = "${userId}_${routineStep.id}",
                id = routineStep.id,
                userId = userId,
                routineId = routineId,
                routineCompositeId = routineCompositeId,
                stepType = ConversionHelpers.fromSkincareStep(routineStep.type),
                productName = routineStep.productName,
                duration = routineStep.duration
            )
        }
    }
}


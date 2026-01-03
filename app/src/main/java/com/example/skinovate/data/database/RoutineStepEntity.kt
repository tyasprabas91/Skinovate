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
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoutineStepEntity(
    @PrimaryKey
    val id: String,
    val routineId: String, // Foreign key to RoutineEntity
    val stepType: String, // Stored as String, converted by TypeConverter
    val productName: String = "",
    val time: String? = null
) {
    /**
     * Convert Entity to Domain Model
     */
    fun toRoutineStep(): RoutineStep {
        return RoutineStep(
            id = id,
            type = ConversionHelpers.toSkincareStep(stepType),
            productName = productName,
            time = time
        )
    }
    
    companion object {
        /**
         * Convert Domain Model to Entity
         */
        fun fromRoutineStep(routineStep: RoutineStep, routineId: String): RoutineStepEntity {
            return RoutineStepEntity(
                id = routineStep.id,
                routineId = routineId,
                stepType = ConversionHelpers.fromSkincareStep(routineStep.type),
                productName = routineStep.productName,
                time = routineStep.time
            )
        }
    }
}


package com.example.skinovate.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skinovate.data.Routine

/**
 * Entity untuk Routine dalam Room Database
 * Steps disimpan di table terpisah (RoutineStepEntity)
 */
@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey
    val id: String, // "morning" or "evening"
    val title: String,
    val time: String
)


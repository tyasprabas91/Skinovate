package com.example.skinovate.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room Database untuk aplikasi Skinovate
 * 
 * Version 3: Added userId to all user-specific entities for data isolation
 * Version 4: Changed RoutineStepEntity.time to duration (Int, in minutes)
 */
@Database(
    entities = [
        ProductEntity::class,
        RoutineEntity::class,
        RoutineStepEntity::class,
        ScanHistoryEntity::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SkinovateDatabase : RoomDatabase() {
    
    abstract fun productDao(): ProductDao
    abstract fun routineDao(): RoutineDao
    abstract fun routineStepDao(): RoutineStepDao
    abstract fun scanHistoryDao(): ScanHistoryDao
    
    companion object {
        const val DATABASE_NAME = "skinovate_database"
    }
}


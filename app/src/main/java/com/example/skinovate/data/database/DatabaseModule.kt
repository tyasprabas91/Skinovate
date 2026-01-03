package com.example.skinovate.data.database

import android.content.Context
import androidx.room.Room

/**
 * Database module untuk manage database instance
 * Singleton pattern untuk ensure single database instance
 */
object DatabaseModule {
    
    @Volatile
    private var INSTANCE: SkinovateDatabase? = null
    
    /**
     * Get database instance (singleton)
     */
    fun getDatabase(context: Context): SkinovateDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                SkinovateDatabase::class.java,
                SkinovateDatabase.DATABASE_NAME
            )
                .fallbackToDestructiveMigration() // For development - remove in production
                .build()
            INSTANCE = instance
            instance
        }
    }
}


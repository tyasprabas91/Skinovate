package com.example.skinovate.data.database

import androidx.room.TypeConverter
import com.example.skinovate.data.SkincareStep

/**
 * Type converters untuk Room Database
 * Mengkonversi complex types (List, Enum) ke String dan sebaliknya
 * Room akan membuat instance dari class ini
 */
class Converters {
    
    /**
     * Convert List<String> to comma-separated String
     */
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }
    
    /**
     * Convert comma-separated String to List<String>
     */
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            value.split(",")
        }
    }
    
    /**
     * Convert SkincareStep enum to String
     */
    @TypeConverter
    fun fromSkincareStep(value: SkincareStep): String {
        return value.name
    }
    
    /**
     * Convert String to SkincareStep enum
     */
    @TypeConverter
    fun toSkincareStep(value: String): SkincareStep {
        return SkincareStep.valueOf(value)
    }
}

/**
 * Helper functions untuk manual conversion di Entity classes
 * (bukan untuk TypeConverter, tapi untuk manual conversion)
 */
object ConversionHelpers {
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }
    
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            value.split(",")
        }
    }
    
    fun fromSkincareStep(value: SkincareStep): String {
        return value.name
    }
    
    fun toSkincareStep(value: String): SkincareStep {
        return SkincareStep.valueOf(value)
    }
}


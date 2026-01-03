package com.example.skinovate.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skinovate.data.ScanResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Entity untuk Scan History dalam Room Database
 */
@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val score: Int,
    val skinType: String,
    val acnePercentage: Int,
    val dryPercentage: Int,
    val recommendation: String,
    val dateTimestamp: Long = System.currentTimeMillis() // Store as timestamp for sorting
) {
    /**
     * Convert Entity to Domain Model
     */
    fun toScanResult(): ScanResult {
        val date = if (dateTimestamp > 0) {
            formatDate(dateTimestamp)
        } else {
            "Today"
        }
        
        return ScanResult(
            score = score,
            skinType = skinType,
            acnePercentage = acnePercentage,
            dryPercentage = dryPercentage,
            recommendation = recommendation,
            date = date
        )
    }
    
    private fun formatDate(timestamp: Long): String {
        val dateObj = Date(timestamp)
        val today = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        val dateStr = formatter.format(dateObj)
        val todayStr = formatter.format(today)
        
        val calendar = Calendar.getInstance()
        calendar.time = today
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = formatter.format(calendar.time)
        
        return when {
            dateStr == todayStr -> "Today"
            dateStr == yesterdayStr -> "Yesterday"
            else -> dateStr
        }
    }
    
    companion object {
        /**
         * Convert Domain Model to Entity
         */
        fun fromScanResult(scanResult: ScanResult): ScanHistoryEntity {
            return ScanHistoryEntity(
                score = scanResult.score,
                skinType = scanResult.skinType,
                acnePercentage = scanResult.acnePercentage,
                dryPercentage = scanResult.dryPercentage,
                recommendation = scanResult.recommendation,
                dateTimestamp = System.currentTimeMillis()
            )
        }
    }
}


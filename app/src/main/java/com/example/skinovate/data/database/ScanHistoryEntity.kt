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
    val tipsString: String = "", // Store as string for simplicity
    val dateTimestamp: Long = System.currentTimeMillis() // Store as timestamp for sorting
) {
    /**
     * Convert Entity to Domain Model
     */
    fun toScanResult(): ScanResult {
        val date = formatDate(dateTimestamp)

        // Convert "Tip1|Tip2" string back to a List
        val tipsList = if (tipsString.isEmpty()) emptyList() else tipsString.split("|")

        return ScanResult(
            score = score,
            skinType = skinType,
            acnePercentage = acnePercentage,
            dryPercentage = dryPercentage,
            recommendation = recommendation,
            tips = tipsList, // <--- Map it here
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
        fun fromScanResult(scanResult: ScanResult): ScanHistoryEntity {
            return ScanHistoryEntity(
                score = scanResult.score,
                skinType = scanResult.skinType,
                acnePercentage = scanResult.acnePercentage,
                dryPercentage = scanResult.dryPercentage,
                recommendation = scanResult.recommendation,
                tipsString = scanResult.tips.joinToString("|"), // <--- Flatten List to String
                dateTimestamp = System.currentTimeMillis()
            )
        }
    }
}


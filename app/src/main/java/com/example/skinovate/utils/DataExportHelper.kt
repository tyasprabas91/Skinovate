package com.example.skinovate.utils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.example.skinovate.auth.AuthRepository
import com.example.skinovate.data.database.DatabaseModule
import com.example.skinovate.data.ScanResult
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper untuk export dan delete user data
 */
object DataExportHelper {
    
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create()
    
    /**
     * Export semua user data ke JSON file
     */
    suspend fun exportUserData(context: Context): Result<File> = withContext(Dispatchers.IO) {
        try {
            val userId = AuthRepository.currentUser.value?.id ?: return@withContext Result.failure(Exception("User not logged in"))
            val database = DatabaseModule.getDatabase(context)
            
            // Collect all user data
            val scanHistory = database.scanHistoryDao().getAllScans(userId).first()
            val morningRoutine = database.routineDao().getRoutineById("morning", userId)
            val eveningRoutine = database.routineDao().getRoutineById("evening", userId)
            
            val morningSteps = morningRoutine?.let {
                database.routineStepDao().getStepsByRoutineId("morning", userId).first()
            } ?: emptyList()
            
            val eveningSteps = eveningRoutine?.let {
                database.routineStepDao().getStepsByRoutineId("evening", userId).first()
            } ?: emptyList()
            
            // Create data structure
            val exportData = mapOf(
                "exportDate" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                "scanHistory" to scanHistory.map { entity ->
                    entity.toScanResult()
                },
                "routines" to mapOf(
                    "morning" to mapOf(
                        "routine" to morningRoutine,
                        "steps" to morningSteps.map { stepEntity ->
                            stepEntity.toRoutineStep()
                        }
                    ),
                    "evening" to mapOf(
                        "routine" to eveningRoutine,
                        "steps" to eveningSteps.map { stepEntity ->
                            stepEntity.toRoutineStep()
                        }
                    )
                )
            )
            
            // Convert to JSON
            val jsonString = gson.toJson(exportData)
            
            // Get external storage directory
            val downloadsDir = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_DOWNLOADS).firstOrNull()
                ?: context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.filesDir
            
            val fileName = "skinovate_export_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.json"
            val file = File(downloadsDir, fileName)
            
            // Write to file
            FileWriter(file).use { writer ->
                writer.write(jsonString)
            }
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete all user data dari database dan SharedPreferences
     */
    suspend fun deleteAllUserData(context: Context): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = AuthRepository.currentUser.value?.id ?: return@withContext Result.failure(Exception("User not logged in"))
            val database = DatabaseModule.getDatabase(context)
            
            // Delete all user data from database
            database.scanHistoryDao().deleteAllScans(userId)
            database.routineStepDao().deleteAllSteps(userId)
            
            // Delete routines (steps will be cascade deleted)
            database.routineDao().deleteAllRoutines(userId)
            
            // Clear SharedPreferences (except notification settings - user might want to keep those)
            // We'll clear auth and product seeding flag
            val authPrefs = context.getSharedPreferences("skinovate_auth_prefs", Context.MODE_PRIVATE)
            authPrefs.edit().clear().apply()
            
            val productPrefs = context.getSharedPreferences("skinovate_products_prefs", Context.MODE_PRIVATE)
            productPrefs.edit().clear().apply()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

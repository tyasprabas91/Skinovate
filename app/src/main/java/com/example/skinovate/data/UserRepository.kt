package com.example.skinovate.data

import android.content.Context
import com.example.skinovate.data.database.DatabaseModule
import com.example.skinovate.data.database.ScanHistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Data model tetap sama
data class ScanResult(
    val score: Int,
    val skinType: String,
    val acnePercentage: Int,
    val dryPercentage: Int,
    val recommendation: String,
    val tips: List<String> = emptyList(),
    val date: String = "Today" // Default value
)

// 2. Repository dengan Room Database
object UserRepository {
    
    private var databaseInitialized = false
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // StateFlow untuk lastScan (reactive)
    private val _lastScan = MutableStateFlow<ScanResult?>(null)
    val lastScan: StateFlow<ScanResult?> = _lastScan.asStateFlow()
    
    // Backward compatibility: Property untuk akses langsung (akan deprecated)
    @Deprecated("Use lastScan Flow instead", ReplaceWith("lastScan.value"))
    var lastScanSync: ScanResult?
        get() = _lastScan.value
        set(value) {
            _lastScan.value = value
        }
    
    /**
     * Initialize repository dengan context
     * Load latest scan dari database
     */
    fun init(context: Context) {
        if (databaseInitialized) return
        
        val database = DatabaseModule.getDatabase(context)
        databaseInitialized = true
        
        // Load latest scan from database
        repositoryScope.launch {
            try {
                val latestScanEntity = database.scanHistoryDao().getLatestScan()
                if (latestScanEntity != null) {
                    _lastScan.value = latestScanEntity.toScanResult()
                }
            } catch (e: Exception) {
                // Database belum ready atau error, ignore
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Save scan result ke database
     */
    fun saveScan(scanResult: ScanResult, context: Context) {
        if (!databaseInitialized) {
            init(context)
        }
        
        repositoryScope.launch {
            try {
                val database = DatabaseModule.getDatabase(context)
                val entity = ScanHistoryEntity.fromScanResult(scanResult)
                database.scanHistoryDao().insertScan(entity)
                
                // Update lastScan
                _lastScan.value = scanResult
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Get all scan history
     */
    fun getAllScans(context: Context) = DatabaseModule.getDatabase(context).scanHistoryDao().getAllScans()
}

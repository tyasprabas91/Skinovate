package com.example.skinovate.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk Scan History operations
 */
@Dao
interface ScanHistoryDao {
    
    /**
     * Get all scan history, ordered by most recent first
     */
    @Query("SELECT * FROM scan_history ORDER BY dateTimestamp DESC")
    fun getAllScans(): Flow<List<ScanHistoryEntity>>
    
    /**
     * Get latest scan
     */
    @Query("SELECT * FROM scan_history ORDER BY dateTimestamp DESC LIMIT 1")
    suspend fun getLatestScan(): ScanHistoryEntity?
    
    /**
     * Get scan by ID
     */
    @Query("SELECT * FROM scan_history WHERE id = :scanId")
    suspend fun getScanById(scanId: Long): ScanHistoryEntity?
    
    /**
     * Insert scan
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity): Long
    
    /**
     * Update scan
     */
    @Update
    suspend fun updateScan(scan: ScanHistoryEntity)
    
    /**
     * Delete scan
     */
    @Delete
    suspend fun deleteScan(scan: ScanHistoryEntity)
    
    /**
     * Delete scan by ID
     */
    @Query("DELETE FROM scan_history WHERE id = :scanId")
    suspend fun deleteScanById(scanId: Long)
    
    /**
     * Delete all scans
     */
    @Query("DELETE FROM scan_history")
    suspend fun deleteAllScans()
}


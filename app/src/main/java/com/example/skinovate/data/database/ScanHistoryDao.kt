package com.example.skinovate.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk Scan History operations
 */
@Dao
interface ScanHistoryDao {
    
    /**
     * Get all scan history for a user, ordered by most recent first
     */
    @Query("SELECT * FROM scan_history WHERE userId = :userId ORDER BY dateTimestamp DESC")
    fun getAllScans(userId: String): Flow<List<ScanHistoryEntity>>
    
    /**
     * Get latest scan for a user
     */
    @Query("SELECT * FROM scan_history WHERE userId = :userId ORDER BY dateTimestamp DESC LIMIT 1")
    suspend fun getLatestScan(userId: String): ScanHistoryEntity?
    
    /**
     * Get scan by ID and userId
     */
    @Query("SELECT * FROM scan_history WHERE id = :scanId AND userId = :userId")
    suspend fun getScanById(scanId: Long, userId: String): ScanHistoryEntity?
    
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
     * Delete all scans for a user
     */
    @Query("DELETE FROM scan_history WHERE userId = :userId")
    suspend fun deleteAllScans(userId: String)
    
    /**
     * Get scan count for a user
     */
    @Query("SELECT COUNT(*) FROM scan_history WHERE userId = :userId")
    suspend fun getScanCount(userId: String): Int
}


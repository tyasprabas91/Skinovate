package com.example.skinovate.data

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for UserRepository
 * 
 * Note: UserRepository requires Android Context and Room Database.
 * These tests are for basic data validation and logic.
 * For full integration tests, see androidTest directory.
 */
class UserRepositoryTest {

    @Test
    fun `ScanResult data class should have correct properties`() {
        val scanResult = ScanResult(
            score = 85,
            skinType = "Combination",
            acnePercentage = 10,
            dryPercentage = 20,
            recommendation = "Use gentle cleanser",
            date = "2024-01-01"
        )
        
        assertEquals(85, scanResult.score)
        assertEquals("Combination", scanResult.skinType)
        assertEquals(10, scanResult.acnePercentage)
        assertEquals(20, scanResult.dryPercentage)
        assertEquals("Use gentle cleanser", scanResult.recommendation)
        assertEquals("2024-01-01", scanResult.date)
    }

    @Test
    fun `ScanResult with default date should work`() {
        val scanResult = ScanResult(
            score = 85,
            skinType = "Combination",
            acnePercentage = 10,
            dryPercentage = 20,
            recommendation = "Use gentle cleanser"
        )
        
        assertEquals("Today", scanResult.date)
    }

    @Test
    fun `ScanResult score should be within valid range`() {
        // Score should typically be 0-100
        val scanResult = ScanResult(
            score = 75,
            skinType = "Normal",
            acnePercentage = 5,
            dryPercentage = 10,
            recommendation = "Good skin condition"
        )
        
        assertTrue("Score should be >= 0", scanResult.score >= 0)
        assertTrue("Score should be <= 100", scanResult.score <= 100)
    }

    @Test
    fun `ScanResult percentages should be within valid range`() {
        val scanResult = ScanResult(
            score = 80,
            skinType = "Normal",
            acnePercentage = 5,
            dryPercentage = 15,
            recommendation = "Good skin"
        )
        
        assertTrue("Acne percentage should be >= 0", scanResult.acnePercentage >= 0)
        assertTrue("Acne percentage should be <= 100", scanResult.acnePercentage <= 100)
        assertTrue("Dry percentage should be >= 0", scanResult.dryPercentage >= 0)
        assertTrue("Dry percentage should be <= 100", scanResult.dryPercentage <= 100)
    }
}


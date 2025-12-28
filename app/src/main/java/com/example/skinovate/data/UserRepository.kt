package com.example.skinovate.data

// 1. We move the data model here so both screens can use it
data class ScanResult(
    val score: Int,
    val skinType: String,
    val acnePercentage: Int,
    val dryPercentage: Int,
    val recommendation: String,
    val date: String = "Today" // Default value
)

// 2. A Singleton to store the data
object UserRepository {
    // Starts as null (no scan yet)
    var lastScan: ScanResult? = null
}
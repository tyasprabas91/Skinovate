package com.example.skinovate.data

/**
 * A simple data model for the UI to display analysis results.
 */
data class SkinAnalysisResult(
    val score: Int,
    val skinType: String,
    val acnePercentage: Int,
    val dryPercentage: Int,
    val recommendation: String,
    val tips: List<String> = emptyList() // Added tips here safely
)
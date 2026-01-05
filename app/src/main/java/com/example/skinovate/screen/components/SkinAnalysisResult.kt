package com.example.skinovate.screen.components

/**
 * Result dari skin analysis
 */
data class SkinAnalysisResult(
    val score: Int,
    val skinType: String,
    val acnePercentage: Int,
    val dryPercentage: Int,
    val recommendation: String
)


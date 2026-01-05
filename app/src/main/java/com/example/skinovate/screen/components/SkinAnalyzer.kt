package com.example.skinovate.screen.components

import com.example.skinovate.data.GeminiRepository
import com.example.skinovate.data.SkinAnalysisResult // Imports the file from Step 1
import java.io.File

/**
 * Wrapper class that connects the Camera Screen to the GeminiRepository.
 */
class SkinAnalyzer {

    suspend fun analyzeSkin(imageFile: File): SkinAnalysisResult {
        // 1. Call the shared Repository
        val result = GeminiRepository.analyzeImage(imageFile)

        // 2. Return result or fallback
        return result ?: createDefaultResult()
    }

    private fun createDefaultResult(): SkinAnalysisResult {
        return SkinAnalysisResult(
            score = 0,
            skinType = "Unknown",
            acnePercentage = 0,
            dryPercentage = 0,
            recommendation = "Analysis failed. Check internet.",
            tips = emptyList()
        )
    }

    fun close() {
        // No cleanup needed
    }
}
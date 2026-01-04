package com.example.skinovate.screen.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import java.io.File
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * SkinAnalyzer menggunakan ML Kit Face Detection dan image processing
 * untuk menganalisis kondisi kulit dari foto wajah
 */
class SkinAnalyzer {
    
    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .build()
    
    private val faceDetector = FaceDetection.getClient(faceDetectorOptions)
    
    /**
     * Analyze skin dari bitmap image
     * @return SkinAnalysisResult dengan score, skinType, acnePercentage, dryPercentage, recommendation
     */
    suspend fun analyzeSkin(imageFile: File): SkinAnalysisResult = suspendCancellableCoroutine { continuation ->
        try {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            if (bitmap == null) {
                continuation.resume(createDefaultResult())
                return@suspendCancellableCoroutine
            }
            
            val image = InputImage.fromBitmap(bitmap, 0)
            
            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isEmpty()) {
                        continuation.resume(createDefaultResult())
                        return@addOnSuccessListener
                    }
                    
                    // Gunakan wajah pertama (terbesar/terdepan)
                    val face = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() } ?: faces[0]
                    
                    // Analisis kulit dari region wajah
                    val analysis = analyzeFaceRegion(bitmap, face)
                    continuation.resume(analysis)
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    // Return default result jika error
                    continuation.resume(createDefaultResult())
                }
        } catch (e: Exception) {
            e.printStackTrace()
            continuation.resume(createDefaultResult())
        }
    }
    
    /**
     * Analisis region wajah dari bitmap
     */
    private fun analyzeFaceRegion(bitmap: Bitmap, face: Face): SkinAnalysisResult {
        val boundingBox = face.boundingBox
        
        // Ekstrak region wajah (dengan padding kecil)
        val padding = 20
        val left = (boundingBox.left - padding).coerceAtLeast(0)
        val top = (boundingBox.top - padding).coerceAtLeast(0)
        val right = (boundingBox.right + padding).coerceAtMost(bitmap.width)
        val bottom = (boundingBox.bottom + padding).coerceAtMost(bitmap.height)
        
        val width = right - left
        val height = bottom - top
        
        if (width <= 0 || height <= 0) {
            return createDefaultResult()
        }
        
        // Crop region wajah
        val faceBitmap = Bitmap.createBitmap(bitmap, left, top, width, height)
        
        // Analisis warna dan texture
        val skinMetrics = analyzeSkinMetrics(faceBitmap)
        
        // Deteksi skin type berdasarkan metrics
        val skinType = determineSkinType(skinMetrics)
        
        // Estimasi acne percentage berdasarkan texture variations
        val acnePercentage = estimateAcnePercentage(skinMetrics)
        
        // Estimasi dryness percentage
        val dryPercentage = estimateDrynessPercentage(skinMetrics)
        
        // Calculate overall score (0-100)
        val score = calculateOverallScore(skinMetrics, acnePercentage, dryPercentage)
        
        // Generate recommendation
        val recommendation = generateRecommendation(score, skinType, acnePercentage, dryPercentage)
        
        return SkinAnalysisResult(
            score = score,
            skinType = skinType,
            acnePercentage = acnePercentage,
            dryPercentage = dryPercentage,
            recommendation = recommendation
        )
    }
    
    /**
     * Analisis metrics kulit dari bitmap
     */
    private fun analyzeSkinMetrics(bitmap: Bitmap): SkinMetrics {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        var totalRed = 0L
        var totalGreen = 0L
        var totalBlue = 0L
        var totalBrightness = 0.0
        var varianceSum = 0.0
        
        // Sample pixels (every 10th pixel untuk performance)
        val sampleStep = 10
        var sampleCount = 0
        
        for (i in pixels.indices step sampleStep) {
            val pixel = pixels[i]
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            
            totalRed += r
            totalGreen += g
            totalBlue += b
            
            // Calculate brightness (luminance)
            val brightness = 0.299 * r + 0.587 * g + 0.114 * b
            totalBrightness += brightness
            
            sampleCount++
        }
        
        if (sampleCount == 0) {
            return SkinMetrics(0.5, 0.5, 0.5, 128.0, 0.0)
        }
        
        val avgRed = totalRed.toDouble() / sampleCount
        val avgGreen = totalGreen.toDouble() / sampleCount
        val avgBlue = totalBlue.toDouble() / sampleCount
        val avgBrightness = totalBrightness / sampleCount
        
        // Calculate variance (texture variation indicator)
        for (i in pixels.indices step sampleStep) {
            val pixel = pixels[i]
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            val brightness = 0.299 * r + 0.587 * g + 0.114 * b
            
            varianceSum += (brightness - avgBrightness).pow(2)
        }
        
        val variance = varianceSum / sampleCount
        
        // Normalize to 0-1 range
        val normalizedVariance = (variance / (255.0 * 255.0)).coerceIn(0.0, 1.0)
        
        return SkinMetrics(
            redRatio = avgRed / 255.0,
            greenRatio = avgGreen / 255.0,
            blueRatio = avgBlue / 255.0,
            brightness = avgBrightness,
            textureVariance = normalizedVariance
        )
    }
    
    /**
     * Determine skin type berdasarkan metrics
     */
    private fun determineSkinType(metrics: SkinMetrics): String {
        // Oily skin: higher brightness, more yellow (higher red/green ratio)
        // Dry skin: lower brightness, more pale
        // Combination: mixed
        // Sensitive: higher redness
        
        val redness = metrics.redRatio
        val brightness = metrics.brightness / 255.0
        
        return when {
            redness > 0.55 && brightness < 0.65 -> "Sensitive"
            brightness > 0.75 && metrics.redRatio > 0.5 -> "Oily"
            brightness < 0.60 -> "Dry"
            metrics.textureVariance > 0.15 -> "Combination"
            else -> "Normal"
        }
    }
    
    /**
     * Estimate acne percentage berdasarkan texture variance
     */
    private fun estimateAcnePercentage(metrics: SkinMetrics): Int {
        // Higher texture variance = more texture variations = potential acne
        val basePercentage = (metrics.textureVariance * 100).toInt()
        return basePercentage.coerceIn(0, 40) // Cap at 40%
    }
    
    /**
     * Estimate dryness percentage
     */
    private fun estimateDrynessPercentage(metrics: SkinMetrics): Int {
        // Lower brightness and higher variance = dryness
        val brightnessFactor = 1.0 - (metrics.brightness / 255.0)
        val varianceFactor = metrics.textureVariance
        
        val drynessScore = (brightnessFactor * 0.6 + varianceFactor * 0.4) * 100
        return drynessScore.toInt().coerceIn(0, 50) // Cap at 50%
    }
    
    /**
     * Calculate overall skin health score (0-100)
     */
    private fun calculateOverallScore(
        metrics: SkinMetrics,
        acnePercentage: Int,
        dryPercentage: Int
    ): Int {
        // Base score dari brightness (healthier skin biasanya lebih bright)
        val brightnessScore = (metrics.brightness / 255.0) * 40
        
        // Penalty untuk acne
        val acnePenalty = acnePercentage * 0.5
        
        // Penalty untuk dryness
        val drynessPenalty = dryPercentage * 0.3
        
        // Bonus untuk texture smoothness (lower variance = smoother)
        val smoothnessBonus = (1.0 - metrics.textureVariance) * 20
        
        val rawScore = brightnessScore + smoothnessBonus - acnePenalty - drynessPenalty
        return rawScore.toInt().coerceIn(60, 98) // Minimum 60, maximum 98 untuk realism
    }
    
    /**
     * Generate recommendation berdasarkan analisis
     */
    private fun generateRecommendation(
        score: Int,
        skinType: String,
        acnePercentage: Int,
        dryPercentage: Int
    ): String {
        val recommendations = mutableListOf<String>()
        
        when (skinType) {
            "Oily" -> recommendations.add("Gunakan cleanser untuk oily skin")
            "Dry" -> recommendations.add("Tingkatkan penggunaan moisturizer")
            "Sensitive" -> recommendations.add("Gunakan produk hypoallergenic")
            "Combination" -> recommendations.add("Gunakan produk untuk combination skin")
        }
        
        if (acnePercentage > 20) {
            recommendations.add("Pertimbangkan produk untuk acne-prone skin")
        }
        
        if (dryPercentage > 25) {
            recommendations.add("Tambahkan hydrating serum ke routine Anda")
        }
        
        if (score > 85) {
            recommendations.add("Kulit Anda dalam kondisi baik, pertahankan routine")
        } else if (score < 75) {
            recommendations.add("Pertimbangkan konsultasi dengan dermatologist")
        }
        
        return recommendations.firstOrNull() ?: "Pertahankan skincare routine yang konsisten"
    }
    
    /**
     * Default result jika analysis gagal
     */
    private fun createDefaultResult(): SkinAnalysisResult {
        return SkinAnalysisResult(
            score = 75,
            skinType = "Normal",
            acnePercentage = 10,
            dryPercentage = 15,
            recommendation = "Lakukan scan ulang dengan pencahayaan yang lebih baik"
        )
    }
    
    /**
     * Cleanup resources
     */
    fun close() {
        faceDetector.close()
    }
    
    /**
     * Data class untuk skin metrics
     */
    private data class SkinMetrics(
        val redRatio: Double,
        val greenRatio: Double,
        val blueRatio: Double,
        val brightness: Double,
        val textureVariance: Double
    )
    
}

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


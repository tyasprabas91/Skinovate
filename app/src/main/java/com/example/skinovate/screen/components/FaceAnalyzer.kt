package com.example.skinovate.screens.components

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

// This class runs on every single camera frame
class FaceAnalyzer(private val onFaceDetected: (Boolean) -> Unit) : ImageAnalysis.Analyzer {

    // High-performance settings (we just want to know if a face exists)
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .build()

    private val detector = FaceDetection.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            detector.process(image)
                .addOnSuccessListener { faces ->
                    // If faces list is not empty, we found a face!
                    val isFacePresent = faces.isNotEmpty()
                    onFaceDetected(isFacePresent)
                }
                .addOnCompleteListener {
                    // MUST close the image so the camera can send the next frame
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
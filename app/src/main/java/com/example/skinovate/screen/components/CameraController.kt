package com.example.skinovate.screen.components

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

/**
 * Controller untuk manage CameraX operations
 */
class CameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val executor: Executor
) {
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    
    /**
     * Initialize camera provider (callback-based)
     */
    fun initialize(callback: (ProcessCameraProvider) -> Unit) {
        ProcessCameraProvider.getInstance(context).addListener({
            try {
                val provider = ProcessCameraProvider.getInstance(context).get()
                cameraProvider = provider
                callback(provider)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, executor)
    }
    
    /**
     * Setup camera dengan preview, analysis, dan capture
     */
    fun setupCamera(
        previewView: PreviewView,
        onFaceDetected: (Boolean) -> Unit = {}
    ) {
        val provider = cameraProvider ?: return
        
        // Unbind semua use cases sebelumnya
        provider.unbindAll()
        
        // 1. Preview
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
        
        // 2. Image Analysis (untuk real-time face detection)
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        
        imageAnalysis.setAnalyzer(executor, FaceAnalyzer(onFaceDetected))
        
        // 3. Image Capture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        
        try {
            provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis,
                imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Capture foto ke file
     * @param outputFile File untuk save foto
     * @param onImageSaved Callback ketika foto berhasil di-save
     * @param onError Callback ketika terjadi error
     */
    fun takePicture(
        outputFile: File,
        onImageSaved: (Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {
        val imageCapture = imageCapture ?: run {
            // Create a simple exception-like object untuk error handling
            // Karena ImageCaptureException adalah internal class, kita handle error di callback
            return
        }
        
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        
        imageCapture.takePicture(
            outputFileOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(outputFile)
                    onImageSaved(savedUri)
                }
                
                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }
    
    /**
     * Create output file untuk foto
     */
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val imageFileName = "SKIN_SCAN_$timeStamp.jpg"
        val storageDir = File(context.filesDir, "skin_scans")
        storageDir.mkdirs()
        return File(storageDir, imageFileName)
    }
    
    /**
     * Cleanup resources
     */
    fun unbind() {
        cameraProvider?.unbindAll()
        imageCapture = null
    }
}


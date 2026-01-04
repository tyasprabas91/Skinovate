package com.example.skinovate.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.skinovate.data.ScanResult
import com.example.skinovate.data.UserRepository
import com.example.skinovate.screen.components.CameraPreview
import com.example.skinovate.screen.components.CameraController
import com.example.skinovate.screen.components.SkinAnalyzer
import com.example.skinovate.screen.components.SkinAnalysisResult
import com.example.skinovate.ui.components.EmptyScanHistoryState
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.platform.LocalLifecycleOwner
import java.io.File

// The 4 States of this Screen
enum class ScanState { HISTORY, CAMERA_PREVIEW, ANALYZING, RESULT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceAnalysisScreen(navController: NavController) {
    val context = LocalContext.current
    var currentState by remember { mutableStateOf(ScanState.HISTORY) }

    // Use ScanResult from your data package
    var currentResult by remember { mutableStateOf<ScanResult?>(null) }
    var capturedImageFile by remember { mutableStateOf<File?>(null) }
    
    // Initialize UserRepository
    LaunchedEffect(Unit) {
        UserRepository.init(context)
    }

    // Mock Data for History
    val historyItems = listOf("Dec 28 - Oily", "Dec 20 - Dry", "Dec 12 - Normal")

    Scaffold(
        containerColor = if (currentState == ScanState.RESULT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
        topBar = {
            if (currentState != ScanState.RESULT) {
                CenterAlignedTopAppBar(
                    title = { Text("Face Analysis", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentState == ScanState.HISTORY) {
                                navController.popBackStack() // Go home
                            } else {
                                currentState = ScanState.HISTORY // Go back to history
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (currentState) {
                ScanState.HISTORY -> HistoryView(
                    historyItems = historyItems,
                    onStartScan = { currentState = ScanState.CAMERA_PREVIEW }
                )
                ScanState.CAMERA_PREVIEW -> CameraView(
                    onCapture = { imageFile ->
                        capturedImageFile = imageFile
                        currentState = ScanState.ANALYZING
                    }
                )
                ScanState.ANALYZING -> AnalyzingView(
                    imageFile = capturedImageFile,
                    onFinished = { result ->
                        // Convert SkinAnalysisResult to ScanResult
                        val newResult = ScanResult(
                            score = result.score,
                            skinType = result.skinType,
                            acnePercentage = result.acnePercentage,
                            dryPercentage = result.dryPercentage,
                            recommendation = result.recommendation
                        )

                        // 1. Update Local State
                        currentResult = newResult
                        // 2. SAVE TO REPOSITORY (This makes it visible on Home Screen!)
                        UserRepository.saveScan(newResult, context)

                        currentState = ScanState.RESULT
                    }
                )
                ScanState.RESULT -> ResultView(
                    data = currentResult!!,
                    onBack = { currentState = ScanState.HISTORY }
                )
            }
        }
    }
}

// --- 1. HISTORY VIEW ---
@Composable
fun HistoryView(historyItems: List<String>, onStartScan: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().height(150.dp).clickable { onStartScan() }
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Face, null, tint = Color.White, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Start New Face Scan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Scan History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (historyItems.isEmpty()) {
            EmptyScanHistoryState(
                onStartScan = onStartScan,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(historyItems.size) { index ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(historyItems[index], fontWeight = FontWeight.Medium)
                            Text("View >", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// --- 2. CAMERA VIEW ---
@Composable
fun CameraView(onCapture: (File) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = ContextCompat.getMainExecutor(context)
    var hasPermission by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraController by remember { mutableStateOf<CameraController?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )

    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.CAMERA)
    }

    // Initialize camera controller
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val controller = CameraController(context, lifecycleOwner, executor)
            cameraController = controller
            controller.initialize { provider ->
                // Camera provider initialized
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasPermission) {
            // Create ImageCapture
            val capture = remember {
                ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
            }
            
            imageCapture = capture
            
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                imageCapture = capture
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Camera permission needed", color = Color.White)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            )
            Button(
                onClick = {
                    val capture = imageCapture
                    val controller = cameraController
                    if (capture != null && controller != null) {
                        val imageFile = controller.createImageFile(context)
                        controller.takePicture(
                            outputFile = imageFile,
                            onImageSaved = { _ ->
                                onCapture(imageFile)
                            },
                            onError = { exception ->
                                exception.printStackTrace()
                                // Handle error - could show error message
                            }
                        )
                    }
                },
                modifier = Modifier.padding(bottom = 48.dp).size(80.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.size(60.dp).background(MaterialTheme.colorScheme.secondary, CircleShape))
            }
        }
    }
}

// --- 3. ANALYZING VIEW ---
@Composable
fun AnalyzingView(
    imageFile: File?,
                    onFinished: (SkinAnalysisResult) -> Unit
) {
    val context = LocalContext.current
    val skinAnalyzer = remember { SkinAnalyzer() }
    
    LaunchedEffect(imageFile) {
        if (imageFile != null && imageFile.exists()) {
            // Analyze skin using SkinAnalyzer
            val result = withContext(Dispatchers.IO) {
                skinAnalyzer.analyzeSkin(imageFile)
            }
            onFinished(result)
        } else {
            // If no image file, use default result
            delay(1500) // Small delay untuk UX
            onFinished(
                SkinAnalysisResult(
                    score = 75,
                    skinType = "Normal",
                    acnePercentage = 10,
                    dryPercentage = 15,
                    recommendation = "Lakukan scan ulang dengan pencahayaan yang lebih baik"
                )
            )
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            skinAnalyzer.close()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Analyzing Skin Texture...", color = Color.White)
        }
    }
}

// --- 4. RESULT VIEW ---
@Composable
fun ResultView(data: ScanResult, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.weight(0.4f).fillMaxWidth().background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(140.dp)) {
                        drawCircle(color = Color.White.copy(alpha = 0.2f), style = Stroke(width = 20f))
                        val angle = (data.score / 100f) * 360f
                        drawArc(
                            color = Color(0xFFE9C46A),
                            startAngle = -90f,
                            sweepAngle = angle,
                            useCenter = false,
                            style = Stroke(width = 20f, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${data.score}%", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("Score", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("${data.skinType} Skin", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                Text("Detected ${data.acnePercentage}% Acne Prone areas", color = Color.White.copy(alpha = 0.7f))
            }
        }

        Box(
            modifier = Modifier.weight(0.6f).fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Text("Analysis Results", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    TreatmentStep(1, data.recommendation)
                    TreatmentStep(2, "Detected ${data.dryPercentage}% Dryness")
                    TreatmentStep(3, "Use SPF 50 Daily")
                }
                item {
                    Text("Top Product Picks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(3) { ProductCardPlaceholder() }
                    }
                }
            }
        }
    }
}

// Helper Composables
@Composable
fun TreatmentStep(number: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(
            modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.secondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(number.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, color = Color.DarkGray)
    }
}

@Composable
fun ProductCardPlaceholder() {
    Card(
        modifier = Modifier.size(100.dp, 120.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.LightGray, RoundedCornerShape(8.dp)))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Serum", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
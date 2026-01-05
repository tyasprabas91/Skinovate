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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.skinovate.data.ScanResult
import com.example.skinovate.data.UserRepository
import com.example.skinovate.screen.components.CameraController
import com.example.skinovate.screen.components.CameraPreview
import com.example.skinovate.screen.components.SkinAnalyzer
import com.example.skinovate.ui.components.EmptyScanHistoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import androidx.camera.core.ImageCapture

enum class ScanState { HISTORY, CAMERA_PREVIEW, ANALYZING, RESULT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceAnalysisScreen(navController: NavController) {
    val context = LocalContext.current
    var currentState by remember { mutableStateOf(ScanState.HISTORY) }
    var currentResult by remember { mutableStateOf<ScanResult?>(null) }
    var capturedImageFile by remember { mutableStateOf<File?>(null) }

    // Load History
    val historyList by UserRepository.getAllScans(context).collectAsState(initial = emptyList())
    LaunchedEffect(Unit) { UserRepository.init(context) }

    Scaffold(
        containerColor = if (currentState == ScanState.RESULT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
        topBar = {
            if (currentState != ScanState.RESULT) {
                CenterAlignedTopAppBar(
                    title = { Text("Face Analysis", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentState == ScanState.HISTORY) navController.popBackStack()
                            else currentState = ScanState.HISTORY
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (currentState) {
                ScanState.HISTORY -> HistoryView(
                    historyItems = historyList.map { it.toScanResult() },
                    onStartScan = { currentState = ScanState.CAMERA_PREVIEW },
                    onItemClick = { scan ->
                        currentResult = scan
                        currentState = ScanState.RESULT
                    }
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
                        val newResult = ScanResult(
                            score = result.score,
                            skinType = result.skinType,
                            acnePercentage = result.acnePercentage,
                            dryPercentage = result.dryPercentage,
                            recommendation = result.recommendation,
                            tips = result.tips
                        )
                        currentResult = newResult
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

// --- CAMERA VIEW (UPDATED) ---
@Composable
fun CameraView(onCapture: (File) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = ContextCompat.getMainExecutor(context)
    var hasPermission by remember { mutableStateOf(false) }

    // We keep track of the controller and the capture use case
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraController by remember { mutableStateOf<CameraController?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val controller = CameraController(context, lifecycleOwner, executor)
            cameraController = controller
            controller.initialize { }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasPermission) {
            // 1. Create the ImageCapture use case
            val capture = remember {
                ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
            }
            imageCapture = capture

            // 2. Pass it to the Preview (so it gets bound to lifecycle)
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

            // 3. The Button
            Button(
                onClick = {
                    val capture = imageCapture
                    val controller = cameraController

                    if (capture != null && controller != null) {
                        val imageFile = controller.createImageFile(context)
                        // FIX: Pass the 'capture' object directly!
                        controller.takePicture(
                            imageCapture = capture,
                            outputFile = imageFile,
                            onImageSaved = { onCapture(imageFile) },
                            onError = { it.printStackTrace() }
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

// ... (Rest of your Views: HistoryView, AnalyzingView, ResultView remain the same) ...

@Composable
fun AnalyzingView(imageFile: File?, onFinished: (com.example.skinovate.data.SkinAnalysisResult) -> Unit) {
    val context = LocalContext.current
    val skinAnalyzer = remember { SkinAnalyzer() }

    LaunchedEffect(imageFile) {
        if (imageFile != null && imageFile.exists()) {
            val result = withContext(Dispatchers.IO) {
                skinAnalyzer.analyzeSkin(imageFile)
            }
            try { imageFile.delete() } catch (e: Exception) { e.printStackTrace() }
            onFinished(result)
        } else {
            delay(1500)
            // Fallback logic
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Analyzing Skin Texture...", color = Color.White)
        }
    }
}

@Composable
fun HistoryView(
    historyItems: List<ScanResult>,
    onStartScan: () -> Unit,
    onItemClick: (ScanResult) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().height(100.dp).clickable { onStartScan() }
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Face, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Start New Scan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (historyItems.isNotEmpty()) {
            Text("Skin Health Journey", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            ProgressChart(historyItems) // Ensure you have this function from previous steps
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text("History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (historyItems.isEmpty()) {
            EmptyScanHistoryState(onStartScan = onStartScan, modifier = Modifier.fillMaxWidth())
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(historyItems) { scan ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().clickable { onItemClick(scan) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(scan.date, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(scan.skinType, fontWeight = FontWeight.Bold)
                            }
                            Surface(
                                color = if (scan.score > 80) Color(0xFFE6F4EA) else Color(0xFFFFF0E0),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "${scan.score}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = if (scan.score > 80) Color(0xFF1E8E3E) else Color(0xFFE8710A)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultView(data: ScanResult, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.weight(0.35f).fillMaxWidth().background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${data.score}", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                Text("Overall Health", color = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(8.dp))
                Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)) {
                    Text(data.skinType, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }
        }

        Box(
            modifier = Modifier.weight(0.65f).fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White)
        ) {
            LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                item {
                    Text("Personalized Tips", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (data.tips.isNotEmpty()) {
                        data.tips.forEachIndexed { index, tip ->
                            TreatmentStep(index + 1, tip)
                        }
                    } else {
                        TreatmentStep(1, data.recommendation)
                    }
                }
            }
        }
    }
}

// Reuse your existing Helper Composables (TreatmentStep, ProgressChart, etc.)
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
fun ProgressChart(history: List<ScanResult>) {
    val recentHistory = history.take(7).reversed()
    if (recentHistory.size < 2) return

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().height(150.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height
            val maxScore = 100f
            val path = Path()
            recentHistory.forEachIndexed { index, scan ->
                val x = (width / (recentHistory.size - 1)) * index
                val y = height - ((scan.score / maxScore) * height)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                drawCircle(color = Color(0xFF6200EE), radius = 8f, center = Offset(x, y))
            }
            drawPath(path, color = Color(0xFF6200EE), style = Stroke(width = 5f, cap = StrokeCap.Round))
        }
    }
}
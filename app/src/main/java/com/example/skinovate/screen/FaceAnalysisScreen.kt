package com.example.skinovate.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// The 4 States of this Screen
enum class ScanState { HISTORY, CAMERA_PREVIEW, ANALYZING, RESULT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceAnalysisScreen(navController: NavController) {
    var currentState by remember { mutableStateOf(ScanState.HISTORY) }

    // Mock Data for History
    val historyItems = listOf("Dec 28 - Oily", "Dec 20 - Dry", "Dec 12 - Normal")

    Scaffold(
        containerColor = if (currentState == ScanState.RESULT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
        topBar = {
            if (currentState != ScanState.RESULT) {
                CenterAlignedTopAppBar(
                    title = { Text("Face Analysis", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        if (currentState != ScanState.HISTORY) {
                            IconButton(onClick = { currentState = ScanState.HISTORY }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
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
                ScanState.CAMERA_PREVIEW -> CameraMockView(
                    onCapture = { currentState = ScanState.ANALYZING }
                )
                ScanState.ANALYZING -> AnalyzingView(
                    onFinished = { currentState = ScanState.RESULT }
                )
                ScanState.RESULT -> ResultView(
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
        // The Big "New Scan" Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().height(150.dp),
            onClick = onStartScan
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

        // History List
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

// --- 2. CAMERA MOCK VIEW ---
@Composable
fun CameraMockView(onCapture: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Fake Camera Feed (Grey Box)
        Box(modifier = Modifier.fillMaxSize().padding(20.dp).clip(RoundedCornerShape(20.dp)).background(Color.DarkGray))

        // Overlay Layout
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // The Face Outline
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            )

            // Capture Button
            Button(
                onClick = onCapture,
                modifier = Modifier.padding(bottom = 48.dp).size(80.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.size(60.dp).background(MaterialTheme.colorScheme.secondary, CircleShape))
            }
        }
    }
}

// --- 3. ANALYZING VIEW (Fake Scanning) ---
@Composable
fun AnalyzingView(onFinished: () -> Unit) {
    // Auto-advance after 2 seconds
    LaunchedEffect(Unit) {
        delay(2500)
        onFinished()
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

// --- 4. RESULT VIEW (The Glow Up!) ---
@Composable
fun ResultView(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {

        // TOP SECTION: The Score (Dark Background)
        Box(
            modifier = Modifier
                .weight(0.4f) // Takes top 40% of screen
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            // Close Button
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // The Score Circle
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(140.dp)) {
                        drawCircle(color = Color.White.copy(alpha = 0.2f), style = Stroke(width = 20f))
                        drawArc(
                            color = Color(0xFFE9C46A), // Gold Accent
                            startAngle = -90f,
                            sweepAngle = 240f, // 67% filled
                            useCenter = false,
                            style = Stroke(width = 20f, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("67%", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("Score", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Diagnosis Text
                Text("Oily & Sensitive", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                Text("Detected 17% Acne Prone areas", color = Color.White.copy(alpha = 0.7f))
            }
        }

        // BOTTOM SECTION: Recommendations (White Sheet)
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Text("Recommended Plan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    TreatmentStep(1, "Use Salicylic Acid Cleanser (AM/PM)")
                    TreatmentStep(2, "Apply Niacinamide Serum")
                    TreatmentStep(3, "Use Oil-Free Moisturizer")
                }

                item {
                    Text("Top Product Picks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(3) {
                            ProductCardPlaceholder()
                        }
                    }
                }
            }
        }
    }
}

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
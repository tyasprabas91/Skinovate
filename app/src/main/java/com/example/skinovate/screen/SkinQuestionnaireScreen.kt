package com.example.skinovate.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.example.skinovate.data.SkinQuestionnaireResult
import com.example.skinovate.data.SkinQuestionnaireRepository
import com.example.skinovate.data.UserRepository
import com.example.skinovate.data.RoutineRecommendation
import com.example.skinovate.data.RoutineRecommendationRepository
import com.example.skinovate.data.RoutineRepository
import com.example.skinovate.navigation.Screen
import com.example.skinovate.ai.SkinAnalysisAiService
import kotlinx.coroutines.launch

/**
 * Question data class untuk questionnaire
 */
data class Question(
    val id: String,
    val category: String, // skin_type, sensitivity, elasticity, acne_tendency, skin_color, texture
    val question: String,
    val options: List<String>
)

/**
 * Screen untuk questionnaire analisis kulit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinQuestionnaireScreen(navController: NavController) {
    val context = LocalContext.current
    val apiKey = context.getString(com.example.skinovate.R.string.groq_api_key)
    val aiService = remember { SkinAnalysisAiService(apiKey) }
    val scope = rememberCoroutineScope()
    
    val questions = remember { generateQuestions() }
    val answers = remember { mutableStateMapOf<String, String>() }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<SkinQuestionnaireResult?>(null) }
    
    // AI Analysis states
    var aiAnalysis by remember { mutableStateOf<String?>(null) }
    var isLoadingAI by remember { mutableStateOf(false) }
    var aiError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Analisis Tipe Kulit",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (showResult && result != null) {
                val recommendedRoutine = remember(result) {
                    RoutineRecommendationRepository.getRecommendationForAnalysis(
                        skinType = result!!.skinType,
                        sensitivity = result!!.sensitivity,
                        acneTendency = result!!.acneTendency,
                        texture = result!!.texture
                    )
                }
                
                // Generate AI analysis when result is available
                LaunchedEffect(result) {
                    if (result != null && aiAnalysis == null && !isLoadingAI) {
                        isLoadingAI = true
                        aiError = null
                        
                        scope.launch {
                            try {
                                val analysis = aiService.generateFullAnalysis(result!!)
                                analysis.onSuccess { analysisText ->
                                    aiAnalysis = analysisText
                                }.onFailure { exception ->
                                    aiError = "Gagal memuat analisis AI. Menggunakan rekomendasi standar."
                                    // Continue with fallback - no need to show error to user
                                }
                            } catch (e: Exception) {
                                aiError = "Gagal memuat analisis AI. Menggunakan rekomendasi standar."
                            } finally {
                                isLoadingAI = false
                            }
                        }
                    }
                }
                
                ResultView(
                    result = result!!,
                    recommendedRoutine = recommendedRoutine,
                    aiAnalysis = aiAnalysis,
                    isLoadingAI = isLoadingAI,
                    onBack = { navController.popBackStack() },
                    onSave = { applyRecommendation ->
                        SkinQuestionnaireRepository.saveResult(result!!)
                        // Convert to ScanResult for compatibility with existing system
                        // Use AI analysis as recommendation if available, otherwise use hardcoded
                        val recommendationText = aiAnalysis ?: result!!.recommendation
                        val scanResult = com.example.skinovate.data.ScanResult(
                            score = calculateScore(result!!),
                            skinType = result!!.skinType,
                            acnePercentage = when (result!!.acneTendency) {
                                "none" -> 0
                                "low" -> 15
                                "medium" -> 30
                                "high" -> 50
                                else -> 0
                            },
                            dryPercentage = when (result!!.skinType) {
                                "dry" -> 40
                                "combination" -> 20
                                else -> 10
                            },
                            recommendation = recommendationText,
                            date = result!!.date
                        )
                        UserRepository.saveScan(scanResult, context)
                        
                        // Apply recommendation if user wants to
                        if (applyRecommendation) {
                            RoutineRepository.applyRecommendation(
                                recommendedRoutine.morningRoutine,
                                recommendedRoutine.eveningRoutine,
                                context
                            )
                        }
                        
                        // Navigate to Analisis screen to show result
                        navController.navigate(com.example.skinovate.navigation.Screen.HistoryAnalysis.route) {
                            popUpTo(com.example.skinovate.navigation.Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            } else {
                QuestionView(
                    questions = questions,
                    currentIndex = currentQuestionIndex,
                    answers = answers,
                    onAnswerSelected = { questionId, answer ->
                        answers[questionId] = answer
                    },
                    onNext = {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                        } else {
                            // Calculate result
                            result = calculateResult(answers, questions)
                            showResult = true
                        }
                    },
                    onPrevious = {
                        if (currentQuestionIndex > 0) {
                            currentQuestionIndex--
                        }
                    },
                    canGoNext = { answers[questions[currentQuestionIndex].id] != null }
                )
            }
        }
    }
}

@Composable
fun QuestionView(
    questions: List<Question>,
    currentIndex: Int,
    answers: Map<String, String>,
    onAnswerSelected: (String, String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    canGoNext: () -> Boolean
) {
    val question = questions[currentIndex]
    val selectedAnswer = answers[question.id]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Progress indicator
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pertanyaan ${currentIndex + 1} dari ${questions.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            // Category label
            Text(
                text = getCategoryLabel(question.category),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Question
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        itemsIndexed(question.options) { index, option ->
            OptionCard(
                option = option,
                isSelected = selectedAnswer == option,
                onClick = { onAnswerSelected(question.id, option) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentIndex > 0) {
                    OutlinedButton(onClick = onPrevious) {
                        Text("Sebelumnya")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                
                Button(
                    onClick = onNext,
                    enabled = canGoNext()
                ) {
                    Text(if (currentIndex < questions.size - 1) "Selanjutnya" else "Lihat Hasil")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun OptionCard(
    option: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) null else 
            androidx.compose.foundation.BorderStroke(
                1.dp, 
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = option,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) 
                    Color.White 
                else 
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ResultView(
    result: SkinQuestionnaireResult,
    recommendedRoutine: RoutineRecommendation,
    aiAnalysis: String?,
    isLoadingAI: Boolean,
    onBack: () -> Unit,
    onSave: (applyRecommendation: Boolean) -> Unit
) {
    var showApplyDialog by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Hasil Analisis Kulit Anda",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tanggal: ${result.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                ResultCard(title = "Tipe Kulit", value = result.skinType.capitalizeWords())
            }
            item {
                ResultCard(title = "Tingkat Sensitivitas", value = result.sensitivity.capitalizeWords())
            }
            item {
                ResultCard(title = "Tingkat Elastisitas", value = result.elasticity.capitalizeWords())
            }
            item {
                ResultCard(title = "Kecenderungan Jerawat", value = result.acneTendency.capitalizeWords())
            }
            item {
                ResultCard(title = "Warna Kulit", value = result.skinColor.capitalizeWords())
            }
            item {
                ResultCard(title = "Tekstur Kulit", value = result.texture.capitalizeWords())
            }

            // AI Analysis Section (if available or loading)
            if (isLoadingAI || aiAnalysis != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "🤖 Analisis Personalisasi AI",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                if (isLoadingAI) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            if (isLoadingAI) {
                                Text(
                                    text = "Sedang menganalisis hasil questionnaire Anda...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            } else if (aiAnalysis != null) {
                                Text(
                                    text = aiAnalysis,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    lineHeight = TextUnit(22f, TextUnitType.Sp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Fallback Recommendation (if AI not available)
            if (!isLoadingAI && aiAnalysis == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Rekomendasi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = result.recommendation,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
            
            // Recommended Routine Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rekomendasi Rutinitas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        
            item {
                Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = recommendedRoutine.icon,
                            fontSize = 32.sp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = recommendedRoutine.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = recommendedRoutine.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Cocok untuk:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recommendedRoutine.suitableFor.take(3).joinToString(separator = "\n") { "• $it" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showApplyDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Terapkan Rekomendasi & Simpan", style = MaterialTheme.typography.bodyLarge)
                }
            }
            
            item {
                OutlinedButton(
                    onClick = { onSave(false) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Simpan Hasil Saja", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        
        // Dialog for confirmation
        if (showApplyDialog) {
            AlertDialog(
                onDismissRequest = { showApplyDialog = false },
                title = { Text("Terapkan Rekomendasi?") },
                text = {
                    Text("Rekomendasi rutinitas akan diterapkan ke routine Anda. Anda dapat mengubahnya nanti.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showApplyDialog = false
                            onSave(true)
                        }
                    ) {
                        Text("Terapkan", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showApplyDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun ResultCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Helper functions
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase() else it.toString() 
        }
    }
}

fun getCategoryLabel(category: String): String {
    return when (category) {
        "skin_type" -> "Tipe Kulit"
        "sensitivity" -> "Sensitivitas"
        "elasticity" -> "Elastisitas"
        "acne_tendency" -> "Kecenderungan Jerawat"
        "skin_color" -> "Warna Kulit"
        "texture" -> "Tekstur Kulit"
        else -> ""
    }
}

fun generateQuestions(): List<Question> {
    return listOf(
        // Tipe Kulit
        Question(
            id = "q1",
            category = "skin_type",
            question = "Bagaimana kondisi kulit wajah Anda setelah mencuci muka?",
            options = listOf(
                "Terasa kencang dan kering",
                "Terasa nyaman dan seimbang",
                "Terasa berminyak di beberapa area",
                "Terasa berminyak di seluruh wajah",
                "Terkadang kering, terkadang berminyak"
            )
        ),
        Question(
            id = "q2",
            category = "skin_type",
            question = "Bagaimana kondisi kulit Anda di tengah hari?",
            options = listOf(
                "Terasa kering dan kusam",
                "Terasa normal dan segar",
                "Terlihat sedikit berminyak di area T-zone",
                "Terlihat sangat berminyak di seluruh wajah",
                "Bervariasi tergantung cuaca"
            )
        ),

        // Sensitivitas
        Question(
            id = "q3",
            category = "sensitivity",
            question = "Bagaimana reaksi kulit Anda terhadap produk baru?",
            options = listOf(
                "Sangat jarang mengalami iritasi",
                "Kadang mengalami iritasi ringan",
                "Sering mengalami kemerahan atau gatal",
                "Sangat mudah mengalami reaksi alergi"
            )
        ),
        Question(
            id = "q4",
            category = "sensitivity",
            question = "Apakah kulit Anda mudah memerah?",
            options = listOf(
                "Jarang sekali",
                "Kadang-kadang",
                "Sering, terutama setelah aktivitas",
                "Sangat mudah memerah"
            )
        ),

        // Elastisitas
        Question(
            id = "q5",
            category = "elasticity",
            question = "Bagaimana kondisi elastisitas kulit wajah Anda?",
            options = listOf(
                "Sangat elastis dan kencang",
                "Elastis dengan baik",
                "Kurang elastis, mulai terlihat kendur",
                "Sangat kurang elastis"
            )
        ),

        // Kecenderungan Jerawat
        Question(
            id = "q6",
            category = "acne_tendency",
            question = "Seberapa sering Anda mengalami jerawat?",
            options = listOf(
                "Hampir tidak pernah",
                "Kadang-kadang, 1-2 jerawat",
                "Sering, beberapa jerawat sekaligus",
                "Sangat sering, banyak jerawat"
            )
        ),
        Question(
            id = "q7",
            category = "acne_tendency",
            question = "Jenis jerawat apa yang paling sering muncul?",
            options = listOf(
                "Tidak ada jerawat",
                "Blackhead dan whitehead",
                "Jerawat merah kecil (papules)",
                "Jerawat besar dan meradang"
            )
        ),

        // Warna Kulit
        Question(
            id = "q8",
            category = "skin_color",
            question = "Bagaimana warna kulit wajah Anda?",
            options = listOf(
                "Sangat terang / Fair",
                "Sedang / Medium",
                "Zaitun / Olive",
                "Sawo matang / Tan",
                "Gelap / Dark"
            )
        ),

        // Tekstur
        Question(
            id = "q9",
            category = "texture",
            question = "Bagaimana tekstur permukaan kulit wajah Anda?",
            options = listOf(
                "Halus dan rata",
                "Sedikit kasar di beberapa area",
                "Tidak rata, ada area yang kasar",
                "Kombinasi halus dan kasar"
            )
        ),
        Question(
            id = "q10",
            category = "texture",
            question = "Apakah ada masalah tekstur yang Anda alami?",
            options = listOf(
                "Tidak ada masalah",
                "Pori-pori terlihat besar",
                "Ada tekstur bergelombang",
                "Banyak tekstur tidak rata"
            )
        )
    )
}

fun calculateResult(
    answers: Map<String, String>,
    questions: List<Question>
): SkinQuestionnaireResult {
    // Group answers by category
    val skinTypeAnswers = questions
        .filter { it.category == "skin_type" }
        .mapNotNull { answers[it.id] }
    
    val sensitivityAnswers = questions
        .filter { it.category == "sensitivity" }
        .mapNotNull { answers[it.id] }
    
    val elasticityAnswers = questions
        .filter { it.category == "elasticity" }
        .mapNotNull { answers[it.id] }
    
    val acneAnswers = questions
        .filter { it.category == "acne_tendency" }
        .mapNotNull { answers[it.id] }
    
    val skinColorAnswers = questions
        .filter { it.category == "skin_color" }
        .mapNotNull { answers[it.id] }
    
    val textureAnswers = questions
        .filter { it.category == "texture" }
        .mapNotNull { answers[it.id] }

    // Calculate skin type
    val skinType = determineSkinType(skinTypeAnswers)
    
    // Calculate sensitivity
    val sensitivity = determineSensitivity(sensitivityAnswers)
    
    // Calculate elasticity
    val elasticity = determineElasticity(elasticityAnswers)
    
    // Calculate acne tendency
    val acneTendency = determineAcneTendency(acneAnswers)
    
    // Determine skin color (take first answer)
    val skinColor = determineSkinColor(skinColorAnswers.firstOrNull() ?: "")
    
    // Determine texture (take first answer)
    val texture = determineTexture(textureAnswers.firstOrNull() ?: "")
    
    // Generate recommendation
    val recommendation = generateRecommendation(skinType, sensitivity, acneTendency)
    
    return SkinQuestionnaireResult(
        skinType = skinType,
        sensitivity = sensitivity,
        elasticity = elasticity,
        acneTendency = acneTendency,
        skinColor = skinColor,
        texture = texture,
        recommendation = recommendation
    )
}

fun determineSkinType(answers: List<String>): String {
    if (answers.isEmpty()) return "normal"
    
    val dryCount = answers.count { it.contains("kering", ignoreCase = true) || it.contains("kencang", ignoreCase = true) }
    val oilyCount = answers.count { it.contains("berminyak", ignoreCase = true) }
    val combinationCount = answers.count { it.contains("beberapa area", ignoreCase = true) || it.contains("T-zone", ignoreCase = true) }
    val normalCount = answers.count { it.contains("normal", ignoreCase = true) || it.contains("seimbang", ignoreCase = true) }
    
    return when {
        combinationCount > 0 || (dryCount > 0 && oilyCount > 0) -> "combination"
        oilyCount > dryCount && oilyCount > normalCount -> "oily"
        dryCount > oilyCount && dryCount > normalCount -> "dry"
        normalCount > 0 -> "normal"
        else -> "combination"
    }
}

fun determineSensitivity(answers: List<String>): String {
    if (answers.isEmpty()) return "medium"
    
    val highCount = answers.count { it.contains("sangat", ignoreCase = true) || it.contains("mudah", ignoreCase = true) || it.contains("sering", ignoreCase = true) }
    val lowCount = answers.count { it.contains("jarang", ignoreCase = true) || it.contains("tidak", ignoreCase = true) }
    
    return when {
        highCount >= 2 -> "high"
        lowCount >= 1 -> "low"
        else -> "medium"
    }
}

fun determineElasticity(answers: List<String>): String {
    if (answers.isEmpty()) return "medium"
    
    val answer = answers.firstOrNull() ?: ""
    return when {
        answer.contains("sangat", ignoreCase = true) && answer.contains("elastis", ignoreCase = true) -> "high"
        answer.contains("kurang", ignoreCase = true) || answer.contains("kendur", ignoreCase = true) -> "low"
        else -> "medium"
    }
}

fun determineAcneTendency(answers: List<String>): String {
    if (answers.isEmpty()) return "none"
    
    val noneCount = answers.count { it.contains("tidak", ignoreCase = true) || it.contains("jarang", ignoreCase = true) || it.contains("hampir tidak", ignoreCase = true) }
    val highCount = answers.count { it.contains("sangat", ignoreCase = true) || it.contains("banyak", ignoreCase = true) }
    val mediumCount = answers.count { it.contains("sering", ignoreCase = true) || it.contains("beberapa", ignoreCase = true) }
    
    return when {
        noneCount > 0 -> "none"
        highCount > 0 -> "high"
        mediumCount > 0 -> "medium"
        else -> "low"
    }
}

fun determineSkinColor(answer: String): String {
    return when {
        answer.contains("terang", ignoreCase = true) || answer.contains("fair", ignoreCase = true) -> "fair"
        answer.contains("sedang", ignoreCase = true) || answer.contains("medium", ignoreCase = true) -> "medium"
        answer.contains("zaitun", ignoreCase = true) || answer.contains("olive", ignoreCase = true) -> "olive"
        answer.contains("sawo", ignoreCase = true) || answer.contains("tan", ignoreCase = true) -> "tan"
        answer.contains("gelap", ignoreCase = true) || answer.contains("dark", ignoreCase = true) -> "dark"
        else -> "medium"
    }
}

fun determineTexture(answer: String): String {
    return when {
        answer.contains("halus", ignoreCase = true) && answer.contains("rata", ignoreCase = true) -> "smooth"
        answer.contains("kasar", ignoreCase = true) && answer.contains("tidak rata", ignoreCase = true) -> "rough"
        answer.contains("tidak rata", ignoreCase = true) || answer.contains("bergelombang", ignoreCase = true) -> "uneven"
        answer.contains("kombinasi", ignoreCase = true) -> "combination"
        else -> "smooth"
    }
}

fun generateRecommendation(skinType: String, sensitivity: String, acneTendency: String): String {
    val recommendations = mutableListOf<String>()
    
    when (skinType) {
        "oily" -> recommendations.add("Gunakan produk non-comedogenic dan oil-free. Pembersih dengan salicylic acid dapat membantu mengontrol minyak.")
        "dry" -> recommendations.add("Gunakan produk yang mengandung hyaluronic acid dan ceramides untuk melembapkan. Hindari produk yang mengandung alkohol.")
        "combination" -> recommendations.add("Gunakan produk yang seimbang. Pertimbangkan double cleansing dan gunakan moisturizer ringan.")
        "sensitive" -> recommendations.add("Hindari produk dengan fragrance dan alkohol. Pilih produk yang hypoallergenic dan gentle.")
        else -> recommendations.add("Pertahankan rutinitas skincare yang konsisten dengan produk yang cocok untuk kulit normal.")
    }
    
    if (sensitivity == "high") {
        recommendations.add("Lakukan patch test sebelum menggunakan produk baru. Hindari produk dengan kandungan aktif yang kuat.")
    }
    
    when (acneTendency) {
        "high", "medium" -> recommendations.add("Pertimbangkan produk dengan benzoyl peroxide atau salicylic acid. Konsultasi dengan dermatologis jika jerawat parah.")
        "low" -> recommendations.add("Jaga kebersihan wajah dan hindari menyentuh wajah dengan tangan kotor.")
    }
    
    return recommendations.joinToString(" ")
}

fun calculateScore(result: SkinQuestionnaireResult): Int {
    var score = 75 // Base score
    
    // Adjust based on skin type
    when (result.skinType) {
        "normal" -> score += 10
        "combination" -> score += 5
        else -> score -= 5
    }
    
    // Adjust based on sensitivity
    when (result.sensitivity) {
        "low" -> score += 10
        "medium" -> score += 5
        "high" -> score -= 10
    }
    
    // Adjust based on acne tendency
    when (result.acneTendency) {
        "none" -> score += 10
        "low" -> score += 5
        "high" -> score -= 15
    }
    
    // Adjust based on elasticity
    when (result.elasticity) {
        "high" -> score += 5
        "medium" -> score += 0
        "low" -> score -= 10
    }
    
    return score.coerceIn(0, 100)
}


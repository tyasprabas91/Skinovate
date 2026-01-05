package com.example.skinovate.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.skinovate.data.RoutineRecommendation
import com.example.skinovate.data.RoutineRecommendationRepository
import com.example.skinovate.data.RoutineRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineRecommendationScreen(navController: NavController) {
    val context = LocalContext.current
    val recommendations = remember { 
        try {
            RoutineRecommendationRepository.getAllRecommendations()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList<RoutineRecommendation>()
        }
    }
    var showApplyDialog by remember { mutableStateOf<RoutineRecommendation?>(null) }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Rekomendasi Rutinitas",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Pilih Rekomendasi Rutinitas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pilih salah satu rekomendasi rutinitas yang sesuai dengan kebutuhan kulit Anda. Rutinitas akan otomatis diterapkan ke Routine Maker.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Recommendations List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recommendations) { recommendation ->
                    RecommendationCard(
                        recommendation = recommendation,
                        onClick = {
                            showApplyDialog = recommendation
                        }
                    )
                }
            }
        }
    }
    
    // Apply Dialog
    showApplyDialog?.let { recommendation ->
        AlertDialog(
            onDismissRequest = { showApplyDialog = null },
            title = {
                Text(
                    text = "Terapkan Rekomendasi?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Rekomendasi \"${recommendation.title}\" akan diterapkan ke rutinitas pagi dan malam Anda.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rutinitas yang ada akan diganti. Apakah Anda yakin?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            RoutineRepository.applyRecommendation(
                                recommendation.morningRoutine,
                                recommendation.eveningRoutine,
                                context
                            )
                            showApplyDialog = null
                            navController.navigate(com.example.skinovate.navigation.Screen.RoutineMaker.route) {
                                popUpTo(com.example.skinovate.navigation.Screen.RoutineRecommendation.route) {
                                    inclusive = true
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showApplyDialog = null
                            // Still navigate even if save fails
                            navController.navigate(com.example.skinovate.navigation.Screen.RoutineMaker.route) {
                                popUpTo(com.example.skinovate.navigation.Screen.RoutineRecommendation.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                ) {
                    Text("Terapkan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApplyDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun RecommendationCard(
    recommendation: RoutineRecommendation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = recommendation.icon,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Column {
                        Text(
                            text = recommendation.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = recommendation.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Apply",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Benefits
            Text(
                text = "Manfaat:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            recommendation.benefits.forEach { benefit ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• ",
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = benefit,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Suitable For
            Text(
                text = "Cocok untuk:",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recommendation.suitableFor.joinToString(", "),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Routine Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RoutineSummaryItem(
                    label = "Pagi",
                    stepCount = recommendation.morningRoutine.steps.size
                )
                RoutineSummaryItem(
                    label = "Malam",
                    stepCount = recommendation.eveningRoutine.steps.size
                )
            }
        }
    }
}

@Composable
fun RoutineSummaryItem(label: String, stepCount: Int) {
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "$stepCount langkah",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}


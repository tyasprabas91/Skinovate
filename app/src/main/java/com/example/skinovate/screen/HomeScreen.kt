package com.example.skinovate.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skinovate.navigation.Screen

@Composable
fun SkinovateHomeScreen(navController: NavController) {
    // Note: No Scaffold here! It's handled in SkinovateApp.kt
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Uses Theme Color
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HeaderSection(username = "Username")
        RoutineSection(navController)
        FaceAnalysisSection(navController)
        HighlightedProductsSection()
    }
}

@Composable
fun HeaderSection(username: String) {
    Column {
        Text(
            text = "Welcome,",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = username,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun RoutineSection(navController: NavController) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Routine",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary // Uses BrandPrimary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Column {
                    Text(
                        text = "Next Routine",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "08:00 A.M.",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Apply Skincare",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                TextButton(
                    onClick = { navController.navigate(Screen.RoutineMaker.route) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = (12).dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Edit Routine →", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun FaceAnalysisSection(navController: NavController) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Column {
                Text(
                    text = "Your face was",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "45% Oily",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("17% Acne Prone", color = Color.White.copy(alpha = 0.9f))
                Text("8% Dry Areas", color = Color.White.copy(alpha = 0.9f))
            }
            TextButton(
                onClick = { navController.navigate(Screen.FaceAnalysis.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(y = (12).dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Run another face scan →", color = Color.White)
            }
        }
    }
}

@Composable
fun HighlightedProductsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Highlighted Products",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(5) { ProductCardItem() }
        }
    }
}

@Composable
fun ProductCardItem() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(280.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Face Moisturizer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Brand Name",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary, // Uses BrandAccent
                        modifier = Modifier.size(16.dp)
                    )
                    Text(" 4.8 (120)", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
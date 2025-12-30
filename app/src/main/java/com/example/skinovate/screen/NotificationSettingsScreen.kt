package com.example.skinovate.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(navController: NavController) {
    var routineRemindersEnabled by remember { mutableStateOf(true) }
    var productRecommendationsEnabled by remember { mutableStateOf(true) }
    var scanRemindersEnabled by remember { mutableStateOf(false) }
    var weeklyReportEnabled by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Pengaturan Notifikasi",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Kelola notifikasi untuk tetap update dengan rutinitas skincaremu",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Notification Options
            NotificationOptionCard(
                title = "Pengingat Rutinitas",
                description = "Dapatkan notifikasi untuk rutinitas pagi dan malam",
                enabled = routineRemindersEnabled,
                onToggle = { routineRemindersEnabled = it }
            )

            NotificationOptionCard(
                title = "Rekomendasi Produk",
                description = "Notifikasi tentang produk baru yang cocok untuk kulitmu",
                enabled = productRecommendationsEnabled,
                onToggle = { productRecommendationsEnabled = it }
            )

            NotificationOptionCard(
                title = "Pengingat Scan",
                description = "Ingatkan untuk melakukan face analysis secara berkala",
                enabled = scanRemindersEnabled,
                onToggle = { scanRemindersEnabled = it }
            )

            NotificationOptionCard(
                title = "Laporan Mingguan",
                description = "Ringkasan progress skincaremu setiap minggu",
                enabled = weeklyReportEnabled,
                onToggle = { weeklyReportEnabled = it }
            )
        }
    }
}

@Composable
fun NotificationOptionCard(
    title: String,
    description: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}


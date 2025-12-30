package com.example.skinovate.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skinovate.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturesScreen(navController: NavController) {
    val features = listOf(
        FeatureItem(
            title = "Face Analysis",
            description = "Analisis kondisi kulitmu dengan teknologi AI. Deteksi tipe kulit, masalah kulit, dan dapatkan rekomendasi personal.",
            icon = Icons.Default.Face,
            color = Color(0xFF264653),
            route = Screen.FaceAnalysis.route
        ),
        FeatureItem(
            title = "Routine Maker",
            description = "Buat rutinitas skincare pagi dan malam yang sesuai dengan kebutuhan kulitmu. Atur waktu dan produk dengan mudah.",
            icon = Icons.Default.List,
            color = Color(0xFF2A9D8F),
            route = Screen.RoutineMaker.route
        ),
        FeatureItem(
            title = "Product Recommendations",
            description = "Dapatkan rekomendasi produk skincare yang tepat berdasarkan hasil analisis kulitmu. Temukan produk terbaik untuk masalah kulitmu.",
            icon = Icons.Default.ShoppingCart,
            color = Color(0xFFE9C46A),
            route = Screen.Products.route
        ),
        FeatureItem(
            title = "Skin Type Detection",
            description = "Ketahui tipe kulitmu dengan akurat. Deteksi apakah kulitmu normal, berminyak, kering, kombinasi, atau sensitif.",
            icon = Icons.Default.Info,
            color = Color(0xFFF4A261),
            route = Screen.FaceAnalysis.route
        ),
        FeatureItem(
            title = "Progress Tracking",
            description = "Pantau perkembangan kulitmu dari waktu ke waktu. Lihat perbandingan hasil scan dan progress skincare routine.",
            icon = Icons.Default.Star,
            color = Color(0xFFE76F51),
            route = Screen.Home.route
        ),
        FeatureItem(
            title = "Personalized Tips",
            description = "Dapatkan tips dan saran skincare yang disesuaikan dengan kondisi kulitmu. Pelajari cara merawat kulit dengan benar.",
            icon = Icons.Default.Info,
            color = Color(0xFF6C757D),
            route = Screen.Home.route
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Fitur Aplikasi",
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Temukan Semua Fitur",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Jelajahi fitur-fitur canggih untuk merawat kulitmu",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(features) { feature ->
                FeatureCard(
                    feature = feature,
                    onClick = {
                        navController.navigate(feature.route) {
                            // Pop up to home if navigating to a nested screen
                            if (feature.route != Screen.Home.route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

data class FeatureItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@Composable
fun FeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                feature.color,
                                feature.color.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.title,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


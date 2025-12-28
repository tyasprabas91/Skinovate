package com.example.skinovate.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skinovate.data.Product
import com.example.skinovate.data.ProductRepository
import com.example.skinovate.data.ScanResult
import com.example.skinovate.data.UserRepository
import com.example.skinovate.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinovateHomeScreen(navController: NavController) {

    // 1. Get Data from Repository
    val userScan = UserRepository.lastScan
    // If we have a scan, filter products by that skin type. Otherwise show "All".
    val currentSkinCondition = userScan?.skinType ?: "All"

    val highlightedProducts = remember(currentSkinCondition) {
        ProductRepository.getRecommendations(currentSkinCondition)
    }

    // 2. Interaction State for Products
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HeaderSection(username = "User")
        RoutineSection(navController)

        // 3. Pass the scan data to the Face Section
        FaceAnalysisSection(navController, userScan)

        // 4. Pass click listener to Products
        HighlightedProductsSection(
            products = highlightedProducts,
            onProductClick = { product -> selectedProduct = product }
        )
    }

    // 5. Product Detail Sheet
    if (selectedProduct != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedProduct = null },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            ProductDetailContent(product = selectedProduct!!)
        }
    }
}

@Composable
fun HeaderSection(username: String) {
    Column {
        Text(
            text = "Good Morning,",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            text = username,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
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
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Screen.RoutineMaker.route) }
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
                Text(
                    text = "Edit Routine →",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = 12.dp)
                )
            }
        }
    }
}

@Composable
fun FaceAnalysisSection(navController: NavController, scanResult: ScanResult?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.FaceAnalysis.route) }
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            if (scanResult == null) {
                // STATE A: No Scan Yet
                Column {
                    Text(
                        text = "No Analysis Yet",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Start Scan",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Discover your skin type",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // STATE B: Result Exists
                Column {
                    Text(
                        text = "Last Scan: ${scanResult.date}",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${scanResult.score}% Score",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Type: ${scanResult.skinType}",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = scanResult.recommendation,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = if(scanResult == null) "Start ->" else "New Scan ->",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(y = 12.dp)
            )
        }
    }
}

@Composable
fun HighlightedProductsSection(products: List<Product>, onProductClick: (Product) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Recommended for You",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(products) { product ->
                ProductCardItem(product, onClick = { onProductClick(product) })
            }
        }
    }
}

@Composable
fun ProductCardItem(product: Product, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(280.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Placeholder Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(" $${product.price}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
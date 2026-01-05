package com.example.skinovate.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skinovate.data.Product
import com.example.skinovate.data.ProductRepository
import com.example.skinovate.auth.AuthRepository
import com.example.skinovate.data.ScanResult
import com.example.skinovate.data.UserRepository
import com.example.skinovate.data.RoutineRepository
import com.example.skinovate.data.Routine
import com.example.skinovate.navigation.Screen
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinovateHomeScreen(navController: NavController) {
    val context = LocalContext.current

    // 1. Get Data from Repository
    // Initialize UserRepository and observe lastScan
    LaunchedEffect(Unit) {
        UserRepository.init(context)
        RoutineRepository.init(context)
    }
    val userScan by UserRepository.lastScan.collectAsState()
    val currentUser by AuthRepository.currentUser.collectAsState()
    val username = currentUser?.name ?: "User"
    
    // Get routines
    val morningRoutine by RoutineRepository.morningRoutine.collectAsState()
    val eveningRoutine by RoutineRepository.eveningRoutine.collectAsState()

    // If we have a scan, filter products by that skin type. Otherwise show "All".
    val currentSkinCondition = userScan?.skinType ?: "All"

    // Get recommendations - if no method exists, just get all products
    val highlightedProducts = remember(currentSkinCondition) {
        if (currentSkinCondition == "All") {
            ProductRepository.allProducts.take(5)
        } else {
            // Filter products by skin condition if method doesn't exist
            ProductRepository.allProducts.filter { product ->
                product.targetSkinConditions.any {
                    it.contains(currentSkinCondition, ignoreCase = true)
                }
            }.take(5).ifEmpty {
                ProductRepository.allProducts.take(5)
            }
        }
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
        HeaderSection(username = username)
        RoutineSection(
            navController = navController,
            morningRoutine = morningRoutine,
            eveningRoutine = eveningRoutine
        )

        // 3. Pass the scan data to the Questionnaire Section
        SkinQuestionnaireSection(navController, userScan)
        
        // 4. Features Section
        FeaturesSection(navController)

        // 5. Pass click listener to Products
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
            ProductDetailContent(
                product = selectedProduct!!,
                navController = navController,
                onDismiss = { selectedProduct = null }
            )
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

/**
 * Parse time string seperti "08:00 AM" ke Calendar untuk hari ini
 */
private fun parseTimeForToday(timeString: String): Calendar {
    val calendar = Calendar.getInstance()
    try {
        val parts = timeString.split(" ")
        val timePart = parts[0] // "08:00"
        val amPm = if (parts.size > 1) parts[1] else "AM" // "AM" or "PM"
        
        val timeParts = timePart.split(":")
        var hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        
        // Convert to 24-hour format
        if (amPm.equals("PM", ignoreCase = true) && hour != 12) {
            hour += 12
        } else if (amPm.equals("AM", ignoreCase = true) && hour == 12) {
            hour = 0
        }
        
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    } catch (e: Exception) {
        // Default to 8:00 AM if parsing fails
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }
    
    return calendar
}

/**
 * Determine next routine based on current time
 * Returns null if both routines are empty
 * Logic:
 * - If morning time hasn't passed, show morning
 * - If morning time has passed, show evening (even if evening has also passed)
 */
private fun getNextRoutine(morningRoutine: Routine, eveningRoutine: Routine): Routine? {
    val now = Calendar.getInstance()
    
    // Check if routines have steps
    val morningHasSteps = morningRoutine.steps.isNotEmpty()
    val eveningHasSteps = eveningRoutine.steps.isNotEmpty()
    
    // If both are empty, return null
    if (!morningHasSteps && !eveningHasSteps) {
        return null
    }
    
    // If only one has steps, return that one
    if (morningHasSteps && !eveningHasSteps) {
        return morningRoutine
    }
    if (!morningHasSteps && eveningHasSteps) {
        return eveningRoutine
    }
    
    // Both have steps - determine which is next
    val morningTime = parseTimeForToday(morningRoutine.time)
    
    // If morning time has passed today (or is exactly now), show evening
    // Otherwise, show morning
    return if (now.timeInMillis >= morningTime.timeInMillis) {
        eveningRoutine
    } else {
        morningRoutine
    }
}

@Composable
fun RoutineSection(
    navController: NavController,
    morningRoutine: Routine,
    eveningRoutine: Routine
) {
    val nextRoutine = remember(morningRoutine, eveningRoutine) {
        getNextRoutine(morningRoutine, eveningRoutine)
    }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Routine",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        // Only show card if there's a next routine
        if (nextRoutine != null) {
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
                            text = "Next Routine",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = nextRoutine.time,
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = nextRoutine.title,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SkinQuestionnaireSection(navController: NavController, scanResult: ScanResult?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
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
                        text = "Start Analysis",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Jawab pertanyaan untuk mengetahui tipe kulit Anda",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // STATE B: Result Exists - Only show last scan, score, and skin type
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
                }
            }
        }
    }
}

@Composable
fun FeaturesSection(navController: NavController) {
    val features = listOf(
        FeatureItem(
            title = "Analisis Kulit",
            description = "Jawab pertanyaan untuk mengetahui tipe kulitmu",
            icon = Icons.Default.Face,
            color = Color(0xFF264653),
            route = Screen.SkinQuestionnaire.route
        ),
        FeatureItem(
            title = "Routine Maker",
            description = "Buat rutinitas skincare pagi dan malam",
            icon = Icons.Default.List,
            color = Color(0xFF2A9D8F),
            route = Screen.RoutineMaker.route
        ),
        FeatureItem(
            title = "Product Recommendations",
            description = "Dapatkan rekomendasi produk skincare",
            icon = Icons.Default.ShoppingCart,
            color = Color(0xFFE9C46A),
            route = Screen.Products.route
        )
    )
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Fitur Aplikasi",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        
        features.forEach { feature ->
            FeatureCardCompact(
                feature = feature,
                onClick = {
                    navController.navigate(feature.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class FeatureItem(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val route: String
)

@Composable
fun FeatureCardCompact(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
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
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
            
            // Arrow
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
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
            .height(120.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Product Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.imageResId),
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Brand
                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                // Product Name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                // Category
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                // Price
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Rp ${String.format("%,.0f", product.price)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Product Detail Content Component (dipanggil dari bottom sheet)
@Composable
fun ProductDetailContent(
    product: Product,
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        // Header Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = product.imageResId),
                contentDescription = product.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Brand & Category
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = product.brand,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Product Name
        Text(
            text = product.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Description
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Targets
        Text(
            text = "Best For:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Flow Row for chips
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            product.targetSkinConditions.chunked(3).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { condition ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(condition) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Add to Routine Button
        Button(
            onClick = {
                // Save product to be used in Routine Maker
                RoutineMakerScreen.setSelectedProduct(product)
                // Navigate to Routine Maker
                navController.navigate(Screen.RoutineMaker.route) {
                    popUpTo(Screen.Products.route) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Add to Routine - Rp ${String.format("%,.0f", product.price)}",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
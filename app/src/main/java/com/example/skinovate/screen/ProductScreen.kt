package com.example.skinovate.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
import androidx.navigation.NavController
import com.example.skinovate.data.Product
import com.example.skinovate.data.ProductRepository
import com.example.skinovate.ui.components.EmptyProductsState
import androidx.compose.runtime.collectAsState

/**
 * Filter type enum
 */
enum class FilterType {
    SKIN_TYPE,
    BENEFIT,
    INGREDIENT
}

/**
 * Filter option data class
 */
data class FilterOption(
    val name: String,
    val type: FilterType
)

// Helper function to get category color
@Composable
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "cleanser" -> Color(0xFFE3F2FD) // Light Blue
        "toner" -> Color(0xFFF3E5F5) // Light Purple
        "serum" -> Color(0xFFFFE0B2) // Light Orange
        "moisturizer" -> Color(0xFFC8E6C9) // Light Green
        "sunscreen" -> Color(0xFFFFF9C4) // Light Yellow
        "exfoliator" -> Color(0xFFFCE4EC) // Light Pink
        "retinol" -> Color(0xFFE1BEE7) // Light Purple-Pink
        "eye cream" -> Color(0xFFBBDEFB) // Light Blue
        "face mask" -> Color(0xFFFFCCBC) // Light Orange-Red
        else -> Color(0xFFF5F5F5) // Default Gray
    }
}

// Helper function to get category text color
@Composable
fun getCategoryTextColor(category: String): Color {
    return when (category.lowercase()) {
        "cleanser" -> Color(0xFF1976D2) // Dark Blue
        "toner" -> Color(0xFF7B1FA2) // Dark Purple
        "serum" -> Color(0xFFE65100) // Dark Orange
        "moisturizer" -> Color(0xFF388E3C) // Dark Green
        "sunscreen" -> Color(0xFFF57F17) // Dark Yellow
        "exfoliator" -> Color(0xFFC2185B) // Dark Pink
        "retinol" -> Color(0xFF7B1FA2) // Dark Purple
        "eye cream" -> Color(0xFF1565C0) // Dark Blue
        "face mask" -> Color(0xFFD84315) // Dark Orange-Red
        else -> Color(0xFF616161) // Default Dark Gray
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(navController: NavController) {
    val context = LocalContext.current
    
    // Initialize ProductRepository
    LaunchedEffect(Unit) {
        ProductRepository.init(context)
    }
    
    // Use StateFlow for reactive updates
    val allProducts by ProductRepository.allProductsFlow.collectAsState()

    // --- State Variables ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val sheetState = rememberModalBottomSheetState()

    // --- Get Unique Categories Dynamically ---
    val allCategories = remember(allProducts) {
        allProducts.map { it.category }.distinct().sorted()
    }

    // --- Available Filters ---
    val availableFilters = remember {
        listOf(
            // Skin Types
            FilterOption("Oily Skin", FilterType.SKIN_TYPE),
            FilterOption("Dry Skin", FilterType.SKIN_TYPE),
            FilterOption("Sensitive Skin", FilterType.SKIN_TYPE),
            FilterOption("Combination Skin", FilterType.SKIN_TYPE),
            // Benefits
            FilterOption("Barrier Repair", FilterType.BENEFIT),
            FilterOption("Pore Minimizing", FilterType.BENEFIT),
            FilterOption("Acne", FilterType.BENEFIT),
            FilterOption("Brightening", FilterType.BENEFIT),
            FilterOption("Anti-Aging", FilterType.BENEFIT),
            // Ingredients
            FilterOption("Niacinamide", FilterType.INGREDIENT),
            FilterOption("Retinoid", FilterType.INGREDIENT),
            FilterOption("Retinol", FilterType.INGREDIENT),
            FilterOption("Ceramide", FilterType.INGREDIENT),
            FilterOption("Salicylic Acid", FilterType.INGREDIENT),
            FilterOption("AHA", FilterType.INGREDIENT),
            FilterOption("BHA", FilterType.INGREDIENT),
            FilterOption("Hyaluronic Acid", FilterType.INGREDIENT),
            FilterOption("Vitamin C", FilterType.INGREDIENT),
        )
    }

    // --- Filtering Logic ---
    val displayedProducts = remember(allProducts, searchQuery, selectedCategory, selectedFilter) {
        allProducts.filter { product ->
            val matchesSearch = searchQuery.isEmpty() || 
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == null ||
                    product.category.equals(selectedCategory, ignoreCase = true)

            val matchesFilter = selectedFilter == null || when {
                selectedFilter == "Oily Skin" -> 
                    product.targetSkinConditions.any { it.contains("oily", ignoreCase = true) } ||
                    product.description.contains("oily", ignoreCase = true)
                selectedFilter == "Dry Skin" -> 
                    product.targetSkinConditions.any { it.contains("dry", ignoreCase = true) } ||
                    product.description.contains("dry", ignoreCase = true)
                selectedFilter == "Sensitive Skin" -> 
                    product.targetSkinConditions.any { it.contains("sensitive", ignoreCase = true) } ||
                    product.description.contains("sensitive", ignoreCase = true)
                selectedFilter == "Combination Skin" -> 
                    product.targetSkinConditions.any { it.contains("combination", ignoreCase = true) } ||
                    product.description.contains("combination", ignoreCase = true)
                selectedFilter == "Barrier Repair" -> 
                    product.description.contains("barrier", ignoreCase = true) ||
                    product.description.contains("repair", ignoreCase = true) ||
                    product.targetSkinConditions.any { it.contains("barrier", ignoreCase = true) }
                selectedFilter == "Pore Minimizing" -> 
                    product.description.contains("pore", ignoreCase = true) ||
                    product.targetSkinConditions.any { it.contains("pore", ignoreCase = true) }
                selectedFilter == "Acne" -> 
                    product.targetSkinConditions.any { it.contains("acne", ignoreCase = true) } ||
                    product.description.contains("acne", ignoreCase = true)
                selectedFilter == "Brightening" -> 
                    product.description.contains("bright", ignoreCase = true) ||
                    product.description.contains("lighten", ignoreCase = true) ||
                    product.description.contains("glow", ignoreCase = true)
                selectedFilter == "Anti-Aging" -> 
                    product.description.contains("aging", ignoreCase = true) ||
                    product.description.contains("anti-aging", ignoreCase = true) ||
                    product.targetSkinConditions.any { it.contains("aging", ignoreCase = true) }
                selectedFilter == "Niacinamide" -> 
                    product.name.contains("niacinamide", ignoreCase = true) ||
                    product.description.contains("niacinamide", ignoreCase = true)
                selectedFilter == "Retinoid" || selectedFilter == "Retinol" -> 
                    product.name.contains("retinol", ignoreCase = true) ||
                    product.name.contains("retinoid", ignoreCase = true) ||
                    product.description.contains("retinol", ignoreCase = true) ||
                    product.description.contains("retinoid", ignoreCase = true)
                selectedFilter == "Ceramide" -> 
                    product.name.contains("ceramide", ignoreCase = true) ||
                    product.description.contains("ceramide", ignoreCase = true)
                selectedFilter == "Salicylic Acid" -> 
                    product.description.contains("salicylic", ignoreCase = true)
                selectedFilter == "AHA" -> 
                    product.description.contains("aha", ignoreCase = true) ||
                    product.description.contains("glycolic", ignoreCase = true) ||
                    product.description.contains("lactic", ignoreCase = true)
                selectedFilter == "BHA" -> 
                    product.description.contains("bha", ignoreCase = true) ||
                    product.description.contains("salicylic", ignoreCase = true)
                selectedFilter == "Hyaluronic Acid" -> 
                    product.description.contains("hyaluronic", ignoreCase = true) ||
                    product.description.contains("hydrat", ignoreCase = true)
                selectedFilter == "Vitamin C" -> 
                    product.name.contains("vitamin c", ignoreCase = true) ||
                    product.description.contains("vitamin c", ignoreCase = true) ||
                    product.description.contains("ascorbic", ignoreCase = true)
                else -> true
            }

            matchesSearch && matchesCategory && matchesFilter
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .statusBarsPadding()
            ) {
                Text(
                    text = "Find Products",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search products or brands...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Category Filter Chips (Horizontal Scroll)
                Text(
                    text = "Kategori",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("All") }
                        )
                    }
                    items(allCategories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = if (selectedCategory == category) null else category
                            },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = getCategoryColor(category),
                                containerColor = Color(0xFFF5F5F5),
                                selectedLabelColor = getCategoryTextColor(category),
                                labelColor = Color.Gray
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 3. Detail Filter Chips (Horizontal Scroll)
                Text(
                    text = "Filter Lanjutan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            label = { Text("Semua") }
                        )
                    }
                    items(availableFilters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter.name,
                            onClick = {
                                selectedFilter = if (selectedFilter == filter.name) null else filter.name
                            },
                            label = { Text(filter.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (filter.type) {
                                    FilterType.SKIN_TYPE -> Color(0xFFE3F2FD)
                                    FilterType.BENEFIT -> Color(0xFFE8F5E9)
                                    FilterType.INGREDIENT -> Color(0xFFFFF3E0)
                                },
                                containerColor = Color(0xFFF5F5F5),
                                selectedLabelColor = when (filter.type) {
                                    FilterType.SKIN_TYPE -> Color(0xFF1976D2)
                                    FilterType.BENEFIT -> Color(0xFF388E3C)
                                    FilterType.INGREDIENT -> Color(0xFFE65100)
                                },
                                labelColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (displayedProducts.isEmpty()) {
            // Empty state
            EmptyProductsState(
                onRefresh = {
                    searchQuery = ""
                    selectedCategory = null
                    selectedFilter = null
                },
                modifier = Modifier.padding(padding)
            )
        } else {
            // 3. The 2-Column Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(displayedProducts) { product ->
                    ProductGridItem(
                        product = product,
                        onClick = { selectedProduct = product }
                    )
                }
            }
        }
    }

    // 4. Product Detail Sheet
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

// --- Component: Grid Card ---
@Composable
fun ProductGridItem(product: Product, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.imageResId),
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Brand & Category
            Text(
                text = product.brand.uppercase(),
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
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(40.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Category Badge
            Surface(
                color = getCategoryColor(product.category),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = getCategoryTextColor(product.category),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Price
            Text(
                text = "Rp ${String.format("%,.0f", product.price)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- Component: Bottom Sheet Content ---
@Composable
fun ProductDetailContent(
    product: Product,
    navController: NavController,
    onDismiss: () -> Unit
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
                color = getCategoryColor(product.category),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = getCategoryTextColor(product.category),
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
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            product.targetSkinConditions.forEach { condition ->
                SuggestionChip(
                    onClick = {},
                    label = { Text(condition) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Add to Routine Button
        Button(
            onClick = {
                // Save product to be used in Routine Maker
                RoutineMakerScreen.setSelectedProduct(product)
                onDismiss()
                // Navigate to Routine Maker
                navController.navigate(com.example.skinovate.navigation.Screen.RoutineMaker.route) {
                    popUpTo(com.example.skinovate.navigation.Screen.Products.route) {
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

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }

        var xPos = 0
        var yPos = 0
        var maxHeight = 0

        val positions = mutableListOf<Pair<Int, Int>>()

        placeables.forEach { placeable ->
            if (xPos + placeable.width > constraints.maxWidth) {
                xPos = 0
                yPos += maxHeight + 8
                maxHeight = 0
            }

            positions.add(Pair(xPos, yPos))
            xPos += placeable.width + 8
            maxHeight = maxOf(maxHeight, placeable.height)
        }

        val width = constraints.maxWidth
        val height = yPos + maxHeight

        layout(width, height) {
            placeables.forEachIndexed { index, placeable ->
                val (x, y) = positions[index]
                placeable.place(x, y)
            }
        }
    }
}


@Composable
private fun Layout(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    measurePolicy: androidx.compose.ui.layout.MeasurePolicy
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier,
        measurePolicy = measurePolicy
    )
}
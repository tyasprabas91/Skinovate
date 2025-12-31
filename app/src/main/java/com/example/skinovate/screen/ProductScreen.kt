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
import androidx.navigation.NavController
import com.example.skinovate.data.Product
import com.example.skinovate.data.ProductRepository

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

    // --- State Variables ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val sheetState = rememberModalBottomSheetState()

    // --- Get Unique Categories Dynamically ---
    val allCategories = remember {
        ProductRepository.allProducts.map { it.category }.distinct().sorted()
    }

    // --- Filtering Logic ---
    val displayedProducts = remember(searchQuery, selectedCategory) {
        ProductRepository.allProducts.filter { product ->
            val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == null ||
                    product.category.equals(selectedCategory, ignoreCase = true)

            matchesSearch && matchesCategory
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

                // 2. Filter Chips (Horizontal Scroll)
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
            }
        }
    ) { padding ->
        if (displayedProducts.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No products found",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Try adjusting your search or filters",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
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
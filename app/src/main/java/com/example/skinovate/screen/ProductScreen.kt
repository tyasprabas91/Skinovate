package com.example.skinovate.screens

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
import com.example.skinovate.data.SkincareStep
import androidx.compose.ui.platform.LocalUriHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(navController: NavController) { // NavController added (even if unused yet, good practice)

    // --- State Variables ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<SkincareStep?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) } // For Bottom Sheet

    val sheetState = rememberModalBottomSheetState()

    // --- Filtering Logic ---
    // We filter the BIG list based on the search text AND the category chip
    val displayedProducts = remember(searchQuery, selectedCategory) {
        ProductRepository.allProducts.filter { product ->
            val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || product.category == selectedCategory

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
                    .statusBarsPadding() // Avoid overlapping system bar
            ) {
                Text("Find Products", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search brands, products...") },
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
                    items(SkincareStep.values()) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                // Toggle logic: Click again to unselect
                                selectedCategory = if (selectedCategory == category) null else category
                            },
                            label = { Text(category.displayName) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        // 3. The 2-Column Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // <--- THE 2-COLUMN MAGIC
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(displayedProducts) { product ->
                ProductGridItem(
                    product = product,
                    onClick = { selectedProduct = product } // Open sheet
                )
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
            ProductDetailContent(product = selectedProduct!!)
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
            // Placeholder Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Brand & Name
            Text(product.brand.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)

            Spacer(modifier = Modifier.height(4.dp))

            // Rating & Price
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFE9C46A), modifier = Modifier.size(14.dp))
                Text(" ${product.rating}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(product.price, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- Component: Bottom Sheet Content ---
@Composable
fun ProductDetailContent(product: Product) {
    val uriHandler = LocalUriHandler.current

    Column(modifier = Modifier.padding(24.dp).padding(bottom = 32.dp)) {
        // Header Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Title Block
        Text(product.brand, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
        Text(product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, null, tint = Color(0xFFE9C46A))
            Text(" ${product.rating} (${product.reviewCount} reviews)", fontWeight = FontWeight.Medium)
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Description (Mock)
        Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "This ${product.category.displayName} is formulated to improve skin texture and hydration. Suitable for sensitive skin.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Buy Button
        Button(
            onClick = {
                uriHandler.openUri(product.storeUrl)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            // Make it clear they are leaving the app
            Text("Shop on Store - ${product.price}")
        }

        // disclaimer text
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You will be redirected to the seller's page.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
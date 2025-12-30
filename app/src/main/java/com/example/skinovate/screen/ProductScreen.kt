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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skinovate.data.Product
import com.example.skinovate.data.ProductRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(navController: NavController) {

    // --- State Variables ---
    var searchQuery by remember { mutableStateOf("") }
    // We now use String for category since your model uses String
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val sheetState = rememberModalBottomSheetState()

    // --- Get Unique Categories Dynamically ---
    val allCategories = remember {
        ProductRepository.allProducts.map { it.category }.distinct()
    }

    // --- Filtering Logic ---
    val displayedProducts = remember(searchQuery, selectedCategory) {
        ProductRepository.allProducts.filter { product ->
            val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true)

            // FIX: Compare String to String directly
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
                Text("Find Products", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search products...") },
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
                                // Toggle logic: Click again to unselect
                                selectedCategory = if (selectedCategory == category) null else category
                            },
                            label = { Text(category) }
                        )
                    }
                }
            }
        }
    ) { padding ->
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

    // 4. Product Detail Sheet
    if (selectedProduct != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedProduct = null },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            ProductDetailContent(
                product = selectedProduct!!,
                navController = navController
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
                    contentScale = ContentScale.Fit, // Fit ensures the whole image is visible
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Category & Name
            Text(product.category.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)

            Spacer(modifier = Modifier.height(4.dp))

            // Price
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.bodyMedium,
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
    navController: NavController
) {
    Column(modifier = Modifier
        .padding(24.dp)
        .padding(bottom = 32.dp)) {

        // Header Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = product.imageResId),
                contentDescription = product.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(150.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Title Block
        Text(product.category, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
        Text(product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Description
        Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Targets
        Text("Best For:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            product.targetSkinConditions.forEach { condition ->
                SuggestionChip(onClick = {}, label = { Text(condition) })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Add to Routine Button
        Button(
            onClick = {
                // Save product to be used in Routine Maker
                RoutineMakerScreen.setSelectedProduct(product)
                // Navigate to Routine Maker
                navController.navigate(com.example.skinovate.navigation.Screen.RoutineMaker.route) {
                    popUpTo(com.example.skinovate.navigation.Screen.Products.route) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Add to Routine - $${product.price}")
        }
    }
}
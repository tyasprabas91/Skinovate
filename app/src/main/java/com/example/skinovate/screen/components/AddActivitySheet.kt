package com.example.skinovate.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skinovate.data.Product
import com.example.skinovate.data.ProductRepository
import com.example.skinovate.data.RoutineStep
import com.example.skinovate.data.SkincareStep
import kotlinx.coroutines.flow.first
import java.util.UUID

// Helper function to map product category to SkincareStep
fun mapProductCategoryToSkincareStep(category: String): SkincareStep? {
    return when (category.lowercase()) {
        "cleanser" -> SkincareStep.CLEANSER
        "toner" -> SkincareStep.TONER
        "exfoliator" -> SkincareStep.EXFOLIATOR
        "serum" -> SkincareStep.SERUM
        "moisturizer" -> SkincareStep.MOISTURIZER
        "sunscreen" -> SkincareStep.SUNSCREEN
        "retinol" -> SkincareStep.RETINOL
        "eye cream" -> SkincareStep.EYE_CREAM
        "face mask" -> SkincareStep.FACE_MASK
        else -> null
    }
}

// Helper function to map SkincareStep to product category string
fun mapSkincareStepToCategory(step: SkincareStep): String {
    return when (step) {
        SkincareStep.CLEANSER -> "cleanser"
        SkincareStep.TONER -> "toner"
        SkincareStep.EXFOLIATOR -> "exfoliator"
        SkincareStep.SERUM -> "serum"
        SkincareStep.MOISTURIZER -> "moisturizer"
        SkincareStep.SUNSCREEN -> "sunscreen"
        SkincareStep.RETINOL -> "retinol"
        SkincareStep.EYE_CREAM -> "eye cream"
        SkincareStep.FACE_MASK -> "face mask"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivitySheet(
    isMorningContext: Boolean,
    onBack: () -> Unit,
    onSave: (RoutineStep) -> Unit,
    product: Product? = null, // Optional product parameter
    editStep: RoutineStep? = null // Optional: if provided, this is edit mode
) {
    // 1. Context for the Time Picker Dialog
    val context = LocalContext.current
    
    // Initialize ProductRepository
    LaunchedEffect(Unit) {
        ProductRepository.init(context)
    }

    // 2. State Variables - Auto-fill if product or editStep is provided
    val initialType = remember(product, editStep) {
        editStep?.type ?: product?.let { mapProductCategoryToSkincareStep(it.category) }
    }
    val initialProductName = remember(product, editStep) {
        editStep?.productName ?: product?.let { "${it.brand} ${it.name}" } ?: ""
    }
    val initialDuration = remember(editStep) {
        editStep?.duration ?: 60 // Default 60 seconds (1 minute)
    }
    
    var selectedType by remember { mutableStateOf<SkincareStep?>(initialType) }
    var productName by remember { mutableStateOf(initialProductName) }
    var selectedProduct by remember { mutableStateOf<Product?>(product) }
    var showProductList by remember { mutableStateOf(false) }
    var durationSeconds by remember { mutableStateOf(initialDuration) }
    
    // Convert seconds to minutes and seconds for display
    val durationMinutes = durationSeconds / 60
    val durationSecondsRemainder = durationSeconds % 60

    // Get products by category when category is selected
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    
    LaunchedEffect(selectedType) {
        if (selectedType != null) {
            val category = mapSkincareStepToCategory(selectedType!!)
            val productsFlow = ProductRepository.getProductsByCategory(category, context)
            products = productsFlow.first()
        } else {
            products = emptyList()
        }
    }

    // 4. UI Layout
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .fillMaxWidth()
    ) {
        // --- Header ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (editStep != null) "Edit Activity" else "Add New Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Category Grid ---
        Text("Select Category", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(150.dp)
        ) {
            items(SkincareStep.values()) { step ->
                FilterChip(
                    selected = selectedType == step,
                    onClick = { 
                        selectedType = step
                        selectedProduct = null // Reset selected product when category changes
                        productName = ""
                    },
                    label = { Text(step.displayName, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Product Selection Section ---
        if (selectedType != null && products.isNotEmpty()) {
            Text("Pilih Produk (Opsional)", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            
            if (!showProductList) {
                OutlinedButton(
                    onClick = { showProductList = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lihat Produk yang Tersedia")
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products) { product ->
                            ProductSelectionItem(
                                product = product,
                                isSelected = selectedProduct?.id == product.id,
                                onClick = {
                                    selectedProduct = product
                                    productName = "${product.brand} ${product.name}"
                                    showProductList = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // --- Product Name Input ---
        OutlinedTextField(
            value = productName,
            onValueChange = { 
                productName = it
                selectedProduct = null // Clear selected product when manually typing
            },
            label = { Text("Product Name (Optional)") },
            placeholder = { Text("e.g. CeraVe") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Timer Duration Input (Jeda ke Activity Selanjutnya) ---
        Text("Jeda ke Activity Selanjutnya", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        
        // Minutes selector
        Column {
            Text("Menit", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (durationMinutes > 0) durationSeconds = (durationMinutes - 1) * 60 + durationSecondsRemainder },
                    enabled = durationMinutes > 0
                ) {
                    Text("-")
                }
                Text(
                    text = "$durationMinutes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Button(
                    onClick = { durationSeconds = (durationMinutes + 1) * 60 + durationSecondsRemainder }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Seconds selector
        Column {
            Text("Detik", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { 
                        if (durationSecondsRemainder > 0) {
                            durationSeconds = durationMinutes * 60 + (durationSecondsRemainder - 1)
                        } else if (durationMinutes > 0) {
                            durationSeconds = (durationMinutes - 1) * 60 + 59
                        }
                    },
                    enabled = durationSeconds > 0
                ) {
                    Text("-")
                }
                Text(
                    text = "$durationSecondsRemainder",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Button(
                    onClick = { 
                        val newSeconds = durationSecondsRemainder + 1
                        if (newSeconds >= 60) {
                            durationSeconds = (durationMinutes + 1) * 60
                        } else {
                            durationSeconds = durationMinutes * 60 + newSeconds
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }
        
        Text(
            "Total: ${durationSeconds} detik (${String.format("%02d:%02d", durationMinutes, durationSecondsRemainder)}). Timer hanya muncul jika ada activity selanjutnya.",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Save Button ---
        Button(
            onClick = {
                if (selectedType != null) {
                    val stepId = editStep?.id ?: UUID.randomUUID().toString()
                    val newStep = RoutineStep(
                        id = stepId,
                        type = selectedType!!,
                        productName = productName,
                        duration = durationSeconds
                    )
                    onSave(newStep)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = selectedType != null
        ) {
            Text(if (editStep != null) "Update Activity" else "Add to Routine")
        }
    }
}

@Composable
fun ProductSelectionItem(
    product: Product,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.primary
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (isSelected) {
                Text(
                    text = "✓",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

package com.example.skinovate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Generic empty state component
 */
@Composable
fun EmptyState(
    icon: ImageVector = Icons.Filled.Info,
    title: String = "Tidak ada data",
    message: String = "Data akan muncul di sini",
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        
        if (actionText != null && onAction != null) {
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(actionText)
            }
        }
    }
}

/**
 * Empty state for products
 */
@Composable
fun EmptyProductsState(
    onRefresh: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Filled.ShoppingCart,
        title = if (onRefresh != null) "Tidak ada produk ditemukan" else "Tidak ada produk",
        message = if (onRefresh != null) "Coba ubah kata kunci atau filter pencarianmu" else "Produk yang cocok untukmu akan muncul di sini",
        actionText = if (onRefresh != null) "Hapus Filter" else null,
        onAction = onRefresh,
        modifier = modifier
    )
}

/**
 * Empty state for routines
 */
@Composable
fun EmptyRoutineState(
    onAddStep: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.List,
        title = "Rutinitas kosong",
        message = "Tambahkan langkah skincare untuk memulai rutinitasmu",
        actionText = if (onAddStep != null) "Tambah Langkah" else null,
        onAction = onAddStep,
        modifier = modifier
    )
}

/**
 * Empty state for scan history
 */
@Composable
fun EmptyScanHistoryState(
    onStartScan: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Filled.Face,
        title = "Belum ada scan",
        message = "Lakukan face analysis untuk melihat hasil scanmu",
        actionText = if (onStartScan != null) "Mulai Scan" else null,
        onAction = onStartScan,
        modifier = modifier
    )
}


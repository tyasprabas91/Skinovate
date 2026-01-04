package com.example.skinovate.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.skinovate.utils.DataExportHelper
import com.example.skinovate.utils.ErrorMessageHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var dataSharingEnabled by remember { mutableStateOf(false) }
    var analyticsEnabled by remember { mutableStateOf(true) }
    var personalizedAdsEnabled by remember { mutableStateOf(false) }
    
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var exportInProgress by remember { mutableStateOf(false) }
    var deleteInProgress by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Pengaturan Privasi",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                text = "Kelola bagaimana data pribadimu digunakan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Privacy Options
            PrivacyOptionCard(
                title = "Berbagi Data",
                description = "Izinkan aplikasi untuk berbagi data anonim untuk penelitian",
                enabled = dataSharingEnabled,
                onToggle = { dataSharingEnabled = it }
            )

            PrivacyOptionCard(
                title = "Analitik",
                description = "Bantu kami meningkatkan aplikasi dengan data penggunaan",
                enabled = analyticsEnabled,
                onToggle = { analyticsEnabled = it }
            )

            PrivacyOptionCard(
                title = "Iklan yang Dipersonalisasi",
                description = "Tampilkan iklan berdasarkan minatmu",
                enabled = personalizedAdsEnabled,
                onToggle = { personalizedAdsEnabled = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Data Management
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Kelola Data",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Unduh atau hapus semua data yang tersimpan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                exportInProgress = true
                                val result = DataExportHelper.exportUserData(context)
                                exportInProgress = false
                                
                                result.onSuccess { file ->
                                    snackbarHostState.showSnackbar("Data berhasil diekspor ke: ${file.name}")
                                    // Optionally share the file
                                    shareFile(context, file)
                                }.onFailure { exception ->
                                    snackbarHostState.showSnackbar(ErrorMessageHelper.getErrorMessage(exception))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !exportInProgress && !deleteInProgress,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (exportInProgress) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (exportInProgress) "Mengekspor..." else "Unduh Data Saya")
                    }
                    OutlinedButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !exportInProgress && !deleteInProgress
                    ) {
                        if (deleteInProgress) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            if (deleteInProgress) "Menghapus..." else "Hapus Semua Data",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        
        // Delete Confirmation Dialog
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Hapus Semua Data?") },
                text = {
                    Text("Tindakan ini akan menghapus semua data Anda termasuk:\n\n" +
                            "• Riwayat scan wajah\n" +
                            "• Rutinitas skincare\n" +
                            "• Pengaturan akun\n\n" +
                            "Tindakan ini tidak dapat dibatalkan.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmDialog = false
                            scope.launch {
                                deleteInProgress = true
                                val result = DataExportHelper.deleteAllUserData(context)
                                deleteInProgress = false
                                
                                result.onSuccess {
                                    snackbarHostState.showSnackbar("Semua data berhasil dihapus")
                                }.onFailure { exception ->
                                    snackbarHostState.showSnackbar(ErrorMessageHelper.getErrorMessage(exception))
                                }
                            }
                        }
                    ) {
                        Text("Hapus", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
        
    }
}

@Composable
fun PrivacyOptionCard(
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

private fun shareFile(context: Context, file: java.io.File) {
    try {
        val uri = Uri.fromFile(file)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/json"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Bagikan data ekspor"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

package com.example.skinovate.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.skinovate.R
import com.example.skinovate.chat.GroqApiService
import com.example.skinovate.chat.GroqMessage
import kotlinx.coroutines.launch

@Composable
fun ChatBotDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val apiKey = context.getString(R.string.groq_api_key)
    val groqService = remember { GroqApiService(apiKey) }
    
    var messages by remember { mutableStateOf<List<GroqMessage>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Scroll to bottom when new message added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Konsultasi Skincare",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tanya tentang permasalahan wajah",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (messages.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Halo! 👋",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Saya di sini untuk membantu Anda dengan permasalahan kulit wajah. Tanyakan apa saja tentang:\n\n" +
                                                "• Jerawat dan perawatannya\n" +
                                                "• Masalah kulit (kering, berminyak, sensitif)\n" +
                                                "• Rekomendasi produk skincare\n" +
                                                "• Tips perawatan wajah\n" +
                                                "• Skincare routine",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    
                    items(messages) { message ->
                        ChatMessageBubble(message = message)
                    }
                    
                    if (isLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF5F5F5)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Text(
                                            text = "Mengetik...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Error message
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Input Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Tanya tentang permasalahan wajah...") },
                        enabled = !isLoading,
                        maxLines = 3
                    )
                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isLoading) {
                                val userMessage = inputText.trim()
                                
                                // Validate question
                                if (!groqService.isValidQuestion(userMessage)) {
                                    errorMessage = "Maaf, saya hanya dapat menjawab pertanyaan tentang permasalahan kulit wajah dan skincare. Silakan tanyakan hal yang terkait dengan skincare."
                                    return@FloatingActionButton
                                }
                                
                                errorMessage = null
                                val newUserMessage = GroqMessage("user", userMessage)
                                messages = messages + newUserMessage
                                inputText = ""
                                isLoading = true
                                
                                scope.launch {
                                    val result = groqService.sendMessage(userMessage, messages.dropLast(1))
                                    isLoading = false
                                    
                                    result.onSuccess { response ->
                                        messages = messages + GroqMessage("assistant", response)
                                    }.onFailure { exception ->
                                        errorMessage = "Terjadi kesalahan: ${exception.message ?: "Silakan coba lagi nanti."}"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.Default.Send,
                            "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: GroqMessage) {
    val isUser = message.role == "user"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(horizontal = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color(0xFFF5F5F5)
                }
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            )
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) Color.White else Color.Black
            )
        }
    }
}


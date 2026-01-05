package com.example.skinovate.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.skinovate.data.Routine
import com.example.skinovate.data.RoutineStep
import com.example.skinovate.utils.RoutineTimerHelper

/**
 * Dialog untuk menampilkan routine dengan semua activity dan timer controls
 */
@Composable
fun RoutineViewDialog(
    routine: Routine,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val timerState by RoutineTimerHelper.timerState.collectAsState()
    var showTimerFinishedDialog by remember { mutableStateOf(false) }
    var activeTimerStepIndex by remember { mutableStateOf<Int?>(null) }
    
    // Handle timer finished callback
    LaunchedEffect(timerState) {
        if (timerState is RoutineTimerHelper.TimerState.Finished) {
            showTimerFinishedDialog = true
            activeTimerStepIndex = null // Reset active step when timer finishes
        }
    }
    
    // Cancel timer when dialog is dismissed
    LaunchedEffect(Unit) {
        RoutineTimerHelper.cancelTimer()
        activeTimerStepIndex = null
    }
    
    Dialog(onDismissRequest = {
        RoutineTimerHelper.cancelTimer()
        activeTimerStepIndex = null
        onDismiss()
    }) {
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
                    Text(
                        text = routine.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        RoutineTimerHelper.cancelTimer()
                        activeTimerStepIndex = null
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Activities List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(routine.steps) { index, step ->
                        val isLastStep = index == routine.steps.size - 1
                        val nextStep = if (!isLastStep) routine.steps[index + 1] else null
                        val stepName = if (step.productName.isNotEmpty()) step.productName else step.type.displayName
                        val isActiveTimer = activeTimerStepIndex == index
                        
                        RoutineStepDialogItem(
                            step = step,
                            isLastStep = isLastStep,
                            nextStepName = nextStep?.let { 
                                if (it.productName.isNotEmpty()) it.productName else it.type.displayName 
                            },
                            timerState = if (isActiveTimer) timerState else RoutineTimerHelper.TimerState.Idle,
                            onStartTimer = {
                                if (!isLastStep) {
                                    // Cancel any existing timer first
                                    RoutineTimerHelper.cancelTimer()
                                    activeTimerStepIndex = index
                                    
                                    val nextName = nextStep?.let { 
                                        if (it.productName.isNotEmpty()) it.productName else it.type.displayName 
                                    } ?: "Activity selanjutnya"
                                    RoutineTimerHelper.startTimer(
                                        durationSeconds = step.duration,
                                        stepName = nextName,
                                        context = context,
                                        onFinished = {
                                            showTimerFinishedDialog = true
                                            activeTimerStepIndex = null
                                        }
                                    )
                                }
                            },
                            onStopTimer = {
                                RoutineTimerHelper.cancelTimer()
                                activeTimerStepIndex = null
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Timer Finished Dialog
    if (showTimerFinishedDialog) {
        AlertDialog(
            onDismissRequest = { 
                showTimerFinishedDialog = false
                RoutineTimerHelper.cancelTimer()
                activeTimerStepIndex = null
            },
            title = {
                Text(
                    text = "Timer Selesai!",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Waktu jeda telah habis. Anda bisa lanjut ke activity selanjutnya.")
            },
            confirmButton = {
                TextButton(onClick = { 
                    showTimerFinishedDialog = false
                    RoutineTimerHelper.cancelTimer()
                    activeTimerStepIndex = null
                }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun RoutineStepDialogItem(
    step: RoutineStep,
    isLastStep: Boolean,
    nextStepName: String?,
    timerState: RoutineTimerHelper.TimerState,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit
) {
    val minutes = step.duration / 60
    val seconds = step.duration % 60
    val durationText = if (minutes > 0) {
        "$minutes m ${seconds}s"
    } else {
        "${seconds}s"
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = step.type.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (step.productName.isNotEmpty()) {
                        Text(
                            text = step.productName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Timer section - only show if not last step
            if (!isLastStep) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Jeda ke: ${nextStepName ?: "Activity selanjutnya"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "Durasi: $durationText",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Timer display and controls
                    when (timerState) {
                        is RoutineTimerHelper.TimerState.Running -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = RoutineTimerHelper.formatTime(timerState.remainingSeconds),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(onClick = onStopTimer) {
                                    Icon(Icons.Default.Close, "Stop", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        else -> {
                            IconButton(onClick = onStartTimer) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    "Start Timer",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


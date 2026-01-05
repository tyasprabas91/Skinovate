package com.example.skinovate.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.skinovate.data.Product
import com.example.skinovate.data.Routine
import com.example.skinovate.data.RoutineRecommendation
import com.example.skinovate.data.RoutineRecommendationRepository
import com.example.skinovate.data.RoutineRepository
import com.example.skinovate.data.RoutineStep
import com.example.skinovate.screen.components.AddActivitySheet
import com.example.skinovate.ui.components.EmptyRoutineState
import com.example.skinovate.navigation.Screen
import com.example.skinovate.utils.RoutineTimerHelper
import androidx.compose.foundation.clickable
import android.app.TimePickerDialog
import java.util.Calendar

// Define states for the Bottom Sheet
enum class SheetMode { NONE, EDIT_LIST, ADD_NEW, EDIT_STEP }
// Define which routine type we are currently touching
enum class RoutineType { MORNING, EVENING }

// Companion object to store product selected from Product Screen
object RoutineMakerScreen {
    var selectedProductForRoutine: Product? = null
        private set
    
    fun setSelectedProduct(product: Product?) {
        selectedProductForRoutine = product
    }
    
    fun clearSelectedProduct() {
        selectedProductForRoutine = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineMakerScreen(navController: NavController) {
    val context = LocalContext.current
    
    // Initialize RoutineRepository
    LaunchedEffect(Unit) {
        RoutineRepository.init(context)
    }
    
    // Observe routines
    val morningRoutine by RoutineRepository.morningRoutine.collectAsState()
    val eveningRoutine by RoutineRepository.eveningRoutine.collectAsState()

    // State Management for Routine Editing
    var sheetMode by remember { mutableStateOf(SheetMode.NONE) }
    var selectedRoutineType by remember { mutableStateOf<RoutineType?>(null) }
    var selectedProductForRoutine by remember { mutableStateOf<Product?>(RoutineMakerScreen.selectedProductForRoutine) }
    var editingStep by remember { mutableStateOf<RoutineStep?>(null) }
    val routineSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    // Auto-open Add Activity Sheet if product is selected
    LaunchedEffect(Unit) {
        val product = RoutineMakerScreen.selectedProductForRoutine
        if (product != null) {
            selectedProductForRoutine = product
            selectedRoutineType = RoutineType.MORNING
            sheetMode = SheetMode.ADD_NEW
            RoutineMakerScreen.clearSelectedProduct()
        }
    }
    
    // Helper: Get the Live Routine based on selection
    val activeRoutine = when (selectedRoutineType) {
        RoutineType.MORNING -> morningRoutine
        RoutineType.EVENING -> eveningRoutine
        null -> null
    }
    
    // Helper to close sheet
    fun closeRoutineSheet() {
        sheetMode = SheetMode.NONE
        selectedRoutineType = null
    }

    // Get recommendations
    val recommendations = remember { 
        try {
            RoutineRecommendationRepository.getAllRecommendations()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList<RoutineRecommendation>()
        }
    }
    var showApplyDialog by remember { mutableStateOf<RoutineRecommendation?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Routine Maker",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Routine Maker Section (Manual Edit)
            item {
                Text(
                    text = "Rutinitas Saya",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                RoutineSummaryCard(
                    routine = morningRoutine,
                    routineType = RoutineType.MORNING,
                    onEditClick = { 
                        selectedRoutineType = RoutineType.MORNING
                        sheetMode = SheetMode.EDIT_LIST
                    },
                    onTimeClick = { newTime ->
                        RoutineRepository.updateMorningRoutineTime(newTime, context)
                    }
                )
            }
            
            item {
                RoutineSummaryCard(
                    routine = eveningRoutine,
                    routineType = RoutineType.EVENING,
                    onEditClick = { 
                        selectedRoutineType = RoutineType.EVENING
                        sheetMode = SheetMode.EDIT_LIST
                    },
                    onTimeClick = { newTime ->
                        RoutineRepository.updateEveningRoutineTime(newTime, context)
                    }
                )
            }
            
            // Divider
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Recommendations Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Rekomendasi Rutinitas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pilih salah satu rekomendasi rutinitas yang sesuai dengan kebutuhan kulit Anda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            items(recommendations) { recommendation ->
                RecommendationCard(
                    recommendation = recommendation,
                    onClick = {
                        showApplyDialog = recommendation
                    }
                )
            }
        }
    }
    
    // Routine Maker Bottom Sheet
    if (sheetMode == SheetMode.ADD_NEW || sheetMode == SheetMode.EDIT_STEP || (sheetMode == SheetMode.EDIT_LIST && activeRoutine != null)) {
        ModalBottomSheet(
            onDismissRequest = { 
                closeRoutineSheet()
                editingStep = null
                if (selectedProductForRoutine != null) {
                    navController.navigate(Screen.Products.route) {
                        popUpTo(Screen.RoutineMaker.route) {
                            inclusive = true
                        }
                    }
                }
            },
            sheetState = routineSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            if (sheetMode == SheetMode.EDIT_LIST && activeRoutine != null) {
                EditRoutineListContent(
                    routine = activeRoutine,
                    onAddClick = { sheetMode = SheetMode.ADD_NEW },
                    onEditStep = { step ->
                        editingStep = step
                        sheetMode = SheetMode.EDIT_STEP
                    },
                    onRemoveStep = { stepId: String ->
                        if (selectedRoutineType == RoutineType.MORNING) {
                            RoutineRepository.removeStepFromMorning(stepId, context)
                        } else {
                            RoutineRepository.removeStepFromEvening(stepId, context)
                        }
                    }
                )
            } else if (sheetMode == SheetMode.ADD_NEW || sheetMode == SheetMode.EDIT_STEP) {
                AddActivitySheet(
                    isMorningContext = (selectedRoutineType == RoutineType.MORNING),
                    onBack = { 
                        editingStep = null
                        if (activeRoutine != null) {
                            sheetMode = SheetMode.EDIT_LIST
                        } else {
                            closeRoutineSheet()
                        }
                        if (selectedProductForRoutine != null) {
                            navController.navigate(Screen.Products.route) {
                                popUpTo(Screen.RoutineMaker.route) {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    onSave = { step ->
                        if (sheetMode == SheetMode.EDIT_STEP && editingStep != null) {
                            // Update existing step
                            if (selectedRoutineType == RoutineType.MORNING) {
                                RoutineRepository.updateStepInMorning(step, context)
                            } else {
                                RoutineRepository.updateStepInEvening(step, context)
                            }
                            editingStep = null
                            sheetMode = SheetMode.EDIT_LIST
                        } else {
                            // Add new step
                            if (selectedRoutineType == RoutineType.MORNING) {
                                RoutineRepository.addStepToMorning(step, context)
                            } else {
                                RoutineRepository.addStepToEvening(step, context)
                            }
                            if (activeRoutine != null) {
                                sheetMode = SheetMode.EDIT_LIST
                            } else {
                                closeRoutineSheet()
                            }
                        }
                        if (selectedProductForRoutine != null) {
                            navController.navigate(Screen.Products.route) {
                                popUpTo(Screen.RoutineMaker.route) {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    product = selectedProductForRoutine,
                    editStep = editingStep
                )
            }
        }
    }
    
    // Apply Recommendation Dialog
    showApplyDialog?.let { recommendation ->
        AlertDialog(
            onDismissRequest = { showApplyDialog = null },
            title = {
                Text(
                    text = "Terapkan Rekomendasi?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Rekomendasi \"${recommendation.title}\" akan diterapkan ke rutinitas pagi dan malam Anda.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rutinitas yang ada akan diganti. Apakah Anda yakin?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            RoutineRepository.applyRecommendation(
                                recommendation.morningRoutine,
                                recommendation.eveningRoutine,
                                context
                            )
                            showApplyDialog = null
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showApplyDialog = null
                        }
                    }
                ) {
                    Text("Terapkan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApplyDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun RoutineSummaryCard(
    routine: Routine,
    routineType: RoutineType,
    onEditClick: () -> Unit,
    onTimeClick: (String) -> Unit
) {
    val context = LocalContext.current
    var showRoutineDialog by remember { mutableStateOf(false) }
    
    // Parse current time from routine.time string (format: "08:00 AM")
    val (currentHour, currentMinute) = remember(routine.time) {
        try {
            val parts = routine.time.split(" ")
            val timePart = parts[0] // "08:00"
            val amPm = if (parts.size > 1) parts[1] else "AM"
            val timeParts = timePart.split(":")
            var hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            
            if (amPm.equals("PM", ignoreCase = true) && hour != 12) {
                hour += 12
            } else if (amPm.equals("AM", ignoreCase = true) && hour == 12) {
                hour = 0
            }
            Pair(hour, minute)
        } catch (e: Exception) {
            Pair(8, 0) // Default to 8:00 AM
        }
    }
    
    var showTimePicker by remember { mutableStateOf(false) }
    
    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val amPm = if (hourOfDay < 12) "AM" else "PM"
                var displayHour = hourOfDay
                if (hourOfDay == 0) {
                    displayHour = 12
                } else if (hourOfDay > 12) {
                    displayHour = hourOfDay - 12
                }
                val timeString = String.format("%02d:%02d %s", displayHour, minute, amPm)
                onTimeClick(timeString)
                showTimePicker = false
            },
            currentHour,
            currentMinute,
            false
        ).show()
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
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
                        text = routine.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${routine.steps.size} langkah",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                // Time (clickable)
                TextButton(
                    onClick = { showTimePicker = true }
                ) {
                    Text(
                        text = routine.time,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Edit Button
            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("Edit Rutinitas")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // View Activities Button (to open dialog)
            OutlinedButton(
                onClick = { showRoutineDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat Activity & Timer")
            }
        }
    }
    
    // Routine View Dialog
    if (showRoutineDialog) {
        com.example.skinovate.screen.components.RoutineViewDialog(
            routine = routine,
            onDismiss = { showRoutineDialog = false }
        )
    }
}

@Composable
fun EditRoutineListContent(
    routine: Routine,
    onAddClick: () -> Unit,
    onEditStep: (RoutineStep) -> Unit,
    onRemoveStep: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp)
            .fillMaxWidth()
    ) {
        Text("Currently Editing", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(routine.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Starts at ${routine.time}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        if (routine.steps.isEmpty()) {
            EmptyRoutineState(
                onAddStep = onAddClick,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(routine.steps) { step ->
                    RoutineStepItem(
                        step = step,
                        onEdit = { onEditStep(step) },
                        onRemove = { onRemoveStep(step.id) }
                    )
                }
                item {
                    Button(
                        onClick = onAddClick,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add new activity")
                    }
                }
            }
        }
    }
}

@Composable
fun RoutineStepItem(step: RoutineStep, onEdit: () -> Unit, onRemove: () -> Unit) {
    
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(step.type.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                val subText = if (step.productName.isNotEmpty()) step.productName else ""
                if (subText.isNotEmpty()) {
                    Text(subText, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                val minutes = step.duration / 60
                val seconds = step.duration % 60
                val durationText = if (minutes > 0) {
                    "$minutes m ${seconds}s"
                } else {
                    "${seconds}s"
                }
                Text("Jeda: $durationText", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, "Remove", tint = Color.Gray)
                }
            }
        }
    }
}

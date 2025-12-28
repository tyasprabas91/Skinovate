package com.example.skinovate.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.skinovate.data.Routine
import com.example.skinovate.data.RoutineRepository
import com.example.skinovate.screen.components.AddActivitySheet


// Define states for the Bottom Sheet
enum class SheetMode { NONE, EDIT_LIST, ADD_NEW }
// Define which routine type we are currently touching
enum class RoutineType { MORNING, EVENING }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineMakerScreen(navController: NavController) {
    // 1. Observe Live Data from Repository
    val morningRoutine by RoutineRepository.morningRoutine.collectAsState()
    val eveningRoutine by RoutineRepository.eveningRoutine.collectAsState()

    // 2. State Management
    var sheetMode by remember { mutableStateOf(SheetMode.NONE) }
    // We only track the TYPE (Morning/Evening), not the data object itself
    var selectedRoutineType by remember { mutableStateOf<RoutineType?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 3. Helper: Get the Live Routine based on selection
    // This ensures the Bottom Sheet ALWAYS sees the newest data
    val activeRoutine = when (selectedRoutineType) {
        RoutineType.MORNING -> morningRoutine
        RoutineType.EVENING -> eveningRoutine
        null -> null
    }

    // Helper to close sheet
    fun closeSheet() {
        sheetMode = SheetMode.NONE
        selectedRoutineType = null
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Routine Maker", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
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
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Morning Card ---
            RoutineSummaryCard(
                routine = morningRoutine,
                onEditClick = {
                    selectedRoutineType = RoutineType.MORNING
                    sheetMode = SheetMode.EDIT_LIST
                }
            )

            // --- Evening Card ---
            RoutineSummaryCard(
                routine = eveningRoutine,
                onEditClick = {
                    selectedRoutineType = RoutineType.EVENING
                    sheetMode = SheetMode.EDIT_LIST
                }
            )
        }
    }

    // 4. The Bottom Sheet Logic
    if (sheetMode != SheetMode.NONE && activeRoutine != null) {
        ModalBottomSheet(
            onDismissRequest = { closeSheet() },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            if (sheetMode == SheetMode.EDIT_LIST) {
                // Pass 'activeRoutine' which is always up to date!
                EditRoutineListContent(
                    routine = activeRoutine,
                    onAddClick = { sheetMode = SheetMode.ADD_NEW },
                    onRemoveStep = { stepId ->
                        if (selectedRoutineType == RoutineType.MORNING) {
                            RoutineRepository.removeStepFromMorning(stepId)
                        } else {
                            RoutineRepository.removeStepFromEvening(stepId)
                        }
                    }
                )
            } else if (sheetMode == SheetMode.ADD_NEW) {
                AddActivitySheet(
                    isMorningContext = (selectedRoutineType == RoutineType.MORNING),
                    onBack = { sheetMode = SheetMode.EDIT_LIST },
                    onSave = { newStep ->
                        if (selectedRoutineType == RoutineType.MORNING) {
                            RoutineRepository.addStepToMorning(newStep)
                        } else {
                            RoutineRepository.addStepToEvening(newStep)
                        }
                        sheetMode = SheetMode.EDIT_LIST
                    }
                )
            }
        }
    }
}

// ==========================================
//           UI COMPONENTS (Keep these)
// ==========================================

@Composable
fun RoutineSummaryCard(routine: Routine, onEditClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onEditClick() }
    ) {
        Box(modifier = Modifier.padding(20.dp).fillMaxSize()) {
            Column {
                Text("Daily", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text(routine.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                val firstStep = routine.steps.firstOrNull()?.type?.displayName ?: "Nothing yet"
                Text(
                    text = "First step: $firstStep",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.align(Alignment.BottomEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Edit Routine", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun EditRoutineListContent(
    routine: Routine,
    onAddClick: () -> Unit,
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            items(routine.steps) { step ->
                RoutineStepItem(step = step, onRemove = { onRemoveStep(step.id) })
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

@Composable
fun RoutineStepItem(step: com.example.skinovate.data.RoutineStep, onRemove: () -> Unit) {
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
            Column {
                Text(step.type.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                val subText = if (step.productName.isNotEmpty()) step.productName else step.time ?: ""
                if (subText.isNotEmpty()) {
                    Text(subText, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, "Remove", tint = Color.Gray)
            }
        }
    }
}
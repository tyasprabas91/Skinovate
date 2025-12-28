package com.example.skinovate.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skinovate.data.RoutineStep
import com.example.skinovate.data.SkincareStep
import java.util.Calendar
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivitySheet(
    isMorningContext: Boolean,
    onBack: () -> Unit,
    onSave: (RoutineStep) -> Unit
) {
    // 1. Context for the Time Picker Dialog
    val context = LocalContext.current

    // 2. State Variables
    var selectedType by remember { mutableStateOf<SkincareStep?>(null) }
    var productName by remember { mutableStateOf("") }

    // Smart Default Time: 8 AM for Morning, 9 PM for Evening
    var timeString by remember {
        mutableStateOf(if (isMorningContext) "08:00 AM" else "09:00 PM")
    }

    // 3. Time Picker Logic
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour: Int, selectedMinute: Int ->
            // Convert 24h format to "08:30 AM" format
            val amPm = if (selectedHour >= 12) "PM" else "AM"
            val hour12 = if (selectedHour % 12 == 0) 12 else selectedHour % 12
            val minuteStr = String.format("%02d", selectedMinute)
            timeString = "$hour12:$minuteStr $amPm"
        },
        if (isMorningContext) 8 else 21, // Default hour when opening picker
        0,
        false // false = AM/PM mode
    )

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
                text = "Add New Activity",
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
                    onClick = { selectedType = step },
                    label = { Text(step.displayName, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Product Name Input ---
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name (Optional)") },
            placeholder = { Text("e.g. CeraVe") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Time Picker Input ---
        OutlinedTextField(
            value = timeString,
            onValueChange = { }, // Read-only text
            label = { Text("Time") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { timePickerDialog.show() }, // Tap to open clock
            enabled = false, // Disables manual typing
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor = Color.Transparent
            )
        )
        Text("Tap to change time", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // --- Save Button ---
        Button(
            onClick = {
                if (selectedType != null) {
                    val newStep = RoutineStep(
                        id = UUID.randomUUID().toString(),
                        type = selectedType!!,
                        productName = productName,
                        time = timeString
                    )
                    onSave(newStep)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = selectedType != null
        ) {
            Text("Add to Routine")
        }
    }
}
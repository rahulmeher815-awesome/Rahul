package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProblemEntity
import com.example.data.model.ProblemCategory
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemSubmissionScreen(
    onBack: () -> Unit,
    onSubmit: (
        crop: String,
        variety: String,
        location: String,
        category: String,
        description: String,
        startedWhen: String,
        symptoms: String,
        previousTreatment: String,
        urgency: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var crop by remember { mutableStateOf("Paddy (Rice)") }
    var cropVariety by remember { mutableStateOf("Pusa Basmati 1121") }
    var location by remember { mutableStateOf("Karnal, Haryana") }
    var selectedCategory by remember { mutableStateOf(ProblemCategory.PLANT_PATHOLOGY.displayName) }
    var description by remember { mutableStateOf("My paddy leaves are turning yellow and brown from the edges, with spindle-shaped spots.") }
    var startedWhen by remember { mutableStateOf("3 days ago") }
    var previousTreatment by remember { mutableStateOf("Applied 10kg Urea per acre, but symptoms persisted.") }
    var urgency by remember { mutableStateOf("High") }
    var isPhotoAttached by remember { mutableStateOf(true) }

    val commonCrops = listOf("Paddy (Rice)", "Wheat", "Tomato", "Cotton", "Sugarcane", "Potato", "Chilli", "Mustard", "Maize", "Soybean")
    val commonSymptoms = listOf("Yellowing Leaves", "Brown Spindle Lesions", "Leaf Curling", "Wilting & Drying", "Whitefly Swarm", "Stem Borer Holes", "Fruit Rot", "Stunted Growth")
    var selectedSymptoms by remember { mutableStateOf(setOf("Yellowing Leaves", "Brown Spindle Lesions")) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Describe Crop Problem", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Step 1: Provide details for expert matching", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("problem_submission_form"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Pill
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AgriGreenContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AgriConnect analyzes your crop category and symptoms to rank verified specialists with highest diagnostic accuracy.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriGreenOnContainer,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 1. Select Crop
            item {
                Text("Select Crop *", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(commonCrops) { c ->
                        FilterChip(
                            selected = crop == c,
                            onClick = { crop = c },
                            label = { Text(c) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AgriGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // 2. Crop Variety & Location
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = cropVariety,
                        onValueChange = { cropVariety = it },
                        label = { Text("Variety (optional)") },
                        placeholder = { Text("e.g. PB 1121") },
                        modifier = Modifier.weight(1f).testTag("input_crop_variety"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location / District *") },
                        placeholder = { Text("e.g. Karnal, Haryana") },
                        modifier = Modifier.weight(1f).testTag("input_location"),
                        singleLine = true
                    )
                }
            }

            // 3. Problem Category Dropdown
            item {
                Text("Problem Category *", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth().testTag("category_dropdown"),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        ProblemCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName) },
                                onClick = {
                                    selectedCategory = cat.displayName
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 4. Description
            item {
                Text("Problem Description *", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Describe the symptoms: discoloration, patches, pest movements, affected plant parts...") },
                    modifier = Modifier.fillMaxWidth().height(110.dp).testTag("input_description"),
                    maxLines = 4
                )
            }

            // 5. When did the problem start?
            item {
                Text("When did it start? *", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                val timeframes = listOf("Today", "2-3 days ago", "1 week ago", "Over 2 weeks ago")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    timeframes.forEach { t ->
                        FilterChip(
                            selected = startedWhen == t,
                            onClick = { startedWhen = t },
                            label = { Text(t, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AgriGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // 6. Symptoms Observed Chips
            item {
                Text("Symptoms Observed", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val chunked = commonSymptoms.chunked(2)
                    chunked.forEach { rowSymptoms ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowSymptoms.forEach { s ->
                                FilterChip(
                                    selected = selectedSymptoms.contains(s),
                                    onClick = {
                                        selectedSymptoms = if (selectedSymptoms.contains(s)) {
                                            selectedSymptoms - s
                                        } else {
                                            selectedSymptoms + s
                                        }
                                    },
                                    label = { Text(s, fontSize = 12.sp) },
                                    leadingIcon = if (selectedSymptoms.contains(s)) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // 7. Previous Treatment Attempted
            item {
                Text("Previous Treatment Attempted (if any)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = previousTreatment,
                    onValueChange = { previousTreatment = it },
                    placeholder = { Text("e.g. Sprayed chemical pesticide, applied fertilizer, or none") },
                    modifier = Modifier.fillMaxWidth().testTag("input_previous_treatment"),
                    singleLine = true
                )
            }

            // 8. Crop Photos Upload
            item {
                Text("Crop Photos (Recommended for Disease Identification)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isPhotoAttached = !isPhotoAttached },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isPhotoAttached) AgriGreenPrimary else MaterialTheme.colorScheme.outlineVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPhotoAttached) Icons.Default.CheckCircle else Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isPhotoAttached) "Photo Attached: Leaf_Lesions_Closeup.jpg" else "Tap to attach leaf or pest photo",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isPhotoAttached) AgriGreenPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isPhotoAttached) "Clear closeup of diseased leaf margin ready for expert review" else "High quality photos improve diagnosis accuracy by 85%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 9. Urgency Level
            item {
                Text("Urgency Level", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("High", "Medium", "Normal").forEach { u ->
                        val uColor = when (u) {
                            "High" -> UrgencyHighColor
                            "Medium" -> UrgencyMediumColor
                            else -> UrgencyLowColor
                        }
                        FilterChip(
                            selected = urgency == u,
                            onClick = { urgency = u },
                            label = { Text(u, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = uColor,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Submit Button
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val allSymptomsStr = (selectedSymptoms + listOfNotNull(description.takeIf { it.isNotBlank() })).joinToString(", ")
                        onSubmit(
                            crop,
                            cropVariety,
                            location,
                            selectedCategory,
                            description,
                            startedWhen,
                            allSymptomsStr,
                            previousTreatment,
                            urgency
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AgriGreenPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_problem_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Analyze & Find Matching Experts",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

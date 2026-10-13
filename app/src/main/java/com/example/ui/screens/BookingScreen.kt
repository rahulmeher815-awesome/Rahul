package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.local.AvailabilitySlotEntity
import com.example.data.local.ProblemEntity
import com.example.data.model.ExpertDetail
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    expert: ExpertDetail,
    availableSlots: List<AvailabilitySlotEntity>,
    farmerProblems: List<ProblemEntity>,
    farmerFee: Double,
    onBack: () -> Unit,
    onConfirmBooking: (slotId: String, slotDate: String, slotTime: String, problemId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSlot by remember(availableSlots) { mutableStateOf(availableSlots.firstOrNull()) }
    var selectedProblemId by remember(farmerProblems) { mutableStateOf(farmerProblems.firstOrNull()?.id) }
    var selectedPaymentMode by remember { mutableStateOf("UPI / PhonePe / GPay") }

    val consultationPrice = expert.pricePerConsultation
    val totalFarmerPaid = consultationPrice + farmerFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Book Consultation", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("With ${expert.name}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Payable", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${totalFarmerPaid.toInt()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = AgriGreenPrimary)
                        }
                        Button(
                            onClick = {
                                val slot = selectedSlot
                                if (slot != null) {
                                    onConfirmBooking(slot.id, "${slot.dayLabel} (${slot.dateString})", slot.timeSlot, selectedProblemId)
                                }
                            },
                            enabled = selectedSlot != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AgriGreenPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("confirm_and_pay_button")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pay ₹${totalFarmerPaid.toInt()} & Confirm", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("booking_flow_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Expert Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AgriGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(expert.avatarInitials, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(expert.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                if (expert.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedBadgeColor, modifier = Modifier.size(14.dp))
                                }
                            }
                            Text(expert.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Step 1: Select Available Slot
            item {
                Text("1. Select Available Time Slot *", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                if (availableSlots.isEmpty()) {
                    Text("No open slots found for this expert today. Please check back soon.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            items(availableSlots) { slot ->
                val isSelected = selectedSlot?.id == slot.id
                Card(
                    onClick = { selectedSlot = slot },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AgriGreenContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(AgriGreenPrimary)
                    ) else null,
                    modifier = Modifier.fillMaxWidth().testTag("slot_item_${slot.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedSlot = slot },
                                colors = RadioButtonDefaults.colors(selectedColor = AgriGreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${slot.dayLabel} • ${slot.dateString}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = slot.timeSlot,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AgriGreenPrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "OPEN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Step 2: Link Submitted Problem
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("2. Link Your Crop Problem (Optional)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                if (farmerProblems.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        farmerProblems.forEach { prob ->
                            val isSelected = selectedProblemId == prob.id
                            Surface(
                                onClick = { selectedProblemId = prob.id },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) HarvestAmberContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(HarvestAmber)
                                ) else null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedProblemId = prob.id },
                                        colors = RadioButtonDefaults.colors(selectedColor = HarvestAmber)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("${prob.crop} (${prob.category})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                        Text(prob.description, maxLines = 1, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Step 3: Transparent Pricing Breakdown
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("3. Transparent Fee Breakdown", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Expert Consultation Price", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${consultationPrice.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Farmer Platform / Service Fee", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${farmerFee.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount to Pay", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("₹${totalFarmerPaid.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = AgriGreenPrimary)
                        }
                    }
                }
            }

            // Step 4: Simulated Payment Gateway
            item {
                Text("4. Payment Method", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                val modes = listOf("UPI / PhonePe / GPay", "Net Banking (SBI / HDFC / PNB)", "Debit / Credit Cards")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    modes.forEach { mode ->
                        Surface(
                            onClick = { selectedPaymentMode = mode },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = ButtonDefaults.outlinedButtonBorder,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMode == mode,
                                    onClick = { selectedPaymentMode = mode },
                                    colors = RadioButtonDefaults.colors(selectedColor = AgriGreenPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(mode, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

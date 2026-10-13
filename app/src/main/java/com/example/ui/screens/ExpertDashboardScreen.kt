package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.local.BookingEntity
import com.example.data.model.ExpertDetail
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertDashboardScreen(
    expert: ExpertDetail?,
    bookings: List<BookingEntity>,
    slots: List<AvailabilitySlotEntity>,
    onOpenChat: (String) -> Unit,
    onAddSlot: (dayLabel: String, dateString: String, timeSlot: String) -> Unit,
    onDeleteSlot: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddSlotDialog by remember { mutableStateOf(false) }
    var newSlotDay by remember { mutableStateOf("Today") }
    var newSlotDate by remember { mutableStateOf("14 Oct 2026") }
    var newSlotTime by remember { mutableStateOf("06:00 PM – 07:00 PM") }

    val completedBookings = bookings.filter { it.status == "COMPLETED" }
    val totalEarnings = bookings.sumOf { it.expertEarnings }
    val activeBookings = bookings.filter { it.status == "CONFIRMED" }

    if (showAddSlotDialog) {
        AlertDialog(
            onDismissRequest = { showAddSlotDialog = false },
            title = { Text("Add Availability Slot", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select or enter slot timing for farmers to book:", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = newSlotDay,
                        onValueChange = { newSlotDay = it },
                        label = { Text("Day Label (e.g. Today, Tomorrow)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newSlotDate,
                        onValueChange = { newSlotDate = it },
                        label = { Text("Date (e.g. 15 Oct 2026)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newSlotTime,
                        onValueChange = { newSlotTime = it },
                        label = { Text("Time Slot (e.g. 04:00 PM - 05:00 PM)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddSlot(newSlotDay, newSlotDate, newSlotTime)
                        showAddSlotDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("Save Slot")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSlotDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("expert_dashboard_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Expert Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriGreenPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = expert?.name ?: "Dr. Rahul Sharma",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (expert?.isVerified == true) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Expert",
                                        tint = HarvestAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                text = expert?.title ?: "Senior Plant Pathology Expert",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (expert?.isVerified == true) Color.White.copy(alpha = 0.2f) else UrgencyHighColor.copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = if (expert?.isVerified == true) "VERIFIED EXPERT" else "PENDING VERIFICATION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Manage your consultation schedule, reply to farmers, review diagnosed plant problems, and track payouts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Metrics Grid (Earnings, Rating, Consultations)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Net Earnings",
                    value = "₹${totalEarnings.toInt()}",
                    subtitle = "Platform fee auto-settled",
                    icon = Icons.Default.AccountBalanceWallet,
                    color = AgriGreenPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Active Bookings",
                    value = "${activeBookings.size}",
                    subtitle = "Ready to chat",
                    icon = Icons.Default.Chat,
                    color = WaterBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Farmer Rating",
                    value = "${expert?.rating ?: 4.9} ★",
                    subtitle = "${expert?.reviewCount ?: 48} reviews",
                    icon = Icons.Default.Star,
                    color = HarvestAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Earnings & Payout Breakdown Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = AgriGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Earnings & Platform Fee Breakdown", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    val grossConsultations = bookings.sumOf { it.consultationPrice }
                    val totalPlatformDeduction = bookings.sumOf { it.expertPlatformFee }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Gross Consultations Billed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹${grossConsultations.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Platform Service Fee Deducted (₹30/booking)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("-₹${totalPlatformDeduction.toInt()}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Net Expert Payout", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("₹${totalEarnings.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = AgriGreenPrimary)
                    }
                }
            }
        }

        // Active Farmer Consultations
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Farmer Consultations (${activeBookings.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (activeBookings.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No active consultation sessions right now.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(activeBookings) { booking ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Farmer Ramesh Kumar", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Slot: ${booking.slotDate} • ${booking.slotTime}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Your Earning: ₹${booking.expertEarnings.toInt()}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
                        }

                        Button(
                            onClick = { onOpenChat(booking.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("expert_open_chat_${booking.id}")
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Chat")
                        }
                    }
                }
            }
        }

        // Availability Calendar & Slot Manager
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Availability Slots (${slots.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { showAddSlotDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("add_slot_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Slot", fontSize = 12.sp)
                }
            }
        }

        items(slots) { slot ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (slot.isBooked) MaterialTheme.colorScheme.outlineVariant else AgriGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (slot.isBooked) Icons.Default.EventBusy else Icons.Default.EventAvailable,
                                contentDescription = null,
                                tint = if (slot.isBooked) MaterialTheme.colorScheme.outline else AgriGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("${slot.dayLabel} • ${slot.dateString}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(slot.timeSlot, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (slot.isBooked) UrgencyHighColor.copy(alpha = 0.12f) else AgriGreenPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = if (slot.isBooked) "BOOKED" else "AVAILABLE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (slot.isBooked) UrgencyHighColor else AgriGreenPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { onDeleteSlot(slot.id) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
        }
    }
}

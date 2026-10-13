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
import com.example.data.local.BookingEntity
import com.example.data.local.PlatformSettingsEntity
import com.example.data.local.UserEntity
import com.example.data.model.ExpertDetail
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    users: List<UserEntity>,
    experts: List<ExpertDetail>,
    bookings: List<BookingEntity>,
    settings: PlatformSettingsEntity?,
    onToggleVerification: (expertId: String, isVerified: Boolean) -> Unit,
    onUpdateFees: (farmerFee: Double, expertFee: Double) -> Unit,
    onToggleSuspension: (userId: String, isSuspended: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var adminTab by remember { mutableStateOf(0) } // 0: Analytics, 1: Expert Verification, 2: Fee Settings, 3: User Management

    var farmerFeeInput by remember(settings) { mutableStateOf(settings?.farmerFee?.toInt()?.toString() ?: "30") }
    var expertFeeInput by remember(settings) { mutableStateOf(settings?.expertFee?.toInt()?.toString() ?: "30") }
    var feeSavedMessage by remember { mutableStateOf<String?>(null) }

    // Analytics calculations
    val totalConsultations = bookings.size
    val completedConsultations = bookings.count { it.status == "COMPLETED" }
    val gtv = bookings.sumOf { it.totalFarmerPaid }
    val platformRevenue = bookings.sumOf { it.platformRevenue }
    val expertPayouts = bookings.sumOf { it.expertEarnings }
    val verifiedExpertsCount = experts.count { it.isVerified }
    val unverifiedExpertsCount = experts.count { !it.isVerified }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AgriConnect Admin Console", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Text("Platform Analytics & Verification Management", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AgriGreenPrimary
                    ) {
                        Text(
                            text = "LIVE SYSTEM",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Navigation Tabs for Admin
        item {
            TabRow(
                selectedTabIndex = adminTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AgriGreenPrimary
            ) {
                Tab(selected = adminTab == 0, onClick = { adminTab = 0 }, text = { Text("Analytics", fontSize = 12.sp) })
                Tab(
                    selected = adminTab == 1,
                    onClick = { adminTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Verification", fontSize = 12.sp)
                            if (unverifiedExpertsCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Badge(containerColor = HarvestAmber) {
                                    Text("$unverifiedExpertsCount")
                                }
                            }
                        }
                    }
                )
                Tab(selected = adminTab == 2, onClick = { adminTab = 2 }, text = { Text("Fees & Rates", fontSize = 12.sp) })
                Tab(selected = adminTab == 3, onClick = { adminTab = 3 }, text = { Text("Users", fontSize = 12.sp) })
            }
        }

        when (adminTab) {
            0 -> {
                // ANALYTICS TAB
                item {
                    Text("Financial & Consultation Metrics", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdminStatCard("Gross Trans. Value", "₹${gtv.toInt()}", "Total farmer spend", Icons.Default.CurrencyRupee, AgriGreenPrimary, Modifier.weight(1f))
                        AdminStatCard("Platform Revenue", "₹${platformRevenue.toInt()}", "Dual fee income", Icons.Default.Savings, HarvestAmber, Modifier.weight(1f))
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdminStatCard("Total Consultations", "$totalConsultations", "$completedConsultations completed", Icons.Default.Forum, WaterBlue, Modifier.weight(1f))
                        AdminStatCard("Expert Payouts", "₹${expertPayouts.toInt()}", "Net to agronomists", Icons.Default.SendToMobile, AgriGreenPrimary, Modifier.weight(1f))
                    }
                }

                // Community User Counts
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("User Demographics", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Registered Users", style = MaterialTheme.typography.bodySmall)
                                Text("${users.size}", fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Agriculture Experts", style = MaterialTheme.typography.bodySmall)
                                Text("${experts.size} ($verifiedExpertsCount verified)", fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pending Expert Verifications", style = MaterialTheme.typography.bodySmall)
                                Text("$unverifiedExpertsCount pending", fontWeight = FontWeight.Bold, color = if (unverifiedExpertsCount > 0) HarvestAmber else AgriGreenPrimary)
                            }
                        }
                    }
                }

                // Most Requested Crops & Domains
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Top Requested Crops & Categories", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))
                            listOf(
                                Pair("Paddy (Rice) — Plant Pathology", "42% of inquiries"),
                                Pair("Tomato — Vegetable Diseases & Pests", "26% of inquiries"),
                                Pair("Wheat — Rust & Soil Fertility", "18% of inquiries"),
                                Pair("Cotton — Bollworm Entomology", "14% of inquiries")
                            ).forEach { (crop, pct) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(crop, style = MaterialTheme.typography.bodySmall)
                                    Text(pct, fontWeight = FontWeight.SemiBold, color = AgriGreenPrimary, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // EXPERT VERIFICATION TAB
                item {
                    Text(
                        text = "Expert Verification Requests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Review submitted credentials and grant verified badges to certified agronomists.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(experts) { expert ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth().testTag("admin_expert_item_${expert.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(AgriGreenPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(expert.avatarInitials, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(expert.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                            if (expert.isVerified) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedBadgeColor, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                        Text(expert.qualification, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(expert.institution, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (expert.isVerified) AgriGreenContainer else HarvestAmberContainer
                                ) {
                                    Text(
                                        text = if (expert.isVerified) "VERIFIED" else "PENDING",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (expert.isVerified) AgriGreenOnContainer else HarvestAmberOnContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Uploaded Doc: ${expert.verificationDocName}", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                if (!expert.isVerified) {
                                    Button(
                                        onClick = { onToggleVerification(expert.id, true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("verify_expert_${expert.id}")
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Verify & Approve Badge", fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { onToggleVerification(expert.id, false) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Revoke Verification", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // PLATFORM FEES TAB
                item {
                    Text("Configurable Platform Fees", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Adjust service charges levied on farmers and experts per consultation session.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = farmerFeeInput,
                                onValueChange = { farmerFeeInput = it },
                                label = { Text("Farmer Platform Fee (₹)") },
                                placeholder = { Text("30") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("input_farmer_fee")
                            )

                            OutlinedTextField(
                                value = expertFeeInput,
                                onValueChange = { expertFeeInput = it },
                                label = { Text("Expert Service Fee Deduction (₹)") },
                                placeholder = { Text("30") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("input_expert_fee")
                            )

                            if (feeSavedMessage != null) {
                                Text(
                                    text = feeSavedMessage ?: "",
                                    color = AgriGreenPrimary,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Button(
                                onClick = {
                                    val fFee = farmerFeeInput.toDoubleOrNull() ?: 30.0
                                    val eFee = expertFeeInput.toDoubleOrNull() ?: 30.0
                                    onUpdateFees(fFee, eFee)
                                    feeSavedMessage = "Platform fees updated successfully! (Farmer: ₹${fFee.toInt()}, Expert: ₹${eFee.toInt()})"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("save_platform_fees_button")
                            ) {
                                Text("Save Platform Fee Rates", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            3 -> {
                // USER MANAGEMENT TAB
                item {
                    Text("Registered Users Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(users) { user ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
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
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(user.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (user.role == "EXPERT") AgriGreenContainer else HarvestAmberContainer
                                    ) {
                                        Text(
                                            text = user.role,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (user.role == "EXPERT") AgriGreenOnContainer else HarvestAmberOnContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(user.email, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(user.location, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Switch(
                                checked = !user.isSuspended,
                                onCheckedChange = { isActive ->
                                    onToggleSuspension(user.id, !isActive)
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = AgriGreenPrimary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(
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
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        }
    }
}

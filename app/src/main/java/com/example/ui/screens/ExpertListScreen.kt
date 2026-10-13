package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.ExpertDetail
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExpertSortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertListScreen(
    experts: List<ExpertDetail>,
    searchQuery: String,
    selectedCategory: String?,
    selectedCrop: String?,
    sortOption: ExpertSortOption,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String?) -> Unit,
    onCropChange: (String?) -> Unit,
    onSortChange: (ExpertSortOption) -> Unit,
    onBack: () -> Unit,
    onExpertSelected: (ExpertDetail) -> Unit,
    onBookExpert: (ExpertDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Plant Pathology", "Entomology", "Soil Science", "Horticulture", "Irrigation", "Organic Farming")
    val crops = listOf("All", "Paddy", "Wheat", "Tomato", "Cotton", "Sugarcane", "Potato")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Find Agriculture Experts", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("${experts.size} Specialists Available", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search by name, crop, domain, location...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AgriGreenPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("expert_search_input")
            )

            // Category Chips Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = if (cat == "All") selectedCategory == null else selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryChange(if (cat == "All") null else cat) },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AgriGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Crop Chips Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                items(crops) { cr ->
                    val isSelected = if (cr == "All") selectedCrop == null else selectedCrop == cr
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCropChange(if (cr == "All") null else cr) },
                        label = { Text("Crop: $cr", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HarvestAmber,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Sort Selector Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sorted by:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ExpertSortOption.values().forEach { sort ->
                        FilterChip(
                            selected = sortOption == sort,
                            onClick = { onSortChange(sort) },
                            label = { Text(sort.label, fontSize = 11.sp) }
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

            // Experts List
            if (experts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = AgriOutline, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No matching agricultural experts found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Try relaxing filters or search terms.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().testTag("expert_directory_list")
                ) {
                    items(experts) { expert ->
                        ExpertCardItem(
                            expert = expert,
                            onCardClick = { onExpertSelected(expert) },
                            onBookClick = { onBookExpert(expert) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpertCardItem(
    expert: ExpertDetail,
    onCardClick: () -> Unit,
    onBookClick: () -> Unit
) {
    Card(
        onClick = onCardClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().testTag("expert_card_${expert.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AgriGreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = expert.avatarInitials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = expert.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (expert.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = VerifiedBadgeColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = expert.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${expert.institution} • ${expert.experienceYears} yrs exp",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Crops Specialized Tag Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                expert.specializedCrops.take(3).forEach { crop ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AgriGreenContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = crop,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriGreenOnContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rating & Price & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = HarvestAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${expert.rating} (${expert.reviewCount})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "₹${expert.pricePerConsultation.toInt()} / session",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGreenPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onCardClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Profile", fontSize = 12.sp)
                    }
                    Button(
                        onClick = onBookClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text("Book", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

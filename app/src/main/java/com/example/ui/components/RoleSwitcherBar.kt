package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenOnContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.HarvestAmber
import com.example.ui.viewmodel.Screen

@Composable
fun RoleSwitcherBar(
    currentRole: UserRole,
    currentScreen: Screen,
    notificationCount: Int,
    onRoleSelected: (UserRole) -> Unit,
    onLandingClicked: () -> Unit,
    onNotificationsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Top Row: Logo & Notifications
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onLandingClicked() }
                        .padding(vertical = 4.dp)
                        .testTag("app_logo_title")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AgriGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "AgriConnect Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AgriConnect",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AgriGreenContainer
                            ) {
                                Text(
                                    text = "VERIFIED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriGreenOnContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Expert Agriculture Advisory",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Landing shortcut button
                    IconButton(
                        onClick = onLandingClicked,
                        modifier = Modifier.testTag("landing_page_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Landing Page",
                            tint = if (currentScreen is Screen.Landing) AgriGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Notification Icon with badge
                    IconButton(
                        onClick = onNotificationsClicked,
                        modifier = Modifier.testTag("notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (notificationCount > 0) {
                                    Badge(containerColor = HarvestAmber) {
                                        Text("$notificationCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom Row: Role Selector Switch (Farmer / Expert / Admin)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RoleTabButton(
                    label = "Farmer",
                    icon = Icons.Default.Agriculture,
                    isSelected = currentRole == UserRole.FARMER,
                    onClick = { onRoleSelected(UserRole.FARMER) },
                    modifier = Modifier.weight(1f).testTag("role_farmer_button")
                )
                Spacer(modifier = Modifier.width(4.dp))
                RoleTabButton(
                    label = "Expert",
                    icon = Icons.Default.Psychology,
                    isSelected = currentRole == UserRole.EXPERT,
                    onClick = { onRoleSelected(UserRole.EXPERT) },
                    modifier = Modifier.weight(1f).testTag("role_expert_button")
                )
                Spacer(modifier = Modifier.width(4.dp))
                RoleTabButton(
                    label = "Admin",
                    icon = Icons.Default.AdminPanelSettings,
                    isSelected = currentRole == UserRole.ADMIN,
                    onClick = { onRoleSelected(UserRole.ADMIN) },
                    modifier = Modifier.weight(1f).testTag("role_admin_button")
                )
            }
        }
    }
}

@Composable
private fun RoleTabButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) AgriGreenPrimary else Color.Transparent,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.local.MessageEntity
import com.example.data.model.UserRole
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    booking: BookingEntity,
    currentRole: UserRole,
    messages: List<MessageEntity>,
    onBack: () -> Unit,
    onSendMessage: (text: String, attachment: String?) -> Unit,
    onCompleteAndReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messageInput by remember { mutableStateOf("") }
    var attachedPhotoText by remember { mutableStateOf<String?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }

    val quickAdviceChips = if (currentRole == UserRole.EXPERT) {
        listOf("Can you upload leaf underside?", "Avoid applying excess Nitrogen", "Apply Tricyclazole 0.6g/L", "Improve trench drainage")
    } else {
        listOf("Uploaded photo of leaf stem", "When should I spray again?", "Is bio-pesticide safe to mix?")
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Consultation") },
            text = { Text("AgriConnect protects both farmers and experts. You can report incorrect advice, harassment, or payment issues for admin review.") },
            confirmButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Submit Report", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (currentRole == UserRole.FARMER) "Dr. Rahul Sharma" else "Farmer Ramesh Kumar",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AgriGreenContainer
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriGreenOnContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${booking.slotDate} • ${booking.slotTime}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (currentRole == UserRole.FARMER) {
                        TextButton(
                            onClick = onCompleteAndReview,
                            modifier = Modifier.testTag("complete_and_review_button")
                        ) {
                            Text("Finish & Rate", color = AgriGreenPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Quick Action Guidance Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickAdviceChips.take(2).forEach { chip ->
                            SuggestionChip(
                                onClick = { messageInput = chip },
                                label = { Text(chip, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Attached Photo Indicator
                    if (attachedPhotoText != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Attached: $attachedPhotoText", style = MaterialTheme.typography.labelSmall, color = AgriGreenPrimary)
                            }
                            IconButton(onClick = { attachedPhotoText = null }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    // Message Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                attachedPhotoText = "Paddy_Leaf_Underside_Sample.jpg"
                            },
                            modifier = Modifier.testTag("attach_crop_photo_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Attach Crop Photo",
                                tint = AgriGreenPrimary
                            )
                        }

                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text("Type message, symptoms, or dosage...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            maxLines = 3,
                            shape = RoundedCornerShape(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (messageInput.isNotBlank() || attachedPhotoText != null) {
                                    onSendMessage(messageInput, attachedPhotoText)
                                    messageInput = ""
                                    attachedPhotoText = null
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(AgriGreenPrimary)
                                .testTag("send_chat_message_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White
                            )
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
                .testTag("chat_messages_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isMyMessage = if (currentRole == UserRole.FARMER) {
                    msg.senderRole == "FARMER"
                } else {
                    msg.senderRole == "EXPERT"
                }
                val isSystem = msg.senderRole == "SYSTEM"

                if (isSystem) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    ChatBubble(
                        message = msg,
                        isMyMessage = isMyMessage
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: MessageEntity,
    isMyMessage: Boolean
) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMyMessage) 16.dp else 2.dp,
                bottomEnd = if (isMyMessage) 2.dp else 16.dp
            ),
            color = if (isMyMessage) AgriGreenPrimary else MaterialTheme.colorScheme.surface,
            tonalElevation = if (isMyMessage) 0.dp else 2.dp,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // If attachment description exists
                if (!message.attachmentDescription.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isMyMessage) Color.White.copy(alpha = 0.2f) else AgriGreenContainer.copy(alpha = 0.6f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = if (isMyMessage) Color.White else AgriGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = message.attachmentDescription,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isMyMessage) Color.White else AgriGreenOnContainer
                            )
                        }
                    }
                }

                if (message.text.isNotBlank()) {
                    Text(
                        text = message.text,
                        color = if (isMyMessage) Color.White else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedTime,
                        fontSize = 10.sp,
                        color = if (isMyMessage) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isMyMessage) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

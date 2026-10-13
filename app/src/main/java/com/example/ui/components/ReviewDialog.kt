package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookingEntity
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.HarvestAmber

@Composable
fun ReviewDialog(
    booking: BookingEntity,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var reviewComment by remember { mutableStateOf("Excellent consultation. The fungicide recommendation and dosage was accurate and saved my crop.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Rate Consultation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "How was your guidance session?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Stars Row
                Row(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star $star",
                            tint = if (star <= rating) HarvestAmber else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { rating = star }
                                .padding(2.dp)
                                .testTag("star_rating_$star")
                        )
                    }
                }

                Text(
                    text = when (rating) {
                        5 -> "5/5 — Highly Effective & Knowledgeable"
                        4 -> "4/5 — Very Helpful Guidance"
                        3 -> "3/5 — Average Consultation"
                        2 -> "2/5 — Needs Improvement"
                        else -> "1/5 — Unsatisfactory"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AgriGreenPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = reviewComment,
                    onValueChange = { reviewComment = it },
                    placeholder = { Text("Write your feedback (optional)...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("review_comment_input"),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, reviewComment) },
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_review_button")
            ) {
                Text("Submit Review", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

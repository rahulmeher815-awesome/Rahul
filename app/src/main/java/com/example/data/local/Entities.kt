package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // FARMER, EXPERT, ADMIN
    val location: String,
    val avatarInitials: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isSuspended: Boolean = false
)

@Entity(
    tableName = "expert_profiles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["expertId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["expertId"], unique = true)]
)
data class ExpertProfileEntity(
    @PrimaryKey val expertId: String,
    val title: String,
    val qualification: String,
    val institution: String,
    val experienceYears: Int,
    val bio: String,
    val pricePerConsultation: Double, // in INR ₹
    val rating: Double,
    val reviewCount: Int,
    val consultationCount: Int,
    val isVerified: Boolean,
    val verificationDocName: String,
    val languagesSpoken: String, // comma-separated
    val specializedCrops: String, // comma-separated
    val expertiseCategories: String // comma-separated
)

@Entity(tableName = "problems")
data class ProblemEntity(
    @PrimaryKey val id: String,
    val farmerId: String,
    val crop: String,
    val cropVariety: String,
    val location: String,
    val category: String,
    val description: String,
    val startedWhen: String,
    val symptoms: String,
    val previousTreatment: String,
    val urgency: String, // High, Medium, Low
    val status: String, // OPEN, IN_CONSULTATION, RESOLVED
    val createdAt: Long = System.currentTimeMillis(),
    val imageUri: String? = null
)

@Entity(
    tableName = "availability_slots",
    indices = [Index(value = ["expertId"])]
)
data class AvailabilitySlotEntity(
    @PrimaryKey val id: String,
    val expertId: String,
    val dayLabel: String, // "Today", "Tomorrow", "Monday", etc.
    val dateString: String, // "14 Oct 2026"
    val timeSlot: String, // "10:00 AM – 11:00 AM"
    val isBooked: Boolean = false
)

@Entity(
    tableName = "bookings",
    indices = [Index(value = ["farmerId"]), Index(value = ["expertId"])]
)
data class BookingEntity(
    @PrimaryKey val id: String,
    val problemId: String?,
    val farmerId: String,
    val expertId: String,
    val consultationType: String, // "Chat Consultation"
    val slotDate: String,
    val slotTime: String,
    val consultationPrice: Double,
    val farmerPlatformFee: Double,
    val totalFarmerPaid: Double,
    val expertPlatformFee: Double,
    val expertEarnings: Double,
    val platformRevenue: Double,
    val status: String, // "CONFIRMED", "COMPLETED", "CANCELLED"
    val bookedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "messages",
    indices = [Index(value = ["bookingId"])]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val bookingId: String,
    val senderId: String,
    val senderRole: String, // FARMER, EXPERT
    val text: String,
    val attachmentDescription: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(
    tableName = "reviews",
    indices = [Index(value = ["expertId"])]
)
data class ReviewEntity(
    @PrimaryKey val id: String,
    val bookingId: String,
    val expertId: String,
    val farmerId: String,
    val farmerName: String,
    val rating: Int, // 1 to 5
    val reviewText: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFlagged: Boolean = false
)

@Entity(
    tableName = "notifications",
    indices = [Index(value = ["userId"])]
)
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // BOOKING, MESSAGE, PAYMENT, REVIEW, VERIFY
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "platform_settings")
data class PlatformSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val farmerFee: Double = 30.0, // ₹30 flat fee
    val expertFee: Double = 30.0, // ₹30 flat fee
    val isDisputeProtectionActive: Boolean = true
)

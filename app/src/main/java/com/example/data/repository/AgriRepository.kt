package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.util.UUID

class AgriRepository(private val dao: AgriDao) {

    suspend fun seedDatabaseIfEmpty() {
        val existing = dao.getUserById("farmer_1")
        if (existing == null) {
            dao.insertPlatformSettings(SeedData.defaultSettings)
            dao.insertUser(SeedData.userFarmer)
            dao.insertUser(SeedData.userAdmin)
            dao.insertUsers(SeedData.expertUsers)
            dao.insertExpertProfiles(SeedData.expertProfiles)
            dao.insertSlots(SeedData.generateInitialSlots())
            SeedData.sampleProblems.forEach { dao.insertProblem(it) }
            dao.insertBooking(SeedData.sampleBooking)
            dao.insertMessages(SeedData.sampleMessages)
            dao.insertReviews(SeedData.sampleReviews)
            dao.insertNotifications(SeedData.sampleNotifications)
        }
    }

    // Combined Expert Details Flow
    fun getAllExpertDetailsFlow(): Flow<List<ExpertDetail>> {
        return combine(
            dao.getAllUsersFlow(),
            dao.getAllExpertProfilesFlow()
        ) { users, profiles ->
            val userMap = users.associateBy { it.id }
            profiles.mapNotNull { profile ->
                val user = userMap[profile.expertId] ?: return@mapNotNull null
                ExpertDetail(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    phone = user.phone,
                    location = user.location,
                    avatarInitials = user.avatarInitials,
                    title = profile.title,
                    qualification = profile.qualification,
                    institution = profile.institution,
                    experienceYears = profile.experienceYears,
                    bio = profile.bio,
                    pricePerConsultation = profile.pricePerConsultation,
                    rating = profile.rating,
                    reviewCount = profile.reviewCount,
                    consultationCount = profile.consultationCount,
                    isVerified = profile.isVerified,
                    verificationDocName = profile.verificationDocName,
                    languagesSpoken = profile.languagesSpoken.split(",").map { it.trim() },
                    specializedCrops = profile.specializedCrops.split(",").map { it.trim() },
                    expertiseCategories = profile.expertiseCategories.split(",").map { it.trim() }
                )
            }
        }
    }

    // Recommendation / Matching Logic
    suspend fun matchExpertsForProblem(problem: ProblemEntity): List<ExpertMatchResult> {
        val allExperts = getAllExpertDetailsFlow().first()
        val problemCrop = problem.crop.lowercase()
        val problemCategory = problem.category.lowercase()
        val problemLocation = problem.location.lowercase()

        return allExperts.map { expert ->
            var score = 50
            val reasons = mutableListOf<String>()

            // 1. Expertise category matching
            val hasCategoryMatch = expert.expertiseCategories.any {
                problemCategory.contains(it.lowercase()) || it.lowercase().contains(problemCategory.take(10))
            }
            if (hasCategoryMatch) {
                score += 28
                reasons.add("Matches category: ${problem.category}")
            }

            // 2. Crop specialization matching
            val hasCropMatch = expert.specializedCrops.any {
                problemCrop.contains(it.lowercase()) || it.lowercase().contains(problemCrop.take(4))
            }
            if (hasCropMatch) {
                score += 15
                reasons.add("Specialized in ${problem.crop}")
            }

            // 3. Location / State proximity
            val hasLocationMatch = expert.location.lowercase().split(",").any {
                problemLocation.contains(it.trim())
            }
            if (hasLocationMatch) {
                score += 5
                reasons.add("Region relevance: ${expert.location}")
            }

            // 4. Verification and High Rating
            if (expert.isVerified) {
                score += 4
                reasons.add("Verified Agricultural Specialist")
            }
            if (expert.rating >= 4.8) {
                score += 3
                reasons.add("Top Rated (${expert.rating}★)")
            }

            val finalScore = score.coerceIn(65, 98)
            ExpertMatchResult(
                expert = expert,
                matchScore = finalScore,
                matchReasons = reasons
            )
        }.sortedByDescending { it.matchScore }
    }

    // Fee Calculation
    suspend fun calculateFees(consultationPrice: Double): FeeCalculation {
        val settings = dao.getPlatformSettings() ?: SeedData.defaultSettings
        val farmerFee = settings.farmerFee
        val expertFee = settings.expertFee
        val totalFarmerPaid = consultationPrice + farmerFee
        val expertEarnings = (consultationPrice - expertFee).coerceAtLeast(0.0)
        val platformRevenue = farmerFee + expertFee

        return FeeCalculation(
            consultationPrice = consultationPrice,
            farmerFee = farmerFee,
            totalFarmerPaid = totalFarmerPaid,
            expertFee = expertFee,
            expertEarnings = expertEarnings,
            platformRevenue = platformRevenue
        )
    }

    // Submit Problem
    suspend fun submitProblem(
        farmerId: String,
        crop: String,
        cropVariety: String,
        location: String,
        category: String,
        description: String,
        startedWhen: String,
        symptoms: String,
        previousTreatment: String,
        urgency: String,
        imageUri: String? = null
    ): ProblemEntity {
        val problem = ProblemEntity(
            id = "prob_${UUID.randomUUID().toString().take(8)}",
            farmerId = farmerId,
            crop = crop,
            cropVariety = cropVariety,
            location = location,
            category = category,
            description = description,
            startedWhen = startedWhen,
            symptoms = symptoms,
            previousTreatment = previousTreatment,
            urgency = urgency,
            status = "OPEN",
            imageUri = imageUri
        )
        dao.insertProblem(problem)

        // Notification for farmer
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                userId = farmerId,
                title = "Problem Submitted",
                message = "Your problem for $crop has been recorded. Matching experts are ready for consultation.",
                type = "BOOKING"
            )
        )
        return problem
    }

    // Create Booking
    suspend fun createBooking(
        problemId: String?,
        farmerId: String,
        expertId: String,
        slotId: String,
        slotDate: String,
        slotTime: String,
        consultationPrice: Double
    ): BookingEntity {
        val fee = calculateFees(consultationPrice)
        val booking = BookingEntity(
            id = "book_${UUID.randomUUID().toString().take(8)}",
            problemId = problemId,
            farmerId = farmerId,
            expertId = expertId,
            consultationType = "Chat Consultation",
            slotDate = slotDate,
            slotTime = slotTime,
            consultationPrice = fee.consultationPrice,
            farmerPlatformFee = fee.farmerFee,
            totalFarmerPaid = fee.totalFarmerPaid,
            expertPlatformFee = fee.expertFee,
            expertEarnings = fee.expertEarnings,
            platformRevenue = fee.platformRevenue,
            status = "CONFIRMED"
        )
        dao.insertBooking(booking)
        dao.markSlotBooked(slotId)

        if (problemId != null) {
            dao.updateProblemStatus(problemId, "IN_CONSULTATION")
        }

        // Welcome message in chat
        dao.insertMessage(
            MessageEntity(
                id = "msg_${UUID.randomUUID().toString().take(8)}",
                bookingId = booking.id,
                senderId = "system",
                senderRole = "SYSTEM",
                text = "Consultation session initiated. Farmer paid ₹${fee.totalFarmerPaid.toInt()} (Consultation: ₹${fee.consultationPrice.toInt()} + Platform fee: ₹${fee.farmerFee.toInt()}). Both parties can now exchange messages, diagnostic guidance, and crop photos.",
                timestamp = System.currentTimeMillis()
            )
        )

        // Farmer notification
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                userId = farmerId,
                title = "Consultation Confirmed",
                message = "Your session is confirmed for $slotDate at $slotTime. You can chat with your expert.",
                type = "BOOKING"
            )
        )

        // Expert notification
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                userId = expertId,
                title = "New Consultation Booking",
                message = "A farmer booked a consultation for $slotDate ($slotTime). Earnings: ₹${fee.expertEarnings.toInt()}.",
                type = "BOOKING"
            )
        )

        return booking
    }

    // Send Message
    suspend fun sendMessage(
        bookingId: String,
        senderId: String,
        senderRole: String,
        text: String,
        attachmentDesc: String? = null
    ) {
        val message = MessageEntity(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            bookingId = bookingId,
            senderId = senderId,
            senderRole = senderRole,
            text = text,
            attachmentDescription = attachmentDesc
        )
        dao.insertMessage(message)
    }

    // Complete Consultation & Submit Review
    suspend fun submitReview(
        bookingId: String,
        expertId: String,
        farmerId: String,
        farmerName: String,
        rating: Int,
        reviewText: String
    ) {
        val review = ReviewEntity(
            id = "rev_${UUID.randomUUID().toString().take(8)}",
            bookingId = bookingId,
            expertId = expertId,
            farmerId = farmerId,
            farmerName = farmerName,
            rating = rating,
            reviewText = reviewText
        )
        dao.insertReview(review)
        dao.updateBookingStatus(bookingId, "COMPLETED")

        // Recalculate expert rating
        val expertReviews = dao.getReviewsForExpertFlow(expertId).first()
        val avgRating = expertReviews.map { it.rating }.average()
        val formattedRating = String.format("%.1f", avgRating).toDoubleOrNull() ?: 4.8
        dao.updateExpertRating(expertId, formattedRating, expertReviews.size)

        // Notify expert
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                userId = expertId,
                title = "New Review Received",
                message = "$farmerName left you a $rating★ review: \"$reviewText\"",
                type = "REVIEW"
            )
        )
    }

    // Admin updates
    suspend fun setExpertVerification(expertId: String, isVerified: Boolean) {
        dao.updateExpertVerification(expertId, isVerified)
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                userId = expertId,
                title = if (isVerified) "Profile Verified!" else "Verification Status Updated",
                message = if (isVerified) "Congratulations! Your credentials have been verified by AgriConnect Admin. The verified badge is now active on your profile." else "Your verification status has been suspended pending review.",
                type = "VERIFY"
            )
        )
    }

    suspend fun updatePlatformFees(farmerFee: Double, expertFee: Double) {
        dao.insertPlatformSettings(
            PlatformSettingsEntity(
                id = 1,
                farmerFee = farmerFee,
                expertFee = expertFee
            )
        )
    }

    suspend fun toggleUserSuspension(userId: String, isSuspended: Boolean) {
        dao.updateUserSuspension(userId, isSuspended)
    }

    // Availability slot management for Expert
    suspend fun addAvailabilitySlot(expertId: String, dayLabel: String, dateString: String, timeSlot: String) {
        val slot = AvailabilitySlotEntity(
            id = "slot_${UUID.randomUUID().toString().take(8)}",
            expertId = expertId,
            dayLabel = dayLabel,
            dateString = dateString,
            timeSlot = timeSlot,
            isBooked = false
        )
        dao.insertSlot(slot)
    }

    suspend fun deleteAvailabilitySlot(slotId: String) {
        dao.deleteSlot(slotId)
    }

    // Flows access
    fun getProblemsForFarmerFlow(farmerId: String) = dao.getProblemsForFarmerFlow(farmerId)
    fun getAllProblemsFlow() = dao.getAllProblemsFlow()
    fun getBookingsForFarmerFlow(farmerId: String) = dao.getBookingsForFarmerFlow(farmerId)
    fun getBookingsForExpertFlow(expertId: String) = dao.getBookingsForExpertFlow(expertId)
    fun getAllBookingsFlow() = dao.getAllBookingsFlow()
    fun getAvailableSlotsForExpertFlow(expertId: String) = dao.getAvailableSlotsForExpertFlow(expertId)
    fun getAllSlotsForExpertFlow(expertId: String) = dao.getAllSlotsForExpertFlow(expertId)
    fun getMessagesForBookingFlow(bookingId: String) = dao.getMessagesForBookingFlow(bookingId)
    fun getReviewsForExpertFlow(expertId: String) = dao.getReviewsForExpertFlow(expertId)
    fun getAllReviewsFlow() = dao.getAllReviewsFlow()
    fun getNotificationsForUserFlow(userId: String) = dao.getNotificationsForUserFlow(userId)
    fun getPlatformSettingsFlow() = dao.getPlatformSettingsFlow()
    fun getAllUsersFlow() = dao.getAllUsersFlow()
}

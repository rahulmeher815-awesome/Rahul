package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AgriDao {

    // Users
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("UPDATE users SET isSuspended = :isSuspended WHERE id = :userId")
    suspend fun updateUserSuspension(userId: String, isSuspended: Boolean)

    // Expert Profiles
    @Query("SELECT * FROM expert_profiles WHERE expertId = :expertId")
    suspend fun getExpertProfile(expertId: String): ExpertProfileEntity?

    @Query("SELECT * FROM expert_profiles")
    fun getAllExpertProfilesFlow(): Flow<List<ExpertProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpertProfile(profile: ExpertProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpertProfiles(profiles: List<ExpertProfileEntity>)

    @Query("UPDATE expert_profiles SET isVerified = :isVerified WHERE expertId = :expertId")
    suspend fun updateExpertVerification(expertId: String, isVerified: Boolean)

    @Query("UPDATE expert_profiles SET rating = :rating, reviewCount = :reviewCount WHERE expertId = :expertId")
    suspend fun updateExpertRating(expertId: String, rating: Double, reviewCount: Int)

    // Problems
    @Query("SELECT * FROM problems WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getProblemsForFarmerFlow(farmerId: String): Flow<List<ProblemEntity>>

    @Query("SELECT * FROM problems ORDER BY createdAt DESC")
    fun getAllProblemsFlow(): Flow<List<ProblemEntity>>

    @Query("SELECT * FROM problems WHERE id = :id")
    suspend fun getProblemById(id: String): ProblemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProblem(problem: ProblemEntity)

    @Query("UPDATE problems SET status = :status WHERE id = :id")
    suspend fun updateProblemStatus(id: String, status: String)

    // Availability Slots
    @Query("SELECT * FROM availability_slots WHERE expertId = :expertId AND isBooked = 0")
    fun getAvailableSlotsForExpertFlow(expertId: String): Flow<List<AvailabilitySlotEntity>>

    @Query("SELECT * FROM availability_slots WHERE expertId = :expertId")
    fun getAllSlotsForExpertFlow(expertId: String): Flow<List<AvailabilitySlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: AvailabilitySlotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlots(slots: List<AvailabilitySlotEntity>)

    @Query("UPDATE availability_slots SET isBooked = 1 WHERE id = :slotId")
    suspend fun markSlotBooked(slotId: String)

    @Query("DELETE FROM availability_slots WHERE id = :slotId")
    suspend fun deleteSlot(slotId: String)

    // Bookings
    @Query("SELECT * FROM bookings WHERE farmerId = :farmerId ORDER BY bookedAt DESC")
    fun getBookingsForFarmerFlow(farmerId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE expertId = :expertId ORDER BY bookedAt DESC")
    fun getBookingsForExpertFlow(expertId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings ORDER BY bookedAt DESC")
    fun getAllBookingsFlow(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id")
    suspend fun getBookingById(id: String): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: String, status: String)

    // Messages
    @Query("SELECT * FROM messages WHERE bookingId = :bookingId ORDER BY timestamp ASC")
    fun getMessagesForBookingFlow(bookingId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("UPDATE messages SET isRead = 1 WHERE bookingId = :bookingId AND senderRole != :myRole")
    suspend fun markMessagesAsRead(bookingId: String, myRole: String)

    // Reviews
    @Query("SELECT * FROM reviews WHERE expertId = :expertId ORDER BY createdAt DESC")
    fun getReviewsForExpertFlow(expertId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviewsFlow(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Query("UPDATE reviews SET isFlagged = :isFlagged WHERE id = :id")
    suspend fun updateReviewFlag(id: String, isFlagged: Boolean)

    // Notifications
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUserFlow(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsRead(userId: String)

    // Platform Settings
    @Query("SELECT * FROM platform_settings WHERE id = 1")
    fun getPlatformSettingsFlow(): Flow<PlatformSettingsEntity?>

    @Query("SELECT * FROM platform_settings WHERE id = 1")
    suspend fun getPlatformSettings(): PlatformSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatformSettings(settings: PlatformSettingsEntity)
}

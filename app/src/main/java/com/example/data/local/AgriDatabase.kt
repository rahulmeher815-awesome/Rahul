package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        ExpertProfileEntity::class,
        ProblemEntity::class,
        AvailabilitySlotEntity::class,
        BookingEntity::class,
        MessageEntity::class,
        ReviewEntity::class,
        NotificationEntity::class,
        PlatformSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AgriDatabase : RoomDatabase() {
    abstract fun agriDao(): AgriDao

    companion object {
        @Volatile
        private var INSTANCE: AgriDatabase? = null

        fun getInstance(context: Context): AgriDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgriDatabase::class.java,
                    "agri_connect.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

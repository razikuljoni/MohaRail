package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.TrainAppDao
import com.example.data.local.entity.SavedTicketEntity
import com.example.data.local.entity.SearchHistoryEntity
import com.example.data.local.entity.TrainAlarmEntity

@Database(
    entities = [
        SavedTicketEntity::class,
        SearchHistoryEntity::class,
        TrainAlarmEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainAppDao(): TrainAppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trainkothay_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

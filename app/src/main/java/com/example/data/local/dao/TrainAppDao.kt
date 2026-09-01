package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.example.data.local.entity.SavedTicketEntity
import com.example.data.local.entity.SearchHistoryEntity
import com.example.data.local.entity.TrainAlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainAppDao {
    // Tickets
    @Query("SELECT * FROM saved_tickets ORDER BY issueTimestamp DESC")
    fun getAllTickets(): Flow<List<SavedTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SavedTicketEntity): Long

    @Query("DELETE FROM saved_tickets WHERE id = :id")
    suspend fun deleteTicket(id: Long)

    // Search History
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistoryEntity): Long

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    // Alarms
    @Query("SELECT * FROM train_alarms ORDER BY createdAt DESC")
    fun getAllAlarms(): Flow<List<TrainAlarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: TrainAlarmEntity): Long

    @Query("UPDATE train_alarms SET isEnabled = :enabled WHERE id = :id")
    suspend fun updateAlarmStatus(id: Long, enabled: Boolean)

    @Query("DELETE FROM train_alarms WHERE id = :id")
    suspend fun deleteAlarm(id: Long)
}

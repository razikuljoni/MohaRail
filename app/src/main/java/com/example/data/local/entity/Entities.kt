package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_tickets")
data class SavedTicketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pnr: String,
    val trainNo: String,
    val trainName: String,
    val fromStation: String,
    val toStation: String,
    val journeyDate: String,
    val departureTime: String,
    val seatClass: String,
    val coachName: String,
    val seatNumbers: String,
    val passengerName: String,
    val totalFare: Int,
    val issueTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originStationCode: String,
    val originStationName: String,
    val destStationCode: String,
    val destStationName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "train_alarms")
data class TrainAlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trainNo: String,
    val trainName: String,
    val destinationStation: String,
    val minutesBeforeArrival: Int = 15,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

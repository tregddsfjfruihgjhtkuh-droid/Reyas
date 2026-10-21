package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String = "INFO", // "APPROVAL", "REJECTION", "REPORT", "ALERT", "INFO"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

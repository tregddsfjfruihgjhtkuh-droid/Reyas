package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val badge: String = "EVENT",
    val author: String = "ReyasNova Team",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

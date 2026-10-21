package com.example.data.model

import androidx.room.Entity

@Entity(tableName = "favorites", primaryKeys = ["userId", "packId"])
data class Favorite(
    val userId: String,
    val packId: String,
    val createdAt: Long = System.currentTimeMillis()
)

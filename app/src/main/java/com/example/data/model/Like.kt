package com.example.data.model

import androidx.room.Entity

@Entity(tableName = "likes", primaryKeys = ["userId", "packId"])
data class Like(
    val userId: String,
    val packId: String,
    val createdAt: Long = System.currentTimeMillis()
)

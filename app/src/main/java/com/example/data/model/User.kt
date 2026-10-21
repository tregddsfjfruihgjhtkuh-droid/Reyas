package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val googleId: String = "",
    val username: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String = "",
    val role: String = ROLE_USER, // "USER" or "ADMIN"
    val accountStatus: String = STATUS_ACTIVE, // "ACTIVE", "SUSPENDED", "BANNED"
    val banReason: String? = null,
    val bio: String = "Minecraft Texture Pack Enthusiast",
    val joinedAt: Long = System.currentTimeMillis()
) {
    val isAdmin: Boolean get() = role == ROLE_ADMIN
    val isBanned: Boolean get() = accountStatus == STATUS_BANNED || accountStatus == STATUS_SUSPENDED

    companion object {
        const val ROLE_USER = "USER"
        const val ROLE_ADMIN = "ADMIN"

        const val STATUS_ACTIVE = "ACTIVE"
        const val STATUS_SUSPENDED = "SUSPENDED"
        const val STATUS_BANNED = "BANNED"
    }
}

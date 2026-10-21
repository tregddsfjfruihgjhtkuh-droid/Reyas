package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "texture_packs")
data class TexturePack(
    @PrimaryKey val id: String,
    val creatorId: String,
    val creatorName: String,
    val creatorAvatar: String = "",
    val name: String,
    val description: String,
    val version: String = "1.0.0",
    val minecraftVersion: String = "1.21.1",
    val resolution: String = "16x",
    val category: String = "PvP",
    val tags: String = "pvp, 16x",
    val fileName: String = "pack.zip",
    val fileSizeBytes: Long = 12_400_000L,
    val thumbnailResName: String = "banner_minecraft",
    val screenshots: String = "",
    val downloads: Int = 0,
    val likes: Int = 0,
    val status: String = STATUS_APPROVED, // "APPROVED", "PENDING_REVIEW", "REJECTED", "DISABLED"
    val moderationReason: String? = null,
    val changelog: String = "Initial community release.",
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val tagList: List<String>
        get() = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    val formattedFileSize: String
        get() {
            val mb = fileSizeBytes / (1024.0 * 1024.0)
            return String.format("%.1f MB", mb)
        }

    val isPublic: Boolean
        get() = status == STATUS_APPROVED

    companion object {
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_PENDING = "PENDING_REVIEW"
        const val STATUS_REJECTED = "REJECTED"
        const val STATUS_DISABLED = "DISABLED"

        val RESOLUTIONS = listOf("16x", "32x", "64x", "128x", "256x", "512x")
        val MC_VERSIONS = listOf("1.21.1", "1.20.6", "1.19.4", "1.18.2", "1.16.5", "1.8.9")
        val CATEGORIES = listOf(
            "PvP", "Survival", "Vanilla", "Faithful", "FPS Boost",
            "UI", "Bedwars", "Skywars", "UHC", "SMP",
            "Animated", "Low Fire", "Items", "Weapons", "Mobs", "Misc"
        )
    }
}

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey val id: String,
    val reporterId: String,
    val reporterName: String,
    val targetType: String, // "PACK" or "USER"
    val targetId: String,
    val targetTitle: String,
    val reason: String,
    val status: String = STATUS_OPEN, // "OPEN", "UNDER_REVIEW", "RESOLVED", "DISMISSED"
    val adminNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_PACK = "PACK"
        const val TYPE_USER = "USER"

        const val STATUS_OPEN = "OPEN"
        const val STATUS_UNDER_REVIEW = "UNDER_REVIEW"
        const val STATUS_RESOLVED = "RESOLVED"
        const val STATUS_DISMISSED = "DISMISSED"

        val REASONS = listOf(
            "Malicious File / Virus",
            "Inappropriate Content",
            "Copyright / Plagiarism Infringement",
            "Broken / Corrupted Download",
            "Misleading Screenshots / Title",
            "Spam / Duplicate Upload"
        )
    }
}

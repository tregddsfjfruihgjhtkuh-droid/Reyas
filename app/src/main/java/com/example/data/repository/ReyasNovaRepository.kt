package com.example.data.repository

import com.example.data.local.ReyasNovaDao
import com.example.data.model.Announcement
import com.example.data.model.Favorite
import com.example.data.model.Like
import com.example.data.model.Notification
import com.example.data.model.Report
import com.example.data.model.TexturePack
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ReyasNovaRepository(private val dao: ReyasNovaDao) {

    // --- PACKS ---
    val allPacks: Flow<List<TexturePack>> = dao.getAllPacks()
    val publicPacks: Flow<List<TexturePack>> = dao.getPublicPacks()

    fun getPacksByCreator(creatorId: String): Flow<List<TexturePack>> =
        dao.getPacksByCreator(creatorId)

    fun getPackById(id: String): Flow<TexturePack?> = dao.getPackById(id)

    suspend fun getPackByIdDirect(id: String): TexturePack? = dao.getPackByIdDirect(id)

    suspend fun insertPack(pack: TexturePack) = dao.insertPack(pack)

    suspend fun updatePack(pack: TexturePack) = dao.updatePack(pack)

    suspend fun deletePackById(id: String) = dao.deletePackById(id)

    suspend fun downloadPack(packId: String) {
        dao.incrementDownloads(packId)
    }

    suspend fun toggleLike(userId: String, packId: String, currentlyLiked: Boolean) {
        val pack = dao.getPackByIdDirect(packId) ?: return
        if (currentlyLiked) {
            dao.deleteLike(userId, packId)
            dao.updateLikes(packId, (pack.likes - 1).coerceAtLeast(0))
        } else {
            dao.insertLike(Like(userId, packId))
            dao.updateLikes(packId, pack.likes + 1)
        }
    }

    suspend fun toggleFavorite(userId: String, packId: String, currentlyFavorited: Boolean) {
        if (currentlyFavorited) {
            dao.deleteFavorite(userId, packId)
        } else {
            dao.insertFavorite(Favorite(userId, packId))
        }
    }

    fun isFavorite(userId: String, packId: String): Flow<Boolean> =
        dao.isFavorite(userId, packId)

    fun isLiked(userId: String, packId: String): Flow<Boolean> =
        dao.isLiked(userId, packId)

    fun getFavoritesForUser(userId: String): Flow<List<Favorite>> =
        dao.getFavoritesForUser(userId)

    // --- USERS ---
    val allUsers: Flow<List<User>> = dao.getAllUsers()

    fun getUserById(id: String): Flow<User?> = dao.getUserById(id)

    suspend fun getUserByIdDirect(id: String): User? = dao.getUserByIdDirect(id)

    suspend fun insertUser(user: User) = dao.insertUser(user)

    suspend fun updateUser(user: User) = dao.updateUser(user)

    suspend fun updateUserStatus(userId: String, status: String, reason: String?) {
        dao.updateUserStatus(userId, status, reason)
        if (status == User.STATUS_BANNED || status == User.STATUS_SUSPENDED) {
            dao.disableAllPacksByCreator(userId)
            dao.insertNotification(
                Notification(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    title = "Account Notice",
                    message = "Your account has been $status. Reason: ${reason ?: "Violation of platform policies"}",
                    type = "ALERT"
                )
            )
        }
    }

    // --- MODERATION ---
    suspend fun approvePack(packId: String) {
        val pack = dao.getPackByIdDirect(packId)
        dao.updatePackStatus(packId, TexturePack.STATUS_APPROVED, null)
        pack?.let {
            dao.insertNotification(
                Notification(
                    id = UUID.randomUUID().toString(),
                    userId = it.creatorId,
                    title = "Texture Pack Approved!",
                    message = "Your pack '${it.name}' is now live on the public community catalog.",
                    type = "APPROVAL"
                )
            )
        }
    }

    suspend fun rejectPack(packId: String, reason: String) {
        val pack = dao.getPackByIdDirect(packId)
        dao.updatePackStatus(packId, TexturePack.STATUS_REJECTED, reason)
        pack?.let {
            dao.insertNotification(
                Notification(
                    id = UUID.randomUUID().toString(),
                    userId = it.creatorId,
                    title = "Texture Pack Rejected",
                    message = "Your submission '${it.name}' was rejected: $reason",
                    type = "REJECTION"
                )
            )
        }
    }

    suspend fun disablePack(packId: String, reason: String) {
        dao.updatePackStatus(packId, TexturePack.STATUS_DISABLED, reason)
    }

    suspend fun restorePack(packId: String) {
        dao.updatePackStatus(packId, TexturePack.STATUS_APPROVED, null)
    }

    suspend fun toggleFeatured(packId: String, isFeatured: Boolean) {
        dao.toggleFeatured(packId, isFeatured)
    }

    // --- REPORTS ---
    val allReports: Flow<List<Report>> = dao.getAllReports()

    suspend fun submitReport(report: Report) = dao.insertReport(report)

    suspend fun updateReportStatus(id: String, status: String, notes: String?) {
        dao.updateReportStatus(id, status, notes)
    }

    // --- ANNOUNCEMENTS ---
    val activeAnnouncements: Flow<List<Announcement>> = dao.getActiveAnnouncements()
    val allAnnouncements: Flow<List<Announcement>> = dao.getAllAnnouncements()

    suspend fun createAnnouncement(announcement: Announcement) =
        dao.insertAnnouncement(announcement)

    suspend fun deleteAnnouncement(id: String) = dao.deleteAnnouncement(id)

    // --- NOTIFICATIONS ---
    fun getNotificationsForUser(userId: String): Flow<List<Notification>> =
        dao.getNotificationsForUser(userId)

    suspend fun markNotificationRead(id: String) = dao.markNotificationRead(id)

    suspend fun markAllNotificationsRead(userId: String) =
        dao.markAllNotificationsRead(userId)
}

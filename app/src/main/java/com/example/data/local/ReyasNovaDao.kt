package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Announcement
import com.example.data.model.Favorite
import com.example.data.model.Like
import com.example.data.model.Notification
import com.example.data.model.Report
import com.example.data.model.TexturePack
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface ReyasNovaDao {

    // --- USERS ---
    @Query("SELECT * FROM users ORDER BY joinedAt DESC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdDirect(id: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET accountStatus = :status, banReason = :reason WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, status: String, reason: String?)

    @Delete
    suspend fun deleteUser(user: User)

    // --- TEXTURE PACKS ---
    @Query("SELECT * FROM texture_packs ORDER BY createdAt DESC")
    fun getAllPacks(): Flow<List<TexturePack>>

    @Query("SELECT * FROM texture_packs WHERE status = 'APPROVED' ORDER BY createdAt DESC")
    fun getPublicPacks(): Flow<List<TexturePack>>

    @Query("SELECT * FROM texture_packs WHERE creatorId = :creatorId ORDER BY createdAt DESC")
    fun getPacksByCreator(creatorId: String): Flow<List<TexturePack>>

    @Query("SELECT * FROM texture_packs WHERE id = :id LIMIT 1")
    fun getPackById(id: String): Flow<TexturePack?>

    @Query("SELECT * FROM texture_packs WHERE id = :id LIMIT 1")
    suspend fun getPackByIdDirect(id: String): TexturePack?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPack(pack: TexturePack)

    @Update
    suspend fun updatePack(pack: TexturePack)

    @Query("UPDATE texture_packs SET status = :status, moderationReason = :reason, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updatePackStatus(id: String, status: String, reason: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE texture_packs SET isFeatured = :isFeatured WHERE id = :id")
    suspend fun toggleFeatured(id: String, isFeatured: Boolean)

    @Query("UPDATE texture_packs SET downloads = downloads + 1 WHERE id = :id")
    suspend fun incrementDownloads(id: String)

    @Query("UPDATE texture_packs SET likes = :likes WHERE id = :id")
    suspend fun updateLikes(id: String, likes: Int)

    @Query("UPDATE texture_packs SET status = 'DISABLED', moderationReason = 'User suspended' WHERE creatorId = :creatorId")
    suspend fun disableAllPacksByCreator(creatorId: String)

    @Query("DELETE FROM texture_packs WHERE id = :id")
    suspend fun deletePackById(id: String)

    // --- FAVORITES ---
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY createdAt DESC")
    fun getFavoritesForUser(userId: String): Flow<List<Favorite>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND packId = :packId)")
    fun isFavorite(userId: String, packId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE userId = :userId AND packId = :packId")
    suspend fun deleteFavorite(userId: String, packId: String)

    // --- LIKES ---
    @Query("SELECT * FROM likes WHERE userId = :userId")
    fun getLikesForUser(userId: String): Flow<List<Like>>

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE userId = :userId AND packId = :packId)")
    fun isLiked(userId: String, packId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: Like)

    @Query("DELETE FROM likes WHERE userId = :userId AND packId = :packId")
    suspend fun deleteLike(userId: String, packId: String)

    // --- REPORTS ---
    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<Report>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: Report)

    @Query("UPDATE reports SET status = :status, adminNotes = :notes WHERE id = :id")
    suspend fun updateReportStatus(id: String, status: String, notes: String?)

    // --- ANNOUNCEMENTS ---
    @Query("SELECT * FROM announcements WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveAnnouncements(): Flow<List<Announcement>>

    @Query("SELECT * FROM announcements ORDER BY createdAt DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: String)

    // --- NOTIFICATIONS ---
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: String): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsRead(userId: String)
}

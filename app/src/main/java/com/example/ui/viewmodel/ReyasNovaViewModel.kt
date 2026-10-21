package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Announcement
import com.example.data.model.Notification
import com.example.data.model.Report
import com.example.data.model.TexturePack
import com.example.data.model.User
import com.example.data.repository.ReyasNovaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed class Screen {
    object Home : Screen()
    object BrowsePacks : Screen()
    data class PackDetails(val packId: String) : Screen()
    object UploadPack : Screen()
    object UserDashboard : Screen()
    data class CreatorProfile(val creatorId: String) : Screen()
    object AdminPanel : Screen()
    object AuthScreen : Screen()
    object Guidelines : Screen()
}

class ReyasNovaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReyasNovaRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = ReyasNovaRepository(db.dao())
    }

    // --- NAVIGATION ---
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val navigationStack = mutableListOf<Screen>(Screen.Home)

    fun navigateTo(screen: Screen) {
        navigationStack.add(screen)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (navigationStack.size > 1) {
            navigationStack.removeAt(navigationStack.size - 1)
            _currentScreen.value = navigationStack.last()
            return true
        }
        return false
    }

    // --- AUTH / CURRENT USER ---
    // Seed with userReyas as active profile by default for immediate preview
    private val _currentUser = MutableStateFlow<User?>(
        User(
            id = "usr_reyasnova",
            username = "reyasnova",
            displayName = "ReyasNova",
            email = "reyas@reyasnova.com",
            role = User.ROLE_USER,
            bio = "Official texture pack creator & community lead."
        )
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun switchUserRole(role: String) {
        viewModelScope.launch {
            if (role == User.ROLE_ADMIN) {
                _currentUser.value = User(
                    id = "usr_admin",
                    username = "admin",
                    displayName = "Platform Owner",
                    email = "admin@reyasnova.com",
                    role = User.ROLE_ADMIN,
                    bio = "ReyasNova Lead Platform Administrator"
                )
            } else if (role == "GUEST") {
                _currentUser.value = null
            } else {
                _currentUser.value = User(
                    id = "usr_reyasnova",
                    username = "reyasnova",
                    displayName = "ReyasNova",
                    email = "reyas@reyasnova.com",
                    role = User.ROLE_USER,
                    bio = "Official texture pack creator & community lead."
                )
            }
        }
    }

    fun signInWithGoogle(displayName: String, email: String) {
        viewModelScope.launch {
            val username = email.substringBefore("@").lowercase().replace(".", "_")
            val newUser = User(
                id = "usr_${UUID.randomUUID().toString().take(8)}",
                googleId = "goog_${UUID.randomUUID().toString().take(10)}",
                username = username,
                displayName = displayName.ifBlank { "Minecraft Player" },
                email = email,
                role = User.ROLE_USER,
                bio = "Minecraft texture pack enthusiast"
            )
            repository.insertUser(newUser)
            _currentUser.value = newUser
            navigateTo(Screen.UserDashboard)
        }
    }

    fun signOut() {
        _currentUser.value = null
        navigateTo(Screen.Home)
    }

    // --- DATA STREAMS ---
    val allPacks: StateFlow<List<TexturePack>> = repository.allPacks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publicPacks: StateFlow<List<TexturePack>> = repository.publicPacks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<Report>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAnnouncements: StateFlow<List<Announcement>> = repository.activeAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- SEARCH & FILTERS ---
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val selectedMcVersion = MutableStateFlow("All")
    val selectedResolution = MutableStateFlow("All")
    val selectedSort = MutableStateFlow("Newest") // "Newest", "Most Downloads", "Most Likes", "Popular"

    private data class FilterCriteria(
        val query: String,
        val category: String,
        val version: String,
        val resolution: String,
        val sort: String
    )

    private val filterCriteria = combine(
        searchQuery,
        selectedCategory,
        selectedMcVersion,
        selectedResolution,
        selectedSort
    ) { query, category, version, resolution, sort ->
        FilterCriteria(query, category, version, resolution, sort)
    }

    val filteredPacks: StateFlow<List<TexturePack>> = combine(
        publicPacks,
        filterCriteria
    ) { packs, criteria ->
        var list = packs

        if (criteria.query.isNotBlank()) {
            val q = criteria.query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.creatorName.lowercase().contains(q) ||
                it.tags.lowercase().contains(q) ||
                it.category.lowercase().contains(q)
            }
        }

        if (criteria.category != "All") {
            list = list.filter { it.category.equals(criteria.category, ignoreCase = true) }
        }

        if (criteria.version != "All") {
            list = list.filter { it.minecraftVersion == criteria.version }
        }

        if (criteria.resolution != "All") {
            list = list.filter { it.resolution == criteria.resolution }
        }

        when (criteria.sort) {
            "Most Downloads" -> list.sortedByDescending { it.downloads }
            "Most Likes" -> list.sortedByDescending { it.likes }
            "Popular" -> list.sortedByDescending { it.downloads + it.likes * 2 }
            else -> list.sortedByDescending { it.createdAt }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- PACK DETAILS & DOWNLOAD ---
    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress.asStateFlow()

    private val _downloadSuccess = MutableStateFlow<String?>(null)
    val downloadSuccess: StateFlow<String?> = _downloadSuccess.asStateFlow()

    fun initiateDownload(pack: TexturePack) {
        viewModelScope.launch {
            _downloadProgress.value = 0.0f
            // Simulate realistic fast download chunks
            for (i in 1..10) {
                delay(120)
                _downloadProgress.value = i / 10.0f
            }
            repository.downloadPack(pack.id)
            _downloadProgress.value = null
            _downloadSuccess.value = "Downloaded ${pack.fileName} (${pack.formattedFileSize})"
            delay(3000)
            _downloadSuccess.value = null
        }
    }

    fun toggleLike(packId: String, currentLiked: Boolean) {
        val user = _currentUser.value ?: run {
            navigateTo(Screen.AuthScreen)
            return
        }
        viewModelScope.launch {
            repository.toggleLike(user.id, packId, currentLiked)
        }
    }

    fun toggleFavorite(packId: String, currentFavorited: Boolean) {
        val user = _currentUser.value ?: run {
            navigateTo(Screen.AuthScreen)
            return
        }
        viewModelScope.launch {
            repository.toggleFavorite(user.id, packId, currentFavorited)
        }
    }

    fun submitReport(packId: String, packTitle: String, reason: String) {
        val user = _currentUser.value ?: run {
            navigateTo(Screen.AuthScreen)
            return
        }
        viewModelScope.launch {
            val report = Report(
                id = "rep_${UUID.randomUUID().toString().take(8)}",
                reporterId = user.id,
                reporterName = user.displayName,
                targetType = Report.TYPE_PACK,
                targetId = packId,
                targetTitle = packTitle,
                reason = reason,
                status = Report.STATUS_OPEN
            )
            repository.submitReport(report)
        }
    }

    // --- UPLOAD FLOW ---
    val uploadStep = MutableStateFlow(1)
    val uploadName = MutableStateFlow("")
    val uploadDesc = MutableStateFlow("")
    val uploadVersion = MutableStateFlow("1.0.0")
    val uploadMcVersion = MutableStateFlow("1.21.1")
    val uploadResolution = MutableStateFlow("16x")
    val uploadCategory = MutableStateFlow("PvP")
    val uploadTags = MutableStateFlow("pvp, 16x")
    val uploadFileName = MutableStateFlow("My_Custom_Pack_16x.zip")
    val uploadChangelog = MutableStateFlow("Initial community release.")
    val uploadError = MutableStateFlow<String?>(null)
    val uploadIsSubmitting = MutableStateFlow(false)

    fun submitUpload() {
        val user = _currentUser.value
        if (user == null) {
            uploadError.value = "You must be signed in to upload a texture pack."
            return
        }
        if (user.isBanned) {
            uploadError.value = "Your account is suspended: ${user.banReason ?: "Policy violation"}"
            return
        }

        if (uploadName.value.isBlank()) {
            uploadError.value = "Pack name cannot be empty."
            return
        }
        if (!uploadFileName.value.endsWith(".zip", ignoreCase = true)) {
            uploadError.value = "Invalid file type. Only Minecraft .zip resource packs are supported."
            return
        }

        viewModelScope.launch {
            uploadIsSubmitting.value = true
            delay(600) // Realistic file validation & virus check simulation

            val newPack = TexturePack(
                id = "pack_${UUID.randomUUID().toString().take(8)}",
                creatorId = user.id,
                creatorName = user.displayName,
                name = uploadName.value.trim(),
                description = uploadDesc.value.trim().ifBlank { "Custom Minecraft texture pack." },
                version = uploadVersion.value.trim(),
                minecraftVersion = uploadMcVersion.value,
                resolution = uploadResolution.value,
                category = uploadCategory.value,
                tags = uploadTags.value.trim(),
                fileName = uploadFileName.value.trim(),
                fileSizeBytes = (8..35).random() * 1024 * 1024L,
                status = TexturePack.STATUS_PENDING,
                changelog = uploadChangelog.value.trim()
            )

            repository.insertPack(newPack)
            uploadIsSubmitting.value = false
            // Reset upload form
            uploadStep.value = 1
            uploadName.value = ""
            uploadDesc.value = ""
            uploadError.value = null
            navigateTo(Screen.UserDashboard)
        }
    }

    // --- ADMIN ACTIONS ---
    fun approvePack(packId: String) {
        viewModelScope.launch {
            repository.approvePack(packId)
        }
    }

    fun rejectPack(packId: String, reason: String) {
        viewModelScope.launch {
            repository.rejectPack(packId, reason.ifBlank { "Violates content guidelines" })
        }
    }

    fun disablePack(packId: String, reason: String) {
        viewModelScope.launch {
            repository.disablePack(packId, reason)
        }
    }

    fun restorePack(packId: String) {
        viewModelScope.launch {
            repository.restorePack(packId)
        }
    }

    fun deletePack(packId: String) {
        viewModelScope.launch {
            repository.deletePackById(packId)
        }
    }

    fun toggleFeaturedPack(packId: String, isFeatured: Boolean) {
        viewModelScope.launch {
            repository.toggleFeatured(packId, isFeatured)
        }
    }

    fun banUser(userId: String, reason: String) {
        viewModelScope.launch {
            repository.updateUserStatus(userId, User.STATUS_BANNED, reason)
        }
    }

    fun suspendUser(userId: String, reason: String) {
        viewModelScope.launch {
            repository.updateUserStatus(userId, User.STATUS_SUSPENDED, reason)
        }
    }

    fun unbanUser(userId: String) {
        viewModelScope.launch {
            repository.updateUserStatus(userId, User.STATUS_ACTIVE, null)
        }
    }

    fun resolveReport(reportId: String, notes: String) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, Report.STATUS_RESOLVED, notes)
        }
    }

    fun dismissReport(reportId: String) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, Report.STATUS_DISMISSED, "Dismissed by admin")
        }
    }

    fun broadcastAnnouncement(title: String, content: String) {
        viewModelScope.launch {
            val announcement = Announcement(
                id = "ann_${UUID.randomUUID().toString().take(8)}",
                title = title.trim(),
                content = content.trim()
            )
            repository.createAnnouncement(announcement)
        }
    }

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch {
            repository.deleteAnnouncement(id)
        }
    }
}

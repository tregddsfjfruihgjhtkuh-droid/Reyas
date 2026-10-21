package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Texture
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Report
import com.example.data.model.TexturePack
import com.example.data.model.User
import com.example.ui.components.McVersionBadge
import com.example.ui.components.ResolutionBadge
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatCount
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.DeepDarkBg
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MinecraftGold
import com.example.ui.theme.MinecraftGreen
import com.example.ui.theme.MinecraftRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleGlow
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ReyasNovaViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun AdminPanelScreen(
    viewModel: ReyasNovaViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allPacks by viewModel.allPacks.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allReports by viewModel.allReports.collectAsState()
    val activeAnnouncements by viewModel.activeAnnouncements.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog states for moderation
    var packToReject by remember { mutableStateOf<TexturePack?>(null) }
    var rejectReason by remember { mutableStateOf("") }

    var userToBan by remember { mutableStateOf<User?>(null) }
    var banReason by remember { mutableStateOf("") }

    var newAnnTitle by remember { mutableStateOf("") }
    var newAnnContent by remember { mutableStateOf("") }
    var showCreateAnnDialog by remember { mutableStateOf(false) }

    // Server-side authorization check!
    if (currentUser?.isAdmin != true) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DeepDarkBg)
                .testTag("admin_access_denied"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Access Denied",
                    tint = MinecraftRed,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Access Denied",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "The Admin Control Center requires administrator privileges. Your current account does not have authorization.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { viewModel.switchUserRole("ADMIN") },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("Switch to Admin Role (Demo)")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { viewModel.navigateBack() }) {
                    Text("Return to Community", color = TextSecondary)
                }
            }
        }
        return
    }

    val pendingPacks = allPacks.filter { it.status == TexturePack.STATUS_PENDING }
    val openReports = allReports.filter { it.status == Report.STATUS_OPEN }
    val bannedUsers = allUsers.filter { it.isBanned }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepDarkBg)
            .testTag("admin_panel_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.testTag("admin_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Icon(Icons.Default.Security, contentDescription = "Security", tint = NeonCyan)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Admin Control Center",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "ReyasNova Platform Owner Moderation",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonPurpleGlow
                )
            }
        }

        // Sub-tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceDark,
            contentColor = TextPrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan
                )
            }
        ) {
            listOf(
                "Overview",
                "Packs (${pendingPacks.size} Pending)",
                "Users (${allUsers.size})",
                "Reports (${openReports.size})",
                "Announcements"
            ).forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) NeonCyan else TextSecondary
                        )
                    }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // OVERVIEW STATS
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Platform Statistics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AdminStatCard(title = "Total Users", count = "1,248", color = NeonCyan, modifier = Modifier.weight(1f))
                            AdminStatCard(title = "Total Packs", count = "3,562", color = NeonPurpleGlow, modifier = Modifier.weight(1f))
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AdminStatCard(
                                title = "Pending Reviews",
                                count = "${pendingPacks.size}",
                                color = if (pendingPacks.isNotEmpty()) MinecraftGold else TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            AdminStatCard(title = "Total Downloads", count = "245.8K", color = MinecraftGreen, modifier = Modifier.weight(1f))
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AdminStatCard(title = "Active Reports", count = "${openReports.size}", color = MinecraftRed, modifier = Modifier.weight(1f))
                            AdminStatCard(title = "Banned Users", count = "${bannedUsers.size}", color = TextMuted, modifier = Modifier.weight(1f))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Recent Platform Activity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            border = BorderStroke(1.dp, CardBorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                ActivityItem(icon = Icons.Default.People, text = "New user @steve_miner joined platform", time = "2 minutes ago")
                                ActivityItem(icon = Icons.Default.Check, text = "Pack 'Purple PvP 16x' approved by staff", time = "15 minutes ago")
                                ActivityItem(icon = Icons.Default.Flag, text = "Report submitted on 'Missing Textures'", time = "45 minutes ago")
                                ActivityItem(icon = Icons.Default.Block, text = "Suspicious user suspended for spam uploads", time = "2 hours ago")
                            }
                        }
                    }
                }
            }
            1 -> {
                // TEXTURE PACKS MODERATION
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Community Submissions (${allPacks.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    items(allPacks) { pack ->
                        AdminPackCard(
                            pack = pack,
                            onApprove = { viewModel.approvePack(pack.id) },
                            onReject = {
                                packToReject = pack
                                rejectReason = ""
                            },
                            onDisable = { viewModel.disablePack(pack.id, "Disabled by Administrator") },
                            onRestore = { viewModel.restorePack(pack.id) },
                            onToggleFeatured = { viewModel.toggleFeaturedPack(pack.id, !pack.isFeatured) },
                            onDelete = { viewModel.deletePack(pack.id) }
                        )
                    }
                }
            }
            2 -> {
                // USERS MODERATION
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Registered Community Creators",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    items(allUsers) { user ->
                        AdminUserCard(
                            user = user,
                            onBan = {
                                userToBan = user
                                banReason = ""
                            },
                            onUnban = { viewModel.unbanUser(user.id) }
                        )
                    }
                }
            }
            3 -> {
                // REPORTS
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Safety & Community Reports",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    if (allReports.isEmpty()) {
                        item {
                            Text("No reports found.", color = TextSecondary)
                        }
                    } else {
                        items(allReports) { report ->
                            AdminReportCard(
                                report = report,
                                onResolve = { viewModel.resolveReport(report.id, "Reviewed and verified by staff") },
                                onDismiss = { viewModel.dismissReport(report.id) }
                            )
                        }
                    }
                }
            }
            4 -> {
                // ANNOUNCEMENTS
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Platform Announcements",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Button(
                            onClick = { showCreateAnnDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text("+ New Announcement")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(activeAnnouncements) { ann ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceDark,
                                border = BorderStroke(1.dp, CardBorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(ann.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(ann.content, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                    IconButton(onClick = { viewModel.deleteAnnouncement(ann.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MinecraftRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Reject Pack Modal Dialog
    if (packToReject != null) {
        Dialog(onDismissRequest = { packToReject = null }) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceDark,
                border = BorderStroke(1.dp, MinecraftRed)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Reject Texture Pack",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MinecraftRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Specify moderation reason for '${packToReject!!.name}':",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        placeholder = { Text("e.g. Inappropriate textures / Misleading screenshots") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = SurfaceElevated,
                            unfocusedContainerColor = SurfaceElevated
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { packToReject = null }, modifier = Modifier.weight(1f)) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                viewModel.rejectPack(packToReject!!.id, rejectReason)
                                packToReject = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MinecraftRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Confirm Rejection", color = TextPrimary)
                        }
                    }
                }
            }
        }
    }

    // Ban User Modal Dialog
    if (userToBan != null) {
        Dialog(onDismissRequest = { userToBan = null }) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceDark,
                border = BorderStroke(1.dp, MinecraftRed)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Suspend / Ban User",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MinecraftRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Suspending @${userToBan!!.username} will disable all their uploaded texture packs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = banReason,
                        onValueChange = { banReason = it },
                        placeholder = { Text("e.g. Malicious files / Terms of Service violation") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = SurfaceElevated,
                            unfocusedContainerColor = SurfaceElevated
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { userToBan = null }, modifier = Modifier.weight(1f)) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                viewModel.banUser(userToBan!!.id, banReason)
                                userToBan = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MinecraftRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Ban User", color = TextPrimary)
                        }
                    }
                }
            }
        }
    }

    // Create Announcement Modal
    if (showCreateAnnDialog) {
        Dialog(onDismissRequest = { showCreateAnnDialog = false }) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceDark,
                border = BorderStroke(1.dp, NeonPurpleGlow)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Broadcast Announcement", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newAnnTitle,
                        onValueChange = { newAnnTitle = it },
                        label = { Text("Title") },
                        placeholder = { Text("e.g. ⚔️ Texture Pack Contest") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newAnnContent,
                        onValueChange = { newAnnContent = it },
                        label = { Text("Content") },
                        placeholder = { Text("Announcement details...") },
                        modifier = Modifier.fillMaxWidth().height(90.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showCreateAnnDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                if (newAnnTitle.isNotBlank()) {
                                    viewModel.broadcastAnnouncement(newAnnTitle, newAnnContent)
                                    showCreateAnnDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Broadcast")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(title: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = count, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
fun ActivityItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, time: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = text, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            Text(text = time, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}

@Composable
fun AdminPackCard(
    pack: TexturePack,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDisable: () -> Unit,
    onRestore: () -> Unit,
    onToggleFeatured: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, if (pack.status == TexturePack.STATUS_PENDING) MinecraftGold else CardBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = pack.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Creator: ${pack.creatorName} • ${pack.formattedFileSize}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                StatusBadge(status = pack.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                McVersionBadge(version = pack.minecraftVersion)
                ResolutionBadge(resolution = pack.resolution)
                if (pack.isFeatured) {
                    Surface(
                        color = MinecraftGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, MinecraftGold)
                    ) {
                        Text("Featured Spotlight", color = MinecraftGold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            if (pack.moderationReason != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Reason: ${pack.moderationReason}", color = MinecraftRed, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (pack.status == TexturePack.STATUS_PENDING || pack.status == TexturePack.STATUS_REJECTED) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = MinecraftGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Approve", color = TextPrimary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                if (pack.status == TexturePack.STATUS_PENDING) {
                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(containerColor = MinecraftRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject", color = TextPrimary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                if (pack.status == TexturePack.STATUS_APPROVED) {
                    OutlinedButton(
                        onClick = onDisable,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Disable", color = MinecraftRed, style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (pack.status == TexturePack.STATUS_DISABLED) {
                    Button(
                        onClick = onRestore,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Restore", style = MaterialTheme.typography.labelSmall)
                    }
                }

                IconButton(onClick = onToggleFeatured) {
                    Icon(
                        imageVector = if (pack.isFeatured) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Feature",
                        tint = if (pack.isFeatured) MinecraftGold else TextMuted
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted)
                }
            }
        }
    }
}

@Composable
fun AdminUserCard(user: User, onBan: () -> Unit, onUnban: () -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.displayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "@${user.username} • ID: ${user.id}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(
                    text = "Status: ${user.accountStatus} • Role: ${user.role}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (user.isBanned) MinecraftRed else MinecraftGreen
                )
                if (user.banReason != null) {
                    Text(text = "Reason: ${user.banReason}", color = MinecraftRed, style = MaterialTheme.typography.labelSmall)
                }
            }

            if (user.isBanned) {
                Button(
                    onClick = onUnban,
                    colors = ButtonDefaults.buttonColors(containerColor = MinecraftGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Unban", style = MaterialTheme.typography.labelSmall)
                }
            } else if (!user.isAdmin) {
                OutlinedButton(
                    onClick = onBan,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MinecraftRed)
                ) {
                    Text("Suspend", color = MinecraftRed, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun AdminReportCard(report: Report, onResolve: () -> Unit, onDismiss: () -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, if (report.status == Report.STATUS_OPEN) MinecraftRed else CardBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Report on: ${report.targetTitle}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = report.status, color = if (report.status == Report.STATUS_OPEN) MinecraftRed else MinecraftGreen, style = MaterialTheme.typography.labelSmall)
            }
            Text(text = "Reason: ${report.reason}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            Text(text = "Reported by: ${report.reporterName}", color = TextMuted, style = MaterialTheme.typography.bodySmall)
            if (report.adminNotes != null) {
                Text(text = "Notes: ${report.adminNotes}", color = NeonCyan, style = MaterialTheme.typography.bodySmall)
            }

            if (report.status == Report.STATUS_OPEN) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onResolve,
                        colors = ButtonDefaults.buttonColors(containerColor = MinecraftGreen),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Resolve", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Dismiss", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

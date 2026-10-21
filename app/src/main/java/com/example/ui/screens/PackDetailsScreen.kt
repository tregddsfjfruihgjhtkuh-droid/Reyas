package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.Report
import com.example.data.model.TexturePack
import com.example.ui.components.CategoryBadge
import com.example.ui.components.McVersionBadge
import com.example.ui.components.ResolutionBadge
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackDetailsScreen(
    packId: String,
    viewModel: ReyasNovaViewModel,
    modifier: Modifier = Modifier
) {
    val allPacks by viewModel.allPacks.collectAsState()
    val pack = allPacks.firstOrNull { it.id == packId }
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val downloadSuccess by viewModel.downloadSuccess.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var showReportDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf(Report.REASONS.first()) }
    var reportExpanded by remember { mutableStateOf(false) }
    var reportSubmittedToast by remember { mutableStateOf(false) }

    var isLiked by remember { mutableStateOf(false) }
    var isFavorited by remember { mutableStateOf(false) }

    if (pack == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DeepDarkBg),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Texture pack not found", color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.navigateBack() }) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("pack_details_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.testTag("pack_details_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out '${pack.name}' Minecraft texture pack on ReyasNova!")
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Pack"))
            }) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = TextPrimary)
            }
            IconButton(onClick = { showReportDialog = true }) {
                Icon(Icons.Default.Flag, contentDescription = "Report", tint = TextSecondary)
            }
        }

        // Hero Screenshot Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(SurfaceDark)
        ) {
            Image(
                painter = painterResource(id = R.drawable.banner_minecraft),
                contentDescription = pack.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, DeepDarkBg.copy(alpha = 0.8f), DeepDarkBg),
                            startY = 100f
                        )
                    )
            )

            // Pack Icon badge on top of hero
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_reyasnova_logo),
                    contentDescription = "Pack Icon",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, NeonPurpleGlow, RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = pack.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.navigateTo(Screen.CreatorProfile(pack.creatorId))
                        }
                    ) {
                        Text(
                            text = "by ${pack.creatorName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeonPurpleGlow,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Creator",
                            tint = MinecraftGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Badges & Tag Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            McVersionBadge(version = pack.minecraftVersion)
            ResolutionBadge(resolution = pack.resolution)
            CategoryBadge(category = pack.category)
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Text(
                    text = pack.formattedFileSize,
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Metrics Stats Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, CardBorderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatCount(pack.downloads),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(text = "Downloads", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(CardBorderColor)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatCount(pack.likes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(text = "Likes", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(CardBorderColor)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = pack.version,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                    Text(text = "Pack Version", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }

        // Main Download Button & Progress
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Button(
                onClick = { viewModel.initiateDownload(pack) },
                enabled = downloadProgress == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("download_pack_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (downloadProgress != null) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = TextPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Downloading ${(downloadProgress!! * 100).toInt()}%...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DOWNLOAD PACK (${pack.formattedFileSize})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            if (downloadProgress != null) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { downloadProgress!! },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NeonCyan,
                    trackColor = SurfaceElevated
                )
            }

            if (downloadSuccess != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ $downloadSuccess",
                    color = MinecraftGreen,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Action Buttons Row: Like, Favorite, Report
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    isLiked = !isLiked
                    viewModel.toggleLike(pack.id, !isLiked)
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("like_pack_button"),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (isLiked) MinecraftRed else CardBorderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isLiked) MinecraftRed else TextPrimary
                )
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) MinecraftRed else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isLiked) "Liked" else "Like")
            }

            OutlinedButton(
                onClick = {
                    isFavorited = !isFavorited
                    viewModel.toggleFavorite(pack.id, !isFavorited)
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("favorite_pack_button"),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (isFavorited) MinecraftGold else CardBorderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isFavorited) MinecraftGold else TextPrimary
                )
            ) {
                Icon(
                    imageVector = if (isFavorited) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorited) MinecraftGold else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isFavorited) "Saved" else "Favorite")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Row: Description, Screenshots, Changelog
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceDark,
            contentColor = TextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonPurpleGlow
                )
            }
        ) {
            listOf("Description", "Screenshots", "Changelog").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) TextPrimary else TextSecondary
                        )
                    }
                )
            }
        }

        // Tab Contents
        when (selectedTab) {
            0 -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About this pack",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = pack.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tags",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        pack.tagList.forEach { tag ->
                            Surface(
                                color = SurfaceElevated,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, CardBorderColor)
                            ) {
                                Text(
                                    text = "#$tag",
                                    color = NeonPurpleGlow,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Screenshots Preview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.banner_minecraft),
                            contentDescription = "In-game screenshot",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            2 -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Changelog",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = SurfaceDark,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = pack.changelog,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }

    // Report Dialog
    if (showReportDialog) {
        Dialog(onDismissRequest = { showReportDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark,
                border = BorderStroke(1.dp, NeonPurpleGlow)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Report Texture Pack",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Help maintain safety across ReyasNova community.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = reportExpanded,
                        onExpandedChange = { reportExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = reportReason,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Reason for report") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reportExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = SurfaceElevated,
                                unfocusedContainerColor = SurfaceElevated
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = reportExpanded,
                            onDismissRequest = { reportExpanded = false }
                        ) {
                            Report.REASONS.forEach { reason ->
                                DropdownMenuItem(
                                    text = { Text(reason) },
                                    onClick = {
                                        reportReason = reason
                                        reportExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showReportDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                viewModel.submitReport(pack.id, pack.name, reportReason)
                                showReportDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MinecraftRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Submit Report", color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

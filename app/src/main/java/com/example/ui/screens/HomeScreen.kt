package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TexturePack
import com.example.ui.components.CategoryCubeItem
import com.example.ui.components.FeaturedPackCard
import com.example.ui.components.POPULAR_CATEGORIES
import com.example.ui.components.TrendingPackRow
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.DeepDarkBg
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MinecraftGold
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
fun HomeScreen(
    viewModel: ReyasNovaViewModel,
    modifier: Modifier = Modifier
) {
    val publicPacks by viewModel.publicPacks.collectAsState()
    val activeAnnouncements by viewModel.activeAnnouncements.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val downloadSuccess by viewModel.downloadSuccess.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val featuredPacks = publicPacks.filter { it.isFeatured }
        .ifEmpty { publicPacks.take(4) }
    val trendingPacks = publicPacks.sortedByDescending { it.downloads }.take(5)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepDarkBg)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo & Brand
                Image(
                    painter = painterResource(id = R.drawable.ic_reyasnova_logo),
                    contentDescription = "ReyasNova Logo",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ReyasNova",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Create. Share. Play.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonPurpleGlow
                    )
                }

                // If admin, show Admin badge button
                if (currentUser?.isAdmin == true) {
                    Surface(
                        onClick = { viewModel.navigateTo(Screen.AdminPanel) },
                        shape = RoundedCornerShape(8.dp),
                        color = NeonPurple.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, NeonPurpleGlow),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("admin_panel_shortcut")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Admin",
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Admin",
                                color = NeonCyan,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // User Avatar / Login Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (currentUser != null) NeonPurple else SurfaceElevated)
                        .clickable {
                            if (currentUser != null) {
                                viewModel.navigateTo(Screen.UserDashboard)
                            } else {
                                viewModel.navigateTo(Screen.AuthScreen)
                            }
                        }
                        .testTag("top_bar_user_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser?.displayName?.take(1)?.uppercase() ?: "?",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Search Input Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        viewModel.searchQuery.value = it
                        if (it.isNotBlank()) {
                            viewModel.navigateTo(Screen.BrowsePacks)
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Search packs, creators, or tags...",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }
        }

        // Active Announcement Banner
        if (activeAnnouncements.isNotEmpty()) {
            item {
                val ann = activeAnnouncements.first()
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonPurple.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, NeonPurpleGlow.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Announcement",
                            tint = MinecraftGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ann.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = ann.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Download Feedback Toast
        if (downloadSuccess != null) {
            item {
                Surface(
                    color = NeonCyan.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, NeonCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✓ $downloadSuccess",
                        color = NeonCyan,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        // Hero Banner (Featured Visual)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_minecraft),
                        contentDescription = "Minecraft Wallpaper Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scrim gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        DeepDarkBg.copy(alpha = 0.75f),
                                        DeepDarkBg.copy(alpha = 0.95f)
                                    ),
                                    startY = 50f
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "Discover & Share\nMinecraft Texture Packs",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            lineHeight = 28.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Upload your creations, discover community packs, and customize your Minecraft experience.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.navigateTo(Screen.BrowsePacks) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                modifier = Modifier.testTag("hero_browse_packs_button")
                            ) {
                                Text(
                                    text = "Browse Packs",
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                onClick = {
                                    if (currentUser != null) {
                                        viewModel.navigateTo(Screen.UploadPack)
                                    } else {
                                        viewModel.navigateTo(Screen.AuthScreen)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                                border = BorderStroke(1.dp, CardBorderColor),
                                modifier = Modifier.testTag("hero_upload_pack_button")
                            ) {
                                Text(
                                    text = "Upload Your Pack",
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Popular Categories Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular Categories",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { viewModel.navigateTo(Screen.BrowsePacks) }) {
                        Text(
                            text = "View All →",
                            color = NeonPurpleGlow,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(POPULAR_CATEGORIES) { cat ->
                        CategoryCubeItem(
                            category = cat,
                            isSelected = selectedCategory == cat.name,
                            onClick = {
                                viewModel.selectedCategory.value = cat.name
                                viewModel.navigateTo(Screen.BrowsePacks)
                            }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Featured Packs Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Featured Packs",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { viewModel.navigateTo(Screen.BrowsePacks) }) {
                        Text(
                            text = "View All →",
                            color = NeonPurpleGlow,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(featuredPacks) { pack ->
                        FeaturedPackCard(
                            pack = pack,
                            onClick = { viewModel.navigateTo(Screen.PackDetails(pack.id)) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Trending Packs Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Trending Packs",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = {
                        viewModel.selectedSort.value = "Most Downloads"
                        viewModel.navigateTo(Screen.BrowsePacks)
                    }) {
                        Text(
                            text = "View All →",
                            color = NeonPurpleGlow,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                trendingPacks.forEach { pack ->
                    TrendingPackRow(
                        pack = pack,
                        onClick = { viewModel.navigateTo(Screen.PackDetails(pack.id)) },
                        onQuickDownload = { viewModel.initiateDownload(pack) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Demo Role Switcher Quick Bar
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Active Account Demo Switcher",
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonCyan
                    )
                    Text(
                        text = "Switch roles instantly to test both Creator & Admin moderation features:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = { viewModel.switchUserRole("USER") },
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentUser?.role == "USER" && currentUser != null) NeonPurple else SurfaceDark,
                            border = BorderStroke(1.dp, CardBorderColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Creator",
                                color = TextPrimary,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            onClick = { viewModel.switchUserRole("ADMIN") },
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentUser?.isAdmin == true) NeonPurple else SurfaceDark,
                            border = BorderStroke(1.dp, CardBorderColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Admin",
                                color = TextPrimary,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            onClick = { viewModel.switchUserRole("GUEST") },
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentUser == null) NeonPurple else SurfaceDark,
                            border = BorderStroke(1.dp, CardBorderColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Visitor",
                                color = TextPrimary,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

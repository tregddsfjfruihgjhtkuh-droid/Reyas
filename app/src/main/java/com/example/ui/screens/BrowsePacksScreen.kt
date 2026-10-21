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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.TexturePack
import com.example.ui.components.FeaturedPackCard
import com.example.ui.components.TrendingPackRow
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.DeepDarkBg
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
fun BrowsePacksScreen(
    viewModel: ReyasNovaViewModel,
    modifier: Modifier = Modifier
) {
    val packs by viewModel.filteredPacks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()
    val selectedVer by viewModel.selectedMcVersion.collectAsState()
    val selectedRes by viewModel.selectedResolution.collectAsState()
    val selectedSort by viewModel.selectedSort.collectAsState()

    val categories = listOf("All") + TexturePack.CATEGORIES.take(10)
    val versions = listOf("All") + TexturePack.MC_VERSIONS
    val resolutions = listOf("All") + TexturePack.RESOLUTIONS.take(5)
    val sortOptions = listOf("Newest", "Most Downloads", "Most Likes", "Popular")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepDarkBg)
            .testTag("browse_packs_screen")
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.testTag("browse_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Community Packs",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Search packs, creators, tags...", color = TextMuted) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("browse_search_input"),
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

        // Filter Rows
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCat == cat,
                    onClick = { viewModel.selectedCategory.value = cat },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonPurple,
                        selectedLabelColor = TextPrimary,
                        containerColor = SurfaceElevated,
                        labelColor = TextSecondary
                    ),
                    border = BorderStroke(1.dp, if (selectedCat == cat) NeonPurpleGlow else CardBorderColor)
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Versions
            items(versions) { ver ->
                FilterChip(
                    selected = selectedVer == ver,
                    onClick = { viewModel.selectedMcVersion.value = ver },
                    label = { Text(if (ver == "All") "All MC" else ver) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SurfaceElevated,
                        selectedLabelColor = NeonPurpleGlow,
                        containerColor = SurfaceDark,
                        labelColor = TextMuted
                    ),
                    border = BorderStroke(1.dp, if (selectedVer == ver) NeonPurple else CardBorderColor)
                )
            }
            // Resolutions
            items(resolutions) { res ->
                FilterChip(
                    selected = selectedRes == res,
                    onClick = { viewModel.selectedResolution.value = res },
                    label = { Text(res) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SurfaceElevated,
                        selectedLabelColor = NeonPurpleGlow,
                        containerColor = SurfaceDark,
                        labelColor = TextMuted
                    ),
                    border = BorderStroke(1.dp, if (selectedRes == res) NeonPurple else CardBorderColor)
                )
            }
        }

        // Sorting Row & Count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${packs.size} packs found",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(sortOptions) { sort ->
                    Surface(
                        onClick = { viewModel.selectedSort.value = sort },
                        shape = RoundedCornerShape(6.dp),
                        color = if (selectedSort == sort) NeonPurple.copy(alpha = 0.2f) else SurfaceDark,
                        border = BorderStroke(1.dp, if (selectedSort == sort) NeonPurple else CardBorderColor)
                    ) {
                        Text(
                            text = sort,
                            color = if (selectedSort == sort) NeonPurpleGlow else TextSecondary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // List of Packs
        if (packs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "No packs",
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No texture packs matched your filters",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "Try clearing search keywords or selecting 'All' categories.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(packs) { pack ->
                    TrendingPackRow(
                        pack = pack,
                        onClick = { viewModel.navigateTo(Screen.PackDetails(pack.id)) },
                        onQuickDownload = { viewModel.initiateDownload(pack) }
                    )
                }
            }
        }
    }
}

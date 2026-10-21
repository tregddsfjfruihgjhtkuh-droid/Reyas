package com.example.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleGlow
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.ReyasNovaViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun ReyasNovaBottomBar(
    viewModel: ReyasNovaViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    NavigationBar(
        containerColor = SurfaceDark,
        contentColor = TextPrimary,
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("main_bottom_nav_bar")
    ) {
        // Home Tab
        val isHome = currentScreen is Screen.Home
        NavigationBarItem(
            selected = isHome,
            onClick = { viewModel.navigateTo(Screen.Home) },
            icon = {
                Icon(
                    imageVector = if (isHome) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            colors = navItemColors(),
            modifier = Modifier.testTag("nav_tab_home")
        )

        // Browse Packs Tab
        val isBrowse = currentScreen is Screen.BrowsePacks
        NavigationBarItem(
            selected = isBrowse,
            onClick = { viewModel.navigateTo(Screen.BrowsePacks) },
            icon = {
                Icon(
                    imageVector = if (isBrowse) Icons.Filled.Explore else Icons.Outlined.Explore,
                    contentDescription = "Browse"
                )
            },
            label = { Text("Browse") },
            colors = navItemColors(),
            modifier = Modifier.testTag("nav_tab_browse")
        )

        // Upload Pack Tab
        val isUpload = currentScreen is Screen.UploadPack
        NavigationBarItem(
            selected = isUpload,
            onClick = {
                if (currentUser != null) {
                    viewModel.navigateTo(Screen.UploadPack)
                } else {
                    viewModel.navigateTo(Screen.AuthScreen)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.CloudUpload,
                    contentDescription = "Upload"
                )
            },
            label = { Text("Upload") },
            colors = navItemColors(),
            modifier = Modifier.testTag("nav_tab_upload")
        )

        // Profile / Dashboard Tab
        val isProfile = currentScreen is Screen.UserDashboard || currentScreen is Screen.AuthScreen
        NavigationBarItem(
            selected = isProfile,
            onClick = {
                if (currentUser != null) {
                    viewModel.navigateTo(Screen.UserDashboard)
                } else {
                    viewModel.navigateTo(Screen.AuthScreen)
                }
            },
            icon = {
                Icon(
                    imageVector = if (isProfile) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            colors = navItemColors(),
            modifier = Modifier.testTag("nav_tab_profile")
        )
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = TextPrimary,
    selectedTextColor = NeonPurpleGlow,
    indicatorColor = NeonPurple,
    unselectedIconColor = TextMuted,
    unselectedTextColor = TextMuted
)

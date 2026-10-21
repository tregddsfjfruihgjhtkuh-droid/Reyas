package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.ReyasNovaBottomBar
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BrowsePacksScreen
import com.example.ui.screens.CreatorProfileScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PackDetailsScreen
import com.example.ui.screens.UploadPackScreen
import com.example.ui.screens.UserDashboardScreen
import com.example.ui.theme.DeepDarkBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ReyasNovaViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: ReyasNovaViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()

                // Hardware & Predictive Back Handling
                BackHandler(enabled = currentScreen !is Screen.Home) {
                    if (!viewModel.navigateBack()) {
                        viewModel.navigateTo(Screen.Home)
                    }
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepDarkBg),
                    bottomBar = {
                        ReyasNovaBottomBar(viewModel = viewModel)
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (val screen = currentScreen) {
                            is Screen.Home -> HomeScreen(viewModel = viewModel)
                            is Screen.BrowsePacks -> BrowsePacksScreen(viewModel = viewModel)
                            is Screen.PackDetails -> PackDetailsScreen(packId = screen.packId, viewModel = viewModel)
                            is Screen.UploadPack -> UploadPackScreen(viewModel = viewModel)
                            is Screen.UserDashboard -> UserDashboardScreen(viewModel = viewModel)
                            is Screen.CreatorProfile -> CreatorProfileScreen(creatorId = screen.creatorId, viewModel = viewModel)
                            is Screen.AdminPanel -> AdminPanelScreen(viewModel = viewModel)
                            is Screen.AuthScreen -> AuthScreen(viewModel = viewModel)
                            is Screen.Guidelines -> BrowsePacksScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

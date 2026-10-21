package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TexturePack
import com.example.ui.components.CategoryBadge
import com.example.ui.components.McVersionBadge
import com.example.ui.components.ResolutionBadge
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.DeepDarkBg
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
fun UploadPackScreen(
    viewModel: ReyasNovaViewModel,
    modifier: Modifier = Modifier
) {
    val step by viewModel.uploadStep.collectAsState()
    val name by viewModel.uploadName.collectAsState()
    val desc by viewModel.uploadDesc.collectAsState()
    val mcVersion by viewModel.uploadMcVersion.collectAsState()
    val resolution by viewModel.uploadResolution.collectAsState()
    val category by viewModel.uploadCategory.collectAsState()
    val tags by viewModel.uploadTags.collectAsState()
    val fileName by viewModel.uploadFileName.collectAsState()
    val changelog by viewModel.uploadChangelog.collectAsState()
    val error by viewModel.uploadError.collectAsState()
    val isSubmitting by viewModel.uploadIsSubmitting.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var mcExpanded by remember { mutableStateOf(false) }
    var catExpanded by remember { mutableStateOf(false) }
    var resExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("upload_pack_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.testTag("upload_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Upload Texture Pack",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Stepper Header (1 Details -> 2 Files -> 3 Preview)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepIndicator(stepNumber = 1, title = "Details", isActive = step == 1, isCompleted = step > 1)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(if (step > 1) NeonPurple else CardBorderColor)
            )
            StepIndicator(stepNumber = 2, title = "Files", isActive = step == 2, isCompleted = step > 2)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(if (step > 2) NeonPurple else CardBorderColor)
            )
            StepIndicator(stepNumber = 3, title = "Preview", isActive = step == 3, isCompleted = false)
        }

        // Error message if any
        if (error != null) {
            Surface(
                color = MinecraftRed.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, MinecraftRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = "Error", tint = MinecraftRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = error!!, color = MinecraftRed, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Step Content
        when (step) {
            1 -> {
                // STEP 1: DETAILS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Pack Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { viewModel.uploadName.value = it },
                        label = { Text("Texture Pack Name") },
                        placeholder = { Text("e.g. Purple PvP 16x") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_name_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = textFieldColors()
                    )

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { viewModel.uploadDesc.value = it },
                        label = { Text("Description") },
                        placeholder = { Text("Describe your textures, design highlights, UI changes...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("upload_desc_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = textFieldColors(),
                        maxLines = 4
                    )

                    // Minecraft Version Dropdown
                    ExposedDropdownMenuBox(
                        expanded = mcExpanded,
                        onExpandedChange = { mcExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = mcVersion,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Minecraft Target Version") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mcExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = mcExpanded,
                            onDismissRequest = { mcExpanded = false }
                        ) {
                            TexturePack.MC_VERSIONS.forEach { ver ->
                                DropdownMenuItem(
                                    text = { Text(ver) },
                                    onClick = {
                                        viewModel.uploadMcVersion.value = ver
                                        mcExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = catExpanded,
                        onExpandedChange = { catExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = catExpanded,
                            onDismissRequest = { catExpanded = false }
                        ) {
                            TexturePack.CATEGORIES.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        viewModel.uploadCategory.value = cat
                                        catExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Resolution Dropdown
                    ExposedDropdownMenuBox(
                        expanded = resExpanded,
                        onExpandedChange = { resExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = resolution,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Resolution") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = resExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = resExpanded,
                            onDismissRequest = { resExpanded = false }
                        ) {
                            TexturePack.RESOLUTIONS.forEach { res ->
                                DropdownMenuItem(
                                    text = { Text(res) },
                                    onClick = {
                                        viewModel.uploadResolution.value = res
                                        resExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = tags,
                        onValueChange = { viewModel.uploadTags.value = it },
                        label = { Text("Tags (comma separated)") },
                        placeholder = { Text("pvp, 16x, purple, smooth") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_tags_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = textFieldColors()
                    )

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                viewModel.uploadError.value = "Pack name is required."
                            } else {
                                viewModel.uploadError.value = null
                                viewModel.uploadStep.value = 2
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("upload_next_step1"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Next: Files & Assets →", fontWeight = FontWeight.Bold)
                    }
                }
            }
            2 -> {
                // STEP 2: FILES & ASSETS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Resource Pack Files",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Pack Zip File Selector
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FolderZip, contentDescription = "ZIP", tint = NeonCyan)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Resource Pack Archive (.zip)", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                    Text("Only valid Minecraft .zip resource packs allowed", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = fileName,
                                onValueChange = { viewModel.uploadFileName.value = it },
                                label = { Text("Package Filename") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors()
                            )
                        }
                    }

                    // Pack Icon & Banner Selector
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_reyasnova_logo),
                                contentDescription = "Pack Icon",
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Pack Icon: pack.png", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("High-resolution 128x128 cube icon configured", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = changelog,
                        onValueChange = { viewModel.uploadChangelog.value = it },
                        label = { Text("Initial Changelog / Release Notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        colors = textFieldColors()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.uploadStep.value = 1 },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("← Back", color = TextSecondary)
                        }
                        Button(
                            onClick = { viewModel.uploadStep.value = 3 },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("upload_next_step2"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text("Next: Preview →", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            3 -> {
                // STEP 3: PREVIEW & CONFIRMATION
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Submission Review",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Summary Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "by ${currentUser?.displayName ?: "Creator"}", color = NeonPurpleGlow, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                McVersionBadge(version = mcVersion)
                                ResolutionBadge(resolution = resolution)
                                CategoryBadge(category = category)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }

                    // Security & Moderation Disclosure
                    Surface(
                        color = NeonPurple.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, NeonPurpleGlow.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = "Security", tint = NeonCyan)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Every texture pack undergoes automated security scans and staff moderation before public publishing.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.uploadStep.value = 2 },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("← Edit", color = TextSecondary)
                        }
                        Button(
                            onClick = { viewModel.submitUpload() },
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .weight(2f)
                                .height(48.dp)
                                .testTag("upload_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = TextPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Scanning & Submitting...")
                            } else {
                                Text("Submit for Review ✓", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicator(stepNumber: Int, title: String, isActive: Boolean, isCompleted: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> MinecraftGreen
                        isActive -> NeonPurple
                        else -> SurfaceElevated
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
            } else {
                Text(
                    text = stepNumber.toString(),
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            color = if (isActive || isCompleted) TextPrimary else TextMuted,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = SurfaceDark,
    unfocusedContainerColor = SurfaceDark,
    focusedBorderColor = NeonPurple,
    unfocusedBorderColor = CardBorderColor,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)

package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.MinecraftGold
import com.example.ui.theme.MinecraftGreen
import com.example.ui.theme.MinecraftRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleGlow
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class CategoryItem(
    val name: String,
    val icon: ImageVector,
    val accentColor: Color
)

val POPULAR_CATEGORIES = listOf(
    CategoryItem("PvP", Icons.Default.LocalFireDepartment, MinecraftRed),
    CategoryItem("Survival", Icons.Default.Eco, MinecraftGreen),
    CategoryItem("UI", Icons.Default.Dashboard, NeonCyan),
    CategoryItem("Animated", Icons.Default.Animation, NeonPurpleGlow),
    CategoryItem("Bedwars", Icons.Default.Bed, FlameOrange),
    CategoryItem("SMP", Icons.Default.Public, MinecraftGold),
    CategoryItem("Faithful", Icons.Default.Shield, ElectricBlue),
    CategoryItem("FPS Boost", Icons.Default.Speed, MinecraftGreen)
)

@Composable
fun CategoryCubeItem(
    category: CategoryItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .testTag("category_cube_${category.name.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) SurfaceElevated else SurfaceDark,
        border = BorderStroke(
            1.dp,
            if (isSelected) category.accentColor else CardBorderColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                category.accentColor.copy(alpha = 0.25f),
                                category.accentColor.copy(alpha = 0.08f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = category.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = category.name,
                color = if (isSelected) category.accentColor else TextPrimary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

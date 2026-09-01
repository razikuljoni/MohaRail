package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BdRailGreenDark
import com.example.ui.theme.BdRailGreenLight
import com.example.ui.theme.BdRailGreenPrimary
import com.example.ui.theme.BdRailOrangeAccent
import com.example.ui.viewmodel.AppTab

@Composable
fun AppBottomNavBar(
    currentTab: AppTab,
    isBengali: Boolean,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("main_bottom_nav_bar")
    ) {
        val items = listOf(
            Triple(AppTab.LIVE_TRACK, Icons.Default.LocationOn, if (isBengali) "ট্র্যাকিং" else "Tracking"),
            Triple(AppTab.SCHEDULES, Icons.Default.Schedule, if (isBengali) "সময়সূচী" else "Schedules"),
            Triple(AppTab.TICKETING, Icons.Default.ConfirmationNumber, if (isBengali) "ই-টিকিট" else "E-Tickets"),
            Triple(AppTab.STATION_BOARD, Icons.Default.Dashboard, if (isBengali) "স্টেশন" else "Stations"),
            Triple(AppTab.SYSTEM_DOCS, Icons.Default.Code, if (isBengali) "রোডম্যাপ" else "System API")
        )

        items.forEach { (tab, icon, label) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BdRailGreenDark,
                    selectedTextColor = BdRailGreenDark,
                    indicatorColor = BdRailGreenLight,
                    unselectedIconColor = Color(0xFF78909C),
                    unselectedTextColor = Color(0xFF78909C)
                ),
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
            )
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SearchHistoryEntity
import com.example.data.model.SeatClass
import com.example.data.model.Station
import com.example.data.model.Train
import com.example.data.model.TrainType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulesScreen(
    isBengali: Boolean,
    allStations: List<Station>,
    searchResults: List<Train>,
    searchHistory: List<SearchHistoryEntity>,
    originStation: Station?,
    destStation: Station?,
    onSetOrigin: (Station) -> Unit,
    onSetDest: (Station) -> Unit,
    onSwapStations: () -> Unit,
    onTrackTrain: (String) -> Unit,
    onViewTrainDetail: (Train) -> Unit,
    onBookTicket: (Train, SeatClass) -> Unit,
    onGlobalSearch: ((String) -> Unit)? = null
) {
    var showOriginMenu by remember { mutableStateOf(false) }
    var showDestMenu by remember { mutableStateOf(false) }
    var originInputText by remember { mutableStateOf("") }
    var destInputText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("01 Sep 2026") }
    var searchModeTab by remember { mutableStateOf(0) } // 0: Route Search, 1: Quick Train Search
    var quickQuery by remember { mutableStateOf("") }
    var selectedTrainTypeFilter by remember { mutableStateOf<TrainType?>(null) }

    LaunchedEffect(originStation, isBengali) {
        originInputText = originStation?.let { if (isBengali) it.nameBn else it.nameEn } ?: "Dhaka"
    }

    LaunchedEffect(destStation, isBengali) {
        destInputText = destStation?.let { if (isBengali) it.nameBn else it.nameEn } ?: "Chattogram"
    }

    val filteredOriginStations = remember(originInputText, allStations) {
        val q = originInputText.trim().lowercase()
        if (q.isBlank()) {
            allStations
        } else {
            allStations.filter { st ->
                st.nameEn.lowercase().contains(q) ||
                st.nameBn.contains(q) ||
                st.code.lowercase().contains(q) ||
                st.district.lowercase().contains(q) ||
                st.division.lowercase().contains(q)
            }
        }
    }

    val filteredDestStations = remember(destInputText, allStations) {
        val q = destInputText.trim().lowercase()
        if (q.isBlank()) {
            allStations
        } else {
            allStations.filter { st ->
                st.nameEn.lowercase().contains(q) ||
                st.nameBn.contains(q) ||
                st.code.lowercase().contains(q) ||
                st.district.lowercase().contains(q) ||
                st.division.lowercase().contains(q)
            }
        }
    }

    val dateOptions = listOf("Today, 01 Sep", "Tomorrow, 02 Sep", "03 Sep", "04 Sep", "05 Sep")

    val displayedTrains = remember(searchResults, quickQuery, selectedTrainTypeFilter, searchModeTab) {
        if (searchModeTab == 1 && quickQuery.isNotBlank()) {
            searchResults.filter { tr ->
                val q = quickQuery.trim().lowercase()
                (tr.trainNo.lowercase().contains(q) ||
                 tr.nameEn.lowercase().contains(q) ||
                 tr.nameBn.contains(q) ||
                 tr.originStationNameEn.lowercase().contains(q) ||
                 tr.originStationNameBn.contains(q) ||
                 tr.destStationNameEn.lowercase().contains(q) ||
                 tr.destStationNameBn.contains(q) ||
                 tr.routeStops.any { it.stationNameEn.lowercase().contains(q) || it.stationNameBn.contains(q) }) &&
                (selectedTrainTypeFilter == null || tr.type == selectedTrainTypeFilter)
            }
        } else if (selectedTrainTypeFilter != null) {
            searchResults.filter { it.type == selectedTrainTypeFilter }
        } else {
            searchResults
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .testTag("screen_schedules"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Mode Switcher Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Tab switcher
                    TabRow(
                        selectedTabIndex = searchModeTab,
                        containerColor = Color(0xFFF1F8E9),
                        contentColor = BdRailGreenDark,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = searchModeTab == 0,
                            onClick = { searchModeTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.AltRoute, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text(if (isBengali) "রুট ও সময়সূচী" else "Route Search", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                        Tab(
                            selected = searchModeTab == 1,
                            onClick = { searchModeTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text(if (isBengali) "ট্রেন বা স্টেশন খুঁজুন" else "Find Train/Station", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (searchModeTab == 0) {
                        val tfColors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimaryLight,
                            unfocusedTextColor = TextPrimaryLight,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BdRailGreenDark,
                            unfocusedBorderColor = Color(0xFFB0BEC5),
                            focusedLabelColor = BdRailGreenDark,
                            unfocusedLabelColor = TextSecondaryLight,
                            focusedPlaceholderColor = Color(0xFF78909C),
                            unfocusedPlaceholderColor = Color(0xFF90A4AE),
                            focusedLeadingIconColor = BdRailGreenPrimary,
                            unfocusedLeadingIconColor = BdRailGreenPrimary,
                            cursorColor = BdRailGreenDark
                        )

                        // Origin & Destination Box with Swap Button
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Origin Station Selector
                                ExposedDropdownMenuBox(
                                    expanded = showOriginMenu,
                                    onExpandedChange = { showOriginMenu = it }
                                ) {
                                    OutlinedTextField(
                                        value = originInputText,
                                        onValueChange = { query ->
                                            originInputText = query
                                            showOriginMenu = true
                                        },
                                        label = { Text(if (isBengali) "হতে (Origin Station)" else "From (Origin Station)", fontSize = 11.sp) },
                                        placeholder = { Text(if (isBengali) "স্টেশন নাম বা কোড দিয়ে খুঁজুন..." else "Type station name or code...", fontSize = 12.sp) },
                                        leadingIcon = {
                                            Icon(Icons.Default.TripOrigin, contentDescription = null, tint = BdRailGreenPrimary, modifier = Modifier.size(18.dp))
                                        },
                                        trailingIcon = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (originInputText.isNotBlank()) {
                                                    IconButton(
                                                        onClick = {
                                                            originInputText = ""
                                                            showOriginMenu = true
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondaryLight, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showOriginMenu)
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = tfColors,
                                        singleLine = true,
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                            .testTag("dropdown_origin_station")
                                    )
                                    ExposedDropdownMenu(
                                        expanded = showOriginMenu,
                                        onDismissRequest = {
                                            showOriginMenu = false
                                            originInputText = originStation?.let { if (isBengali) it.nameBn else it.nameEn } ?: "Dhaka"
                                        },
                                        modifier = Modifier
                                            .background(Color.White)
                                            .heightIn(max = 280.dp)
                                    ) {
                                        if (filteredOriginStations.isEmpty()) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = if (isBengali) "কোনো স্টেশন পাওয়া যায়নি" else "No matching stations found",
                                                        fontSize = 12.sp,
                                                        color = TextSecondaryLight
                                                    )
                                                },
                                                onClick = {}
                                            )
                                        } else {
                                            filteredOriginStations.forEach { station ->
                                                val isSelected = station.code == originStation?.code
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                                                Text(
                                                                    text = if (isBengali) station.nameBn else station.nameEn,
                                                                    fontSize = 13.sp,
                                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                                    color = if (isSelected) BdRailGreenDark else TextPrimaryLight
                                                                )
                                                                Text(
                                                                    text = if (isBengali) "${station.nameEn} • ${station.district}" else "${station.nameBn} • ${station.district}",
                                                                    fontSize = 10.sp,
                                                                    color = TextSecondaryLight
                                                                )
                                                            }
                                                            Surface(
                                                                shape = RoundedCornerShape(4.dp),
                                                                color = if (isSelected) BdRailGreenPrimary.copy(alpha = 0.15f) else Color(0xFFECEFF1)
                                                            ) {
                                                                Text(
                                                                    text = station.code,
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (isSelected) BdRailGreenDark else TextSecondaryLight,
                                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }
                                                    },
                                                    onClick = {
                                                        onSetOrigin(station)
                                                        originInputText = if (isBengali) station.nameBn else station.nameEn
                                                        showOriginMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Destination Station Selector
                                ExposedDropdownMenuBox(
                                    expanded = showDestMenu,
                                    onExpandedChange = { showDestMenu = it }
                                ) {
                                    OutlinedTextField(
                                        value = destInputText,
                                        onValueChange = { query ->
                                            destInputText = query
                                            showDestMenu = true
                                        },
                                        label = { Text(if (isBengali) "গন্তব্য (Destination Station)" else "To (Destination Station)", fontSize = 11.sp) },
                                        placeholder = { Text(if (isBengali) "স্টেশন নাম বা কোড দিয়ে খুঁজুন..." else "Type station name or code...", fontSize = 12.sp) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Place, contentDescription = null, tint = BdRailOrangeAccent, modifier = Modifier.size(18.dp))
                                        },
                                        trailingIcon = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (destInputText.isNotBlank()) {
                                                    IconButton(
                                                        onClick = {
                                                            destInputText = ""
                                                            showDestMenu = true
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondaryLight, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDestMenu)
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = tfColors,
                                        singleLine = true,
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                            .testTag("dropdown_dest_station")
                                    )
                                    ExposedDropdownMenu(
                                        expanded = showDestMenu,
                                        onDismissRequest = {
                                            showDestMenu = false
                                            destInputText = destStation?.let { if (isBengali) it.nameBn else it.nameEn } ?: "Chattogram"
                                        },
                                        modifier = Modifier
                                            .background(Color.White)
                                            .heightIn(max = 280.dp)
                                    ) {
                                        if (filteredDestStations.isEmpty()) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = if (isBengali) "কোনো স্টেশন পাওয়া যায়নি" else "No matching stations found",
                                                        fontSize = 12.sp,
                                                        color = TextSecondaryLight
                                                    )
                                                },
                                                onClick = {}
                                            )
                                        } else {
                                            filteredDestStations.forEach { station ->
                                                val isSelected = station.code == destStation?.code
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                                                Text(
                                                                    text = if (isBengali) station.nameBn else station.nameEn,
                                                                    fontSize = 13.sp,
                                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                                    color = if (isSelected) BdRailOrangeAccent else TextPrimaryLight
                                                                )
                                                                Text(
                                                                    text = if (isBengali) "${station.nameEn} • ${station.district}" else "${station.nameBn} • ${station.district}",
                                                                    fontSize = 10.sp,
                                                                    color = TextSecondaryLight
                                                                )
                                                            }
                                                            Surface(
                                                                shape = RoundedCornerShape(4.dp),
                                                                color = if (isSelected) BdRailOrangeAccent.copy(alpha = 0.15f) else Color(0xFFECEFF1)
                                                            ) {
                                                                Text(
                                                                    text = station.code,
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (isSelected) BdRailOrangeAccent else TextSecondaryLight,
                                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }
                                                    },
                                                    onClick = {
                                                        onSetDest(station)
                                                        destInputText = if (isBengali) station.nameBn else station.nameEn
                                                        showDestMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Floating Swap Button
                            IconButton(
                                onClick = onSwapStations,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 40.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BdRailGreenDark)
                                    .testTag("btn_swap_stations")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Swap",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Date Selection Chips
                        Text(
                            text = if (isBengali) "যাত্রার তারিখ:" else "Journey Date:",
                            fontSize = 11.sp,
                            color = TextSecondaryLight,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            dateOptions.forEach { dt ->
                                val isSelected = selectedDate == dt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedDate = dt },
                                    label = { Text(dt, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BdRailGreenPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFFF0F4F4),
                                        labelColor = TextPrimaryLight
                                    )
                                )
                            }
                        }
                    } else {
                        // Quick Train / Station Universal Search
                        OutlinedTextField(
                            value = quickQuery,
                            onValueChange = {
                                quickQuery = it
                                onGlobalSearch?.invoke(it)
                            },
                            label = { Text(if (isBengali) "ট্রেনের নাম, নম্বর বা স্টেশন লিখুন" else "Search Train Name, No (701, 805) or Station", fontSize = 11.sp) },
                            placeholder = { Text("e.g. 805, Chilahati, সুবর্ণ, Cox's Bazar", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = BdRailGreenPrimary)
                            },
                            trailingIcon = {
                                if (quickQuery.isNotEmpty()) {
                                    IconButton(onClick = {
                                        quickQuery = ""
                                        onGlobalSearch?.invoke("")
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimaryLight,
                                unfocusedTextColor = TextPrimaryLight,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = BdRailGreenDark,
                                unfocusedBorderColor = Color(0xFFB0BEC5),
                                focusedLabelColor = BdRailGreenDark,
                                unfocusedLabelColor = TextSecondaryLight,
                                focusedPlaceholderColor = Color(0xFF78909C),
                                unfocusedPlaceholderColor = Color(0xFF90A4AE),
                                cursorColor = BdRailGreenDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_quick_global_search")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Train Type Filters
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedTrainTypeFilter == null,
                            onClick = { selectedTrainTypeFilter = null },
                            label = { Text(if (isBengali) "সকল ট্রেন" else "All Trains", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BdRailOrangeAccent,
                                selectedLabelColor = Color.White
                            )
                        )
                        TrainType.values().forEach { tType ->
                            val isSel = selectedTrainTypeFilter == tType
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedTrainTypeFilter = if (isSel) null else tType },
                                label = { Text(if (isBengali) tType.labelBn else tType.labelEn, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BdRailGreenDark,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Recent Search History (Only on Route Search Mode)
        if (searchModeTab == 0 && searchHistory.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
                    Text(
                        text = if (isBengali) "সাম্প্রতিক অনুসন্ধান" else "Recent Searches",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        searchHistory.take(5).forEach { history ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = BorderStroke(0.5.dp, Color(0xFFCFD8DC)),
                                modifier = Modifier.clickable {
                                    val orig = allStations.find { it.code == history.originStationCode }
                                    val dst = allStations.find { it.code == history.destStationCode }
                                    if (orig != null && dst != null) {
                                        onSetOrigin(orig)
                                        onSetDest(dst)
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.History, contentDescription = null, tint = TextSecondaryLight, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "${history.originStationName} ➔ ${history.destStationName}",
                                        fontSize = 11.sp,
                                        color = TextPrimaryLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search Results Header
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengali) "উপলব্ধ ট্রেনসমূহ (${displayedTrains.size})" else "Available Trains (${displayedTrains.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BdRailGreenDark
                )

                Text(
                    text = if (isBengali) "মহারেল স্মার্ট সময়সূচী" else "MohaRail Timetable",
                    fontSize = 11.sp,
                    color = TextSecondaryLight
                )
            }
        }

        // Train Result Cards
        items(displayedTrains) { train ->
            TrainScheduleCard(
                train = train,
                isBengali = isBengali,
                onTrackTrain = { onTrackTrain(train.trainNo) },
                onViewDetail = { onViewTrainDetail(train) },
                onBookTicket = { seatClass -> onBookTicket(train, seatClass) }
            )
        }
    }
}

@Composable
fun TrainScheduleCard(
    train: Train,
    isBengali: Boolean,
    onTrackTrain: () -> Unit,
    onViewDetail: () -> Unit,
    onBookTicket: (SeatClass) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("card_train_${train.trainNo}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Train Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = BdRailGreenLight,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = train.trainNo,
                            fontWeight = FontWeight.Bold,
                            color = BdRailGreenDark,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (isBengali) train.nameBn else train.nameEn,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BdRailGreenDark
                            )
                            Text(
                                text = "(${if (isBengali) train.nameEn else train.nameBn})",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                        }
                        Text(
                            text = if (isBengali) train.type.labelBn else train.type.labelEn,
                            fontSize = 10.sp,
                            color = BdRailOrangeAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Off-day indicator
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isBengali) "ছুটি: ${train.offDayBn}" else "Off: ${train.offDayEn}",
                        color = Color(0xFFE65100),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time & Route Stoppage Visualizer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Departure
                Column {
                    Text(
                        text = train.departureTime,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = if (isBengali) train.originStationNameBn else train.originStationNameEn,
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                }

                // Middle Route line with distance
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = "${train.totalDistanceKm} km",
                        fontSize = 10.sp,
                        color = TextSecondaryLight
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(BdRailGreenPrimary)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = BdRailGreenPrimary,
                            thickness = 1.5.dp
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = BdRailGreenPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(BdRailOrangeAccent)
                        )
                    }
                    Text(
                        text = if (isBengali) "${train.routeStops.size}টি বিরতি • ${train.totalCoaches} বগি" else "${train.routeStops.size} Stops • ${train.totalCoaches} Coaches",
                        fontSize = 9.sp,
                        color = BdRailGreenDark
                    )
                }

                // Arrival
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = train.arrivalTime,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = if (isBengali) train.destStationNameBn else train.destStationNameEn,
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(8.dp))

            // Fare Pills Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FareChip(label = if (isBengali) "শোভন চেয়ার" else "S_CHAIR", fare = "৳${train.baseFareSChair}") {
                    onBookTicket(SeatClass.S_CHAIR)
                }
                FareChip(label = if (isBengali) "স্নিগ্ধা (এসি)" else "Snigdha", fare = "৳${train.baseFareSnigdha}") {
                    onBookTicket(SeatClass.SNIGDHA)
                }
                FareChip(label = if (isBengali) "এসি সিট" else "AC Seat", fare = "৳${train.baseFareAcSeat}") {
                    onBookTicket(SeatClass.AC_S)
                }
                FareChip(label = if (isBengali) "এসি বার্থ" else "AC Berth", fare = "৳${train.baseFareAcBerth}") {
                    onBookTicket(SeatClass.AC_B)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Live Track Button
                Button(
                    onClick = onTrackTrain,
                    colors = ButtonDefaults.buttonColors(containerColor = BdRailGreenDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("btn_track_${train.trainNo}")
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBengali) "লাইভ ট্র্যাক" else "Track Live",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Stoppage & Bogie Timetable Button
                OutlinedButton(
                    onClick = onViewDetail,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BdRailGreenDark),
                    modifier = Modifier.weight(1f).testTag("btn_detail_${train.trainNo}")
                ) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = null, tint = BdRailGreenDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBengali) "স্টপেজ ও বগি" else "Stops & Bogies",
                        fontSize = 11.sp,
                        color = BdRailGreenDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FareChip(
    label: String,
    fare: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF1F8E9),
        border = BorderStroke(0.5.dp, BdRailGreenContainer),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = label, fontSize = 10.sp, color = TextPrimaryLight)
            Text(text = fare, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BdRailGreenDark)
        }
    }
}

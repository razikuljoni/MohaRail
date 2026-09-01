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
    onBookTicket: (Train, SeatClass) -> Unit
) {
    var showOriginMenu by remember { mutableStateOf(false) }
    var showDestMenu by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("01 Sep 2026") }

    val dateOptions = listOf("Today, 01 Sep", "Tomorrow, 02 Sep", "03 Sep", "04 Sep", "05 Sep")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .testTag("screen_schedules"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Station Search Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBengali) "ট্রেন খুঁজুন ও সময়সূচী" else "Find Trains & Schedule",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BdRailGreenDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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
                                    value = originStation?.let { if (isBengali) it.nameBn else it.nameEn } ?: "Dhaka",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(if (isBengali) "হতে (Origin)" else "From (Origin)", fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.TripOrigin, contentDescription = null, tint = BdRailGreenPrimary, modifier = Modifier.size(18.dp))
                                    },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showOriginMenu) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                        .testTag("dropdown_origin_station")
                                )
                                ExposedDropdownMenu(
                                    expanded = showOriginMenu,
                                    onDismissRequest = { showOriginMenu = false }
                                ) {
                                    allStations.forEach { station ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "${if (isBengali) station.nameBn else station.nameEn} (${station.code})",
                                                    fontSize = 13.sp
                                                )
                                            },
                                            onClick = {
                                                onSetOrigin(station)
                                                showOriginMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Destination Station Selector
                            ExposedDropdownMenuBox(
                                expanded = showDestMenu,
                                onExpandedChange = { showDestMenu = it }
                            ) {
                                OutlinedTextField(
                                    value = destStation?.let { if (isBengali) it.nameBn else it.nameEn } ?: "Chattogram",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(if (isBengali) "গন্তব্য (Destination)" else "To (Destination)", fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Place, contentDescription = null, tint = BdRailOrangeAccent, modifier = Modifier.size(18.dp))
                                    },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDestMenu) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                        .testTag("dropdown_dest_station")
                                )
                                ExposedDropdownMenu(
                                    expanded = showDestMenu,
                                    onDismissRequest = { showDestMenu = false }
                                ) {
                                    allStations.forEach { station ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "${if (isBengali) station.nameBn else station.nameEn} (${station.code})",
                                                    fontSize = 13.sp
                                                )
                                            },
                                            onClick = {
                                                onSetDest(station)
                                                showDestMenu = false
                                            }
                                        )
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

                    Spacer(modifier = Modifier.height(12.dp))

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
                }
            }
        }

        // Recent Search History
        if (searchHistory.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(
                        text = if (isBengali) "সাম্প্রতিক অনুসন্ধান" else "Recent Searches",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
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
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengali) "উপলব্ধ ট্রেনসমূহ (${searchResults.size})" else "Available Trains (${searchResults.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BdRailGreenDark
                )

                Text(
                    text = if (isBengali) "সব আন্তঃনগর" else "All Intercity",
                    fontSize = 11.sp,
                    color = TextSecondaryLight
                )
            }
        }

        // Train Result Cards
        items(searchResults) { train ->
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
                    Text(
                        text = if (isBengali) train.nameBn else train.nameEn,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BdRailGreenDark
                    )
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
                        text = if (isBengali) "${train.routeStops.size}টি বিরতি" else "${train.routeStops.size} Stoppages",
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
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBengali) "লাইভ ট্র্যাক" else "Track Live",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Stoppage Timetable Button
                OutlinedButton(
                    onClick = onViewDetail,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BdRailGreenDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = null, tint = BdRailGreenDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBengali) "স্টপেজ রুট" else "Stoppages",
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

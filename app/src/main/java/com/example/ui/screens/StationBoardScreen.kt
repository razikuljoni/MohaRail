package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Station
import com.example.data.model.StationArrivalDeparture
import com.example.ui.theme.*

@Composable
fun StationBoardScreen(
    isBengali: Boolean,
    allStations: List<Station>,
    selectedStation: Station?,
    stationBoardList: List<StationArrivalDeparture>,
    onSelectStation: (Station) -> Unit,
    onTrackTrain: (String) -> Unit
) {
    val context = LocalContext.current
    var boardFilterArrivals by remember { mutableStateOf<Boolean?>(null) } // null = All, true = Arrivals, false = Departures

    val currentStation = selectedStation ?: allStations.first()

    val filteredList = when (boardFilterArrivals) {
        true -> stationBoardList.filter { it.isArrival }
        false -> stationBoardList.filter { !it.isArrival }
        null -> stationBoardList
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .testTag("screen_station_board"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Station Selector Chips Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BdRailGreenDark)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = if (isBengali) "লাইভ স্টেশন ডিসপ্লে বোর্ড" else "Live Station Electronic Board",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = if (isBengali) "স্টেশন নির্বাচন করে ট্রেন আগমন ও প্রস্থান দেখুন" else "Select station to view digital departure/arrival board",
                    color = Color(0xFFB2DFDB),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allStations.forEach { st ->
                        val isSelected = st.code == currentStation.code
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectStation(st) },
                            label = {
                                Text(
                                    text = if (isBengali) st.nameBn else st.nameEn,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BdRailOrangeAccent,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0x33FFFFFF),
                                labelColor = Color.White
                            ),
                            border = null
                        )
                    }
                }
            }
        }

        // Filter Tabs (All / Arrivals / Departures)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    // All
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (boardFilterArrivals == null) BdRailGreenPrimary else Color.Transparent)
                            .clickable { boardFilterArrivals = null }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBengali) "সকল (${stationBoardList.size})" else "All (${stationBoardList.size})",
                            fontSize = 11.sp,
                            fontWeight = if (boardFilterArrivals == null) FontWeight.Bold else FontWeight.Medium,
                            color = if (boardFilterArrivals == null) Color.White else TextSecondaryLight
                        )
                    }

                    // Arrivals
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (boardFilterArrivals == true) BdRailGreenPrimary else Color.Transparent)
                            .clickable { boardFilterArrivals = true }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBengali) "আগমন (Arrivals)" else "Arrivals",
                            fontSize = 11.sp,
                            fontWeight = if (boardFilterArrivals == true) FontWeight.Bold else FontWeight.Medium,
                            color = if (boardFilterArrivals == true) Color.White else TextSecondaryLight
                        )
                    }

                    // Departures
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (boardFilterArrivals == false) BdRailGreenPrimary else Color.Transparent)
                            .clickable { boardFilterArrivals = false }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBengali) "প্রস্থান (Departures)" else "Departures",
                            fontSize = 11.sp,
                            fontWeight = if (boardFilterArrivals == false) FontWeight.Bold else FontWeight.Medium,
                            color = if (boardFilterArrivals == false) Color.White else TextSecondaryLight
                        )
                    }
                }
            }
        }

        // Live Station Board Table List
        items(filteredList) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .clickable { onTrackTrain(item.trainNo) }
                    .testTag("station_board_train_${item.trainNo}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Train No & Name & Direction
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = if (item.isArrival) BdRailGreenLight else Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = item.trainNo,
                                    color = if (item.isArrival) BdRailGreenDark else Color(0xFFE65100),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }

                            Text(
                                text = if (isBengali) item.trainNameBn else item.trainNameEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimaryLight
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = item.originOrDest,
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }

                    // Times & Platform & Status
                    Column(horizontalAlignment = Alignment.End) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.scheduledTime,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )

                            Surface(
                                color = BdRailGreenDark,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "প্লাটফর্ম ${item.platform}" else "PF ${item.platform}",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        val isDelay = item.delayMinutes > 0
                        Text(
                            text = if (isDelay) {
                                if (isBengali) "দেরি: +${item.delayMinutes}মি (${item.expectedTime})" else "Late: +${item.delayMinutes}m (${item.expectedTime})"
                            } else {
                                if (isBengali) "সঠিক সময়ে" else "On Time"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDelay) BdRailRedDelay else BdRailGreenOnTime
                        )
                    }
                }
            }
        }

        // Station Emergency Contacts & Directory
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBFB)),
                border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBengali) "🚨 ${currentStation.nameBn} স্টেশন জরুরি সহায়তা ও ডিরেক্টরি" else "🚨 ${currentStation.nameEn} Station Directory & Helpline",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BdRailGreenDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    DirectoryRow(
                        title = if (isBengali) "স্টেশন মাস্টার (Station Master)" else "Station Master Office",
                        phone = currentStation.emergencyPhone,
                        context = context
                    )

                    DirectoryRow(
                        title = if (isBengali) "রেলওয়ে পুলিশ হেল্পলাইন (GRP Police)" else "Railway Police (GRP)",
                        phone = "01711691999",
                        context = context
                    )

                    DirectoryRow(
                        title = if (isBengali) "জাতীয় জরুরি সেবা (National Helpline)" else "National Emergency Hotline",
                        phone = "999",
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
fun DirectoryRow(
    title: String,
    phone: String,
    context: android.content.Context
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 11.sp, color = TextPrimaryLight)
            Text(text = phone, fontSize = 10.sp, color = TextSecondaryLight)
        }

        IconButton(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {}
            },
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(BdRailGreenLight)
        ) {
            Icon(Icons.Default.Phone, contentDescription = "Call", tint = BdRailGreenDark, modifier = Modifier.size(14.dp))
        }
    }
}

package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CrowdLocationReport
import com.example.data.model.LiveTrainStatus
import com.example.data.model.RouteStop
import com.example.data.model.StopStatus
import com.example.data.model.Train
import com.example.ui.theme.*

@Composable
fun LiveTrackingScreen(
    isBengali: Boolean,
    trackedTrainNo: String,
    liveStatus: LiveTrainStatus?,
    isGpsMode: Boolean,
    allTrains: List<Train>,
    crowdReports: List<com.example.data.model.CrowdLocationReport>,
    onSelectTrain: (String) -> Unit,
    onSetTrackingMode: (Boolean) -> Unit,
    onOpenCrowdReport: () -> Unit,
    onOpenAlarm: () -> Unit,
    onViewTrainDetails: (Train) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val currentTrain = allTrains.find { it.trainNo == trackedTrainNo } ?: allTrains.first()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .testTag("screen_live_tracking"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Search and Quick Selection Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BdRailGreenDark, BdRailGreenPrimary)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isBengali) "ট্রেনের নাম বা নম্বর দিয়ে খুঁজুন (যেমন: 701, সুবর্ণ)" else "Search train name or no (e.g. 701, Suborno)",
                            fontSize = 13.sp,
                            color = Color(0xFFCFD8DC)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = BdRailOrangeAccent,
                        focusedBorderColor = BdRailOrangeAccent,
                        unfocusedBorderColor = Color(0x66FFFFFF),
                        focusedContainerColor = Color(0x22000000),
                        unfocusedContainerColor = Color(0x22000000)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_train_search")
                )

                // Search autocomplete suggestions if user is typing
                val filteredTrains = if (searchQuery.isNotBlank()) {
                    allTrains.filter {
                        it.trainNo.contains(searchQuery.trim(), ignoreCase = true) ||
                                it.nameEn.contains(searchQuery.trim(), ignoreCase = true) ||
                                it.nameBn.contains(searchQuery.trim())
                    }
                } else emptyList()

                if (filteredTrains.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            filteredTrains.take(4).forEach { tr ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelectTrain(tr.trainNo)
                                            searchQuery = ""
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${tr.trainNo} - ${if (isBengali) tr.nameBn else tr.nameEn}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = BdRailGreenDark
                                        )
                                        Text(
                                            text = "${if (isBengali) tr.originStationNameBn else tr.originStationNameEn} ➔ ${if (isBengali) tr.destStationNameBn else tr.destStationNameEn}",
                                            fontSize = 11.sp,
                                            color = TextSecondaryLight
                                        )
                                    }
                                    Surface(
                                        color = BdRailGreenLight,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (isBengali) "ট্র্যাক করুন" else "Track",
                                            color = BdRailGreenDark,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFECEFF1))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Popular Trains Chips
                Text(
                    text = if (isBengali) "জনপ্রিয় ট্রেনসমূহ:" else "Popular Trains:",
                    color = Color(0xFFB2DFDB),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allTrains.forEach { train ->
                        val isSelected = train.trainNo == trackedTrainNo
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectTrain(train.trainNo) },
                            label = {
                                Text(
                                    text = "${train.trainNo} ${if (isBengali) train.nameBn.take(8) else train.nameEn.take(9)}",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        Icons.Default.Train,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BdRailOrangeAccent,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White,
                                containerColor = Color(0x33FFFFFF),
                                labelColor = Color.White
                            ),
                            border = null,
                            modifier = Modifier.testTag("chip_train_${train.trainNo}")
                        )
                    }
                }
            }
        }

        // Tracking Mode Switcher: GPS Radar vs SMS Tracker (16318)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFECEFF1), RoundedCornerShape(10.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // GPS Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isGpsMode) BdRailGreenPrimary else Color.Transparent)
                                .clickable { onSetTrackingMode(true) }
                                .padding(vertical = 8.dp)
                                .testTag("btn_mode_gps"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GpsFixed,
                                    contentDescription = null,
                                    tint = if (isGpsMode) Color.White else TextSecondaryLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isBengali) "লাইভ জিপিএস রাডার" else "Live GPS Radar",
                                    color = if (isGpsMode) Color.White else TextSecondaryLight,
                                    fontSize = 12.sp,
                                    fontWeight = if (isGpsMode) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }

                        // SMS 16318 Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isGpsMode) BdRailGreenPrimary else Color.Transparent)
                                .clickable { onSetTrackingMode(false) }
                                .padding(vertical = 8.dp)
                                .testTag("btn_mode_sms"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sms,
                                    contentDescription = null,
                                    tint = if (!isGpsMode) Color.White else TextSecondaryLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isBengali) "SMS ট্র্যাকিং (১৬৩১৮)" else "SMS Tracking (16318)",
                                    color = if (!isGpsMode) Color.White else TextSecondaryLight,
                                    fontSize = 12.sp,
                                    fontWeight = if (!isGpsMode) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    // SMS Tracking Explainer / Launcher
                    if (!isGpsMode) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = BdRailOrangeLight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = BdRailOrangeAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (isBengali) "বাংলাদেশ রেলওয়ে অফিসিয়াল এসএমএস ট্র্যাকিং" else "Official Bangladesh Railway SMS Format",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFFE65100)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBengali)
                                        "মোবাইল থেকে ১৬৩১৮ নম্বরে এসএমএস পাঠান: TR ${currentTrain.trainNo}"
                                    else
                                        "Send SMS to 16318: TR ${currentTrain.trainNo} (Standard SMS charge applies)",
                                    fontSize = 11.sp,
                                    color = TextPrimaryLight
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("sms:16318?body=TR ${currentTrain.trainNo}")
                                        }
                                        try {
                                            context.startActivity(smsIntent)
                                        } catch (_: Exception) {}
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BdRailOrangeAccent),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_send_sms_query")
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isBengali) "১৬৩১৮ নম্বরে SMS পাঠান (TR ${currentTrain.trainNo})" else "Send SMS to 16318 (TR ${currentTrain.trainNo})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Tracking Hero Status Card
        item {
            liveStatus?.let { status ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Train Name & Delay Badge Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        color = BdRailGreenLight,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = status.trainNo,
                                            color = BdRailGreenDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = if (isBengali) status.trainNameBn else status.trainNameEn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = BdRailGreenDark
                                    )
                                }
                                Text(
                                    text = "${if (isBengali) currentTrain.originStationNameBn else currentTrain.originStationNameEn} ➔ ${if (isBengali) currentTrain.destStationNameBn else currentTrain.destStationNameEn}",
                                    fontSize = 12.sp,
                                    color = TextSecondaryLight
                                )
                            }

                            // Delay Status Badge
                            val isDelay = status.delayMinutes > 0
                            Surface(
                                color = if (isDelay) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isDelay) BdRailRedDelay else BdRailGreenOnTime)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isDelay) BdRailRedDelay else BdRailGreenOnTime)
                                    )
                                    Text(
                                        text = if (isDelay) {
                                            if (isBengali) "${status.delayMinutes} মিনিট লেট" else "${status.delayMinutes}m Late"
                                        } else {
                                            if (isBengali) "সঠিক সময়ে" else "Right Time"
                                        },
                                        color = if (isDelay) BdRailRedDelay else BdRailGreenOnTime,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFFECEFF1))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Real-time Metrics (Current Location, Next Station, Speedometer)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Current Location
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBengali) "বর্তমান অবস্থান" else "Current Location",
                                    fontSize = 10.sp,
                                    color = TextSecondaryLight
                                )
                                Text(
                                    text = if (isBengali) status.currentStationBn else status.currentStationEn,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BdRailGreenDark
                                )
                                Text(
                                    text = if (isBengali) "আউটার / স্টেশন পাস করছে" else "At / Passing Station",
                                    fontSize = 10.sp,
                                    color = BdRailOrangeAccent
                                )
                            }

                            // Next Station
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBengali) "পরবর্তী স্টেশন" else "Next Station",
                                    fontSize = 10.sp,
                                    color = TextSecondaryLight
                                )
                                Text(
                                    text = if (isBengali) status.nextStationBn else status.nextStationEn,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryLight
                                )
                                Text(
                                    text = if (isBengali) "আনুমানিক ১৫ মিনিট" else "ETA ~15 mins",
                                    fontSize = 10.sp,
                                    color = TextSecondaryLight
                                )
                            }

                            // Speed Gauge
                            Surface(
                                color = BdRailGreenDark,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${status.currentSpeedKmh}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "km/h",
                                        fontSize = 9.sp,
                                        color = BdRailGreenLight
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Active Crowdsourcing Signal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F8E9), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = BdRailGreenOnTime,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isBengali) "${status.activeCrowdReportersCount} জন যাত্রী লাইভ ডাটা শেয়ার করছেন" else "${status.activeCrowdReportersCount} passengers reporting live",
                                    fontSize = 11.sp,
                                    color = BdRailGreenDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Text(
                                text = if (isBengali) "সক্রিয়" else "Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BdRailGreenOnTime
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons Row (I am in this train, Wake-up alarm, Full timetable)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Share Live Location
                Button(
                    onClick = onOpenCrowdReport,
                    colors = ButtonDefaults.buttonColors(containerColor = BdRailOrangeAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_share_live_location")
                ) {
                    Icon(Icons.Default.ShareLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBengali) "আমি ট্রেনে আছি" else "Share Speed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Set Alarm
                OutlinedButton(
                    onClick = onOpenAlarm,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BdRailGreenDark),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_set_wake_alarm")
                ) {
                    Icon(Icons.Default.Alarm, contentDescription = null, tint = BdRailGreenDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBengali) "অ্যালার্ম দিন" else "Set Alarm",
                        fontSize = 11.sp,
                        color = BdRailGreenDark,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Full Timetable
                OutlinedButton(
                    onClick = { onViewTrainDetails(currentTrain) },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF90A4AE)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_view_full_stoppages")
                ) {
                    Icon(Icons.Default.List, contentDescription = null, tint = TextPrimaryLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBengali) "স্টপেজ রুট" else "Route",
                        fontSize = 11.sp,
                        color = TextPrimaryLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Live Station-by-Station Journey Line Section
        item {
            Text(
                text = if (isBengali) "লাইভ রুট ও স্টপেজ ট্র্যাকার" else "Live Journey Stoppage Tracker",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BdRailGreenDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        liveStatus?.let { status ->
            items(status.stopsWithLiveStatus) { stop ->
                StationStoppageRow(
                    stop = stop,
                    isBengali = isBengali,
                    delayMinutes = status.delayMinutes
                )
            }
        }

        // Recent Crowd Passenger Reports
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Forum,
                                contentDescription = null,
                                tint = BdRailGreenDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isBengali) "সহযাত্রীদের লাইভ আপডেট" else "Passenger Live Updates",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = BdRailGreenDark
                            )
                        }

                        Text(
                            text = if (isBengali) "সর্বশেষ" else "Live Feed",
                            fontSize = 11.sp,
                            color = BdRailOrangeAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    crowdReports.take(3).forEach { report ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(BdRailGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = report.reporterName.take(1),
                                    fontWeight = FontWeight.Bold,
                                    color = BdRailGreenDark,
                                    fontSize = 12.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${report.reporterName} (${report.coachName})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextPrimaryLight
                                    )
                                    Text(
                                        text = report.timeAgo,
                                        fontSize = 10.sp,
                                        color = TextSecondaryLight
                                    )
                                }
                                Text(
                                    text = "📍 ${report.passedStation} • ${report.currentSpeedKmh} km/h",
                                    fontSize = 11.sp,
                                    color = BdRailGreenDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = report.conditionNote,
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                    }
                }
            }
        }
    }
}

@Composable
fun StationStoppageRow(
    stop: RouteStop,
    isBengali: Boolean,
    delayMinutes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Scheduled Time Column
        Column(
            modifier = Modifier.width(64.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = stop.scheduledArrival,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = when (stop.status) {
                    StopStatus.PASSED -> TextSecondaryLight
                    StopStatus.CURRENT -> BdRailOrangeAccent
                    else -> TextPrimaryLight
                }
            )
            if (stop.haltMinutes > 0) {
                Text(
                    text = if (isBengali) "${stop.haltMinutes}মি. বিরতি" else "${stop.haltMinutes}m halt",
                    fontSize = 9.sp,
                    color = TextSecondaryLight
                )
            }
        }

        // Timeline Node and Vertical Line Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(16.dp)
                    .background(
                        when (stop.status) {
                            StopStatus.PASSED -> BdRailGreenPrimary
                            StopStatus.CURRENT -> BdRailOrangeAccent
                            else -> Color(0xFFCFD8DC)
                        }
                    )
            )

            // Status Node Indicator
            when (stop.status) {
                StopStatus.PASSED -> {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(BdRailGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Passed",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                StopStatus.CURRENT -> {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(BdRailOrangeLight)
                            .border(2.dp, BdRailOrangeAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Train,
                            contentDescription = "Current Train Location",
                            tint = BdRailOrangeAccent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, Color(0xFFB0BEC5), CircleShape)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(16.dp)
                    .background(
                        when (stop.status) {
                            StopStatus.PASSED -> BdRailGreenPrimary
                            else -> Color(0xFFCFD8DC)
                        }
                    )
            )
        }

        // Station Details & Live Status
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (stop.status) {
                    StopStatus.CURRENT -> Color(0xFFFFF8E1)
                    StopStatus.PASSED -> Color(0xFFF9FBFB)
                    else -> Color.White
                }
            ),
            border = if (stop.status == StopStatus.CURRENT) BorderStroke(1.dp, BdRailOrangeAccent) else BorderStroke(0.5.dp, Color(0xFFE0E0E0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBengali) stop.stationNameBn else stop.stationNameEn,
                        fontSize = 13.sp,
                        fontWeight = if (stop.status == StopStatus.CURRENT) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (stop.status == StopStatus.CURRENT) Color(0xFFE65100) else TextPrimaryLight
                    )
                    Text(
                        text = "${stop.distanceFromOriginKm} km • ${if (isBengali) "প্ল্যাটফর্ম ${stop.platform}" else "Platform ${stop.platform}"}",
                        fontSize = 10.sp,
                        color = TextSecondaryLight
                    )
                }

                // Actual Time / Status Pill
                when (stop.status) {
                    StopStatus.PASSED -> {
                        Text(
                            text = stop.actualArrivalTime ?: stop.scheduledArrival,
                            fontSize = 11.sp,
                            color = BdRailGreenDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    StopStatus.CURRENT -> {
                        Surface(
                            color = BdRailOrangeAccent,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isBengali) "চলমান..." else "Here Now",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    StopStatus.UPCOMING -> {
                        Text(
                            text = if (delayMinutes > 0) {
                                if (isBengali) "লেট: +${delayMinutes}মি" else "+${delayMinutes}m delay"
                            } else {
                                if (isBengali) "অন-টাইম" else "On Time"
                            },
                            fontSize = 10.sp,
                            color = if (delayMinutes > 0) BdRailRedDelay else BdRailGreenOnTime,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

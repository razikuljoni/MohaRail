package com.example.ui.components

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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.TrainAlarmEntity
import com.example.data.model.CoachInfo
import com.example.data.model.SeatClass
import com.example.data.model.Train
import com.example.ui.theme.*

@Composable
fun CrowdReportDialog(
    trainNo: String,
    isBengali: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (reporter: String, coach: String, note: String, speed: Int, passedStation: String) -> Unit
) {
    var reporterName by remember { mutableStateOf("") }
    var selectedCoach by remember { mutableStateOf("Coach Cha (চ)") }
    var speedText by remember { mutableStateOf("72") }
    var passedStation by remember { mutableStateOf("Outer Signal") }
    var conditionNote by remember { mutableStateOf("Train running smoothly, green signal ahead.") }

    val coachOptions = listOf("Coach Ka (ক)", "Coach Kha (খ)", "Coach Ga (গ)", "Coach Gha (ঘ)", "Coach Cha (চ)", "Coach Chha (ছ)", "Coach Ja (জ)", "Coach Jha (ঝ)")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.ShareLocation, contentDescription = null, tint = BdRailOrangeAccent)
                        Text(
                            text = if (isBengali) "লাইভ অবস্থান ও গতি শেয়ার" else "Share Live Train Location",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BdRailGreenDark
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reporterName,
                    onValueChange = { reporterName = it },
                    label = { Text(if (isBengali) "আপনার নাম" else "Your Name", fontSize = 11.sp) },
                    placeholder = { Text("e.g. Shakil Ahmed", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_crowd_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isBengali) "বগি (Coach):" else "Coach:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    coachOptions.forEach { coach ->
                        val isSelected = selectedCoach == coach
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCoach = coach },
                            label = { Text(coach, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BdRailGreenDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = passedStation,
                        onValueChange = { passedStation = it },
                        label = { Text(if (isBengali) "নিকটবর্তী স্টেশন" else "Near Station", fontSize = 11.sp) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("input_crowd_station")
                    )
                    OutlinedTextField(
                        value = speedText,
                        onValueChange = { speedText = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (isBengali) "গতি (km/h)" else "Speed km/h", fontSize = 11.sp) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.width(100.dp).testTag("input_crowd_speed")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = conditionNote,
                    onValueChange = { conditionNote = it },
                    label = { Text(if (isBengali) "মন্তব্য / ট্রেনের অবস্থা" else "Status Note", fontSize = 11.sp) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_crowd_note")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val spd = speedText.toIntOrNull() ?: 60
                        onSubmit(reporterName, selectedCoach, conditionNote, spd, passedStation)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BdRailGreenDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_submit_crowd_report")
                ) {
                    Text(
                        text = if (isBengali) "রিপোর্ট জমা দিন" else "Broadcast Live Radar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BookTicketDialog(
    train: Train,
    seatClass: SeatClass,
    isBengali: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (name: String, coach: String, seat: String) -> Unit
) {
    var passengerName by remember { mutableStateOf("") }
    var selectedCoach by remember { mutableStateOf("Cha (চ)") }
    var seatNumber by remember { mutableStateOf("45") }

    val fare = when (seatClass) {
        SeatClass.SHOVON -> train.baseFareShovon
        SeatClass.S_CHAIR -> train.baseFareSChair
        SeatClass.SNIGDHA -> train.baseFareSnigdha
        SeatClass.AC_S -> train.baseFareAcSeat
        SeatClass.AC_B -> train.baseFareAcBerth
        SeatClass.F_BERTH -> train.baseFareAcBerth + 200
    }
    val vat = (fare * 0.15).toInt()
    val total = fare + vat + 20

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = BdRailGreenPrimary)
                        Text(
                            text = if (isBengali) "ই-টিকিট বুকিং নিশ্চিতকরণ" else "Confirm E-Ticket",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BdRailGreenDark
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = Color(0xFFF1F8E9),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "${train.trainNo} - ${if (isBengali) train.nameBn else train.nameEn}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BdRailGreenDark
                        )
                        Text(
                            text = "${if (isBengali) train.originStationNameBn else train.originStationNameEn} ➔ ${if (isBengali) train.destStationNameBn else train.destStationNameEn}",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                        Text(
                            text = "${if (isBengali) "শ্রেণি:" else "Class:"} ${if (isBengali) seatClass.nameBn else seatClass.nameEn}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BdRailOrangeAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = passengerName,
                    onValueChange = { passengerName = it },
                    label = { Text(if (isBengali) "যাত্রীর নাম" else "Passenger Name", fontSize = 11.sp) },
                    placeholder = { Text("As per NID / Passport", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_ticket_passenger")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = selectedCoach,
                        onValueChange = { selectedCoach = it },
                        label = { Text(if (isBengali) "বগি" else "Coach", fontSize = 11.sp) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("input_ticket_coach")
                    )
                    OutlinedTextField(
                        value = seatNumber,
                        onValueChange = { seatNumber = it },
                        label = { Text(if (isBengali) "আসন নং" else "Seat No", fontSize = 11.sp) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("input_ticket_seat")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isBengali) "মোট ভাড়া (ভ্যাট ও ফি সহ):" else "Total Fare (Incl. VAT):", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text(text = "৳ $total", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BdRailGreenDark)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onConfirm(passengerName, selectedCoach, seatNumber) },
                    colors = ButtonDefaults.buttonColors(containerColor = BdRailGreenDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_confirm_ticket_purchase")
                ) {
                    Text(
                        text = if (isBengali) "টিকিট সংরক্ষণ ও ডাউনলোড করুন" else "Save Ticket to Wallet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AlarmDialog(
    alarms: List<TrainAlarmEntity>,
    trainNo: String,
    trainName: String,
    isBengali: Boolean,
    onDismiss: () -> Unit,
    onCreateAlarm: (trainNo: String, trainName: String, station: String, minutes: Int) -> Unit,
    onToggleAlarm: (id: Long, enabled: Boolean) -> Unit,
    onDeleteAlarm: (id: Long) -> Unit
) {
    var stationName by remember { mutableStateOf("Dhaka Airport") }
    var minutesBefore by remember { mutableStateOf(15) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Alarm, contentDescription = null, tint = BdRailOrangeAccent)
                        Text(
                            text = if (isBengali) "গন্তব্য অ্যালার্ম ম্যানেজার" else "Destination Alarm Radar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BdRailGreenDark
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = stationName,
                    onValueChange = { stationName = it },
                    label = { Text(if (isBengali) "গন্তব্য স্টেশন" else "Destination Station", fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_alarm_station")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isBengali) "স্টেশনে পৌঁছানোর কত মিনিট পূর্বে অ্যালার্ম বাজবে:" else "Alert before arrival:",
                    fontSize = 11.sp,
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10, 15, 20, 30).forEach { mins ->
                        val isSelected = minutesBefore == mins
                        FilterChip(
                            selected = isSelected,
                            onClick = { minutesBefore = mins },
                            label = { Text("$mins min", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BdRailGreenDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onCreateAlarm(trainNo, trainName, stationName, minutesBefore) },
                    colors = ButtonDefaults.buttonColors(containerColor = BdRailGreenDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_save_alarm")
                ) {
                    Text(text = if (isBengali) "অ্যালার্ম সেট করুন" else "Set Wake-up Alarm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (alarms.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = if (isBengali) "সক্রিয় অ্যালার্মসমূহ:" else "Active Alarms:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    Spacer(modifier = Modifier.height(6.dp))

                    alarms.forEach { al ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "${al.trainName} ➔ ${al.destinationStation}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(text = "${al.minutesBeforeArrival} mins before arrival", fontSize = 10.sp, color = TextSecondaryLight)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = al.isEnabled,
                                    onCheckedChange = { onToggleAlarm(al.id, it) }
                                )
                                IconButton(onClick = { onDeleteAlarm(al.id) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrainDetailModal(
    train: Train,
    isBengali: Boolean,
    onDismiss: () -> Unit,
    onTrack: () -> Unit
) {
    var selectedModalTab by remember { mutableStateOf(0) } // 0: Stops & Schedule, 1: Bogie & Coaches, 2: Fares

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header Bar
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
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = train.trainNo,
                                color = BdRailGreenDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isBengali) train.nameBn else train.nameEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = BdRailGreenDark
                            )
                            Text(
                                text = if (isBengali) train.type.labelBn else train.type.labelEn,
                                fontSize = 10.sp,
                                color = BdRailOrangeAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${if (isBengali) train.originStationNameBn else train.originStationNameEn} ➔ ${if (isBengali) train.destStationNameBn else train.destStationNameEn} • ${train.totalDistanceKm} km • ${if (isBengali) "ছুটির দিন: " + train.offDayBn else "Off: " + train.offDayEn}",
                    fontSize = 11.sp,
                    color = TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Switcher for Modal
                TabRow(
                    selectedTabIndex = selectedModalTab,
                    containerColor = Color(0xFFF1F8E9),
                    contentColor = BdRailGreenDark,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(
                        selected = selectedModalTab == 0,
                        onClick = { selectedModalTab = 0 },
                        text = { Text(if (isBengali) "স্টপেজ ও সময়" else "Stops", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedModalTab == 1,
                        onClick = { selectedModalTab = 1 },
                        text = { Text(if (isBengali) "বগি ও কোচ বিন্যাস" else "Bogies & Coaches", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedModalTab == 2,
                        onClick = { selectedModalTab = 2 },
                        text = { Text(if (isBengali) "ভাড়া তালিকা" else "Fare Matrix", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    when (selectedModalTab) {
                        0 -> {
                            // Stops list
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(train.routeStops) { stop ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = if (isBengali) stop.stationNameBn else stop.stationNameEn,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = TextPrimaryLight
                                            )
                                            Text(
                                                text = "${stop.distanceFromOriginKm} km • Platform ${stop.platform}",
                                                fontSize = 10.sp,
                                                color = TextSecondaryLight
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "Arr: ${stop.scheduledArrival} | Dep: ${stop.scheduledDeparture}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = BdRailGreenDark
                                            )
                                            if (stop.haltMinutes > 0) {
                                                Text(
                                                    text = "${stop.haltMinutes}m halt",
                                                    fontSize = 9.sp,
                                                    color = BdRailOrangeAccent
                                                )
                                            }
                                        }
                                    }
                                    HorizontalDivider(color = Color(0xFFF5F5F5))
                                }
                            }
                        }

                        1 -> {
                            // Bogie / Coach Layout
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Surface(
                                        color = Color(0xFFFFF8E1),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.Train, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(16.dp))
                                            Text(
                                                text = "${train.rakeType} (${train.totalCoaches} ${if (isBengali) "টি বগি" else "Coaches"})",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFE65100)
                                            )
                                        }
                                    }
                                }

                                items(train.coaches) { coach ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (coach.isAc) Color(0xFFE0F2F1) else Color(0xFFFAFAFA)
                                        ),
                                        border = BorderStroke(1.dp, if (coach.isAc) BdRailGreenPrimary else Color(0xFFE0E0E0))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(if (coach.isAc) BdRailGreenPrimary else BdRailGreenDark),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = if (isBengali) coach.coachLetterBn else coach.coachLetterEn,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                                Column {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Text(
                                                            text = if (isBengali) coach.coachClass.nameBn else coach.coachClass.nameEn,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            color = TextPrimaryLight
                                                        )
                                                        if (coach.isPantry) {
                                                            Surface(
                                                                color = BdRailOrangeLight,
                                                                shape = RoundedCornerShape(4.dp)
                                                            ) {
                                                                Text(
                                                                    text = if (isBengali) "খাবার বগি" else "Dining/Pantry",
                                                                    fontSize = 9.sp,
                                                                    color = Color(0xFFE65100),
                                                                    fontWeight = FontWeight.Bold,
                                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Text(
                                                        text = "${if (isBengali) "মোট আসন: " else "Seats: "}${coach.totalSeats} • ${if (coach.isAc) "Air Conditioned" else "Non-AC Fan"}",
                                                        fontSize = 10.sp,
                                                        color = TextSecondaryLight
                                                    )
                                                }
                                            }

                                            Icon(
                                                imageVector = if (coach.isAc) Icons.Default.AcUnit else Icons.Default.AirlineSeatReclineNormal,
                                                contentDescription = null,
                                                tint = if (coach.isAc) BdRailGreenPrimary else TextSecondaryLight,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // Fares matrix
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val fareList = listOf(
                                    Triple(SeatClass.SHOVON, train.baseFareShovon, "Non-AC 2x3 Seating"),
                                    Triple(SeatClass.S_CHAIR, train.baseFareSChair, "Comfortable Non-AC Chair 2x2"),
                                    Triple(SeatClass.SNIGDHA, train.baseFareSnigdha, "Air-Conditioned Chair Coach"),
                                    Triple(SeatClass.AC_S, train.baseFareAcSeat, "Deluxe AC Cabin Chair"),
                                    Triple(SeatClass.AC_B, train.baseFareAcBerth, "AC Sleeping Sleeper Berth")
                                )

                                fareList.forEach { (sClass, baseFare, desc) ->
                                    val vat = (baseFare * 0.15).toInt()
                                    val total = baseFare + vat + 20
                                    Surface(
                                        color = Color(0xFFF9FBE7),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = if (isBengali) sClass.nameBn else sClass.nameEn,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = BdRailGreenDark
                                                )
                                                Text(
                                                    text = desc,
                                                    fontSize = 10.sp,
                                                    color = TextSecondaryLight
                                                )
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "৳ $total",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = BdRailOrangeAccent
                                                )
                                                Text(
                                                    text = "(Base: ৳$baseFare + VAT)",
                                                    fontSize = 9.sp,
                                                    color = TextSecondaryLight
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onTrack,
                    colors = ButtonDefaults.buttonColors(containerColor = BdRailOrangeAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_modal_track_train")
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBengali) "এই ট্রেনটি লাইভ ট্র্যাক করুন" else "Track This Train Live",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

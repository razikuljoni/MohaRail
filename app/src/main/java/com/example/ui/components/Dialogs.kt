package com.example.ui.components

import androidx.compose.foundation.background
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
                            label = { Text(coach, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BdRailGreenDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = speedText,
                        onValueChange = { speedText = it },
                        label = { Text(if (isBengali) "গতি (km/h)" else "Speed (km/h)", fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("input_crowd_speed")
                    )

                    OutlinedTextField(
                        value = passedStation,
                        onValueChange = { passedStation = it },
                        label = { Text(if (isBengali) "নিকটবর্তী স্টেশন" else "Passing Station", fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.5f).testTag("input_crowd_station")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = conditionNote,
                    onValueChange = { conditionNote = it },
                    label = { Text(if (isBengali) "ট্রেনের অবস্থা বা মন্তব্য" else "Track Condition / Note", fontSize = 11.sp) },
                    singleLine = false,
                    maxLines = 2,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_crowd_note")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val speed = speedText.toIntOrNull() ?: 65
                        onSubmit(reporterName, selectedCoach, conditionNote, speed, passedStation)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BdRailOrangeAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_submit_crowd_report")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBengali) "লাইভ আপডেট পোস্ট করুন" else "Publish Live Update",
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
    var passengerName by remember { mutableStateOf("Md. Tanvir") }
    var selectedCoach by remember { mutableStateOf("Coach Cha (চ)") }
    var seatNumber by remember { mutableStateOf("Cha-34, Cha-35") }

    val baseFare = when (seatClass) {
        SeatClass.SHOVON -> train.baseFareShovon
        SeatClass.S_CHAIR -> train.baseFareSChair
        SeatClass.SNIGDHA -> train.baseFareSnigdha
        SeatClass.AC_S -> train.baseFareAcSeat
        SeatClass.AC_B -> train.baseFareAcBerth
        SeatClass.F_BERTH -> train.baseFareAcBerth + 200
    }
    val vat = (baseFare * 0.15).toInt()
    val total = baseFare + vat + 20

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
                    Text(
                        text = if (isBengali) "অনলাইন ই-টিকিট কনফার্মেশন" else "E-Ticket Booking Confirmation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BdRailGreenDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = Color(0xFFF1F8E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
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
                            text = "${if (isBengali) seatClass.nameBn else seatClass.nameEn} • ${train.departureTime}",
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
                    label = { Text(if (isBengali) "যাত্রীর নাম (NID অনুযায়ী)" else "Passenger Name (As per NID)", fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_booking_passenger_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = selectedCoach,
                    onValueChange = { selectedCoach = it },
                    label = { Text(if (isBengali) "বগি (Coach)" else "Coach", fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = seatNumber,
                    onValueChange = { seatNumber = it },
                    label = { Text(if (isBengali) "আসন নম্বর (Seat No)" else "Seat Number", fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isBengali) "মোট প্রদেয় টাকা:" else "Total Payable:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "৳$total", fontSize = 18.sp, fontWeight = FontWeight.Black, color = BdRailOrangeAccent)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onConfirm(passengerName, selectedCoach, seatNumber) },
                    colors = ButtonDefaults.buttonColors(containerColor = BdRailGreenDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_confirm_ticket_booking")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBengali) "টিকিট কনফার্ম ও ওয়ালেটে সংরক্ষণ" else "Confirm & Save to Wallet",
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
    onToggleAlarm: (Long, Boolean) -> Unit,
    onDeleteAlarm: (Long) -> Unit
) {
    var stationName by remember { mutableStateOf("Dhaka (Kamalapur)") }
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
                        Icon(Icons.Default.Alarm, contentDescription = null, tint = BdRailGreenDark)
                        Text(
                            text = if (isBengali) "স্টেশন আগমন অ্যালার্ম" else "Station Arrival Wake-up Alarm",
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
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                                color = BdRailGreenDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = if (isBengali) train.nameBn else train.nameEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BdRailGreenDark
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "${if (isBengali) train.originStationNameBn else train.originStationNameEn} ➔ ${if (isBengali) train.destStationNameBn else train.destStationNameEn} • ${train.totalDistanceKm} km",
                    fontSize = 11.sp,
                    color = TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFECEFF1))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isBengali) "সকল স্টপেজ ও সময়সূচী" else "All Stoppages & Timetable",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimaryLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
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

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
import com.example.data.local.entity.SavedTicketEntity
import com.example.data.model.SeatClass
import com.example.data.model.Station
import com.example.data.model.Train
import com.example.ui.theme.*

enum class TicketingTab(val labelEn: String, val labelBn: String) {
    FARE_CALCULATOR("Fare Calculator", "ভাড়া ক্যালকুলেটর"),
    MY_TICKETS("My Tickets Wallet", "আমার টিকিট"),
    E_TICKET_PORTAL("Official E-Ticket Portal", "অফিসিয়াল ই-টিকিট")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketingScreen(
    isBengali: Boolean,
    allStations: List<Station>,
    allTrains: List<Train>,
    savedTickets: List<SavedTicketEntity>,
    originStation: Station?,
    destStation: Station?,
    selectedSeatClass: SeatClass,
    passengerCount: Int,
    onSetOrigin: (Station) -> Unit,
    onSetDest: (Station) -> Unit,
    onSetSeatClass: (SeatClass) -> Unit,
    onSetPassengers: (Int) -> Unit,
    onBookTicket: (Train, SeatClass) -> Unit,
    onDeleteTicket: (Long) -> Unit
) {
    val context = LocalContext.current
    var activeSubTab by remember { mutableStateOf(TicketingTab.FARE_CALCULATOR) }

    var showOriginMenu by remember { mutableStateOf(false) }
    var showDestMenu by remember { mutableStateOf(false) }
    var originInputText by remember { mutableStateOf("") }
    var destInputText by remember { mutableStateOf("") }

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
                st.district.lowercase().contains(q)
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
                st.district.lowercase().contains(q)
            }
        }
    }

    val currentTrain = allTrains.firstOrNull() ?: allTrains[0]

    val baseFareSingle = when (selectedSeatClass) {
        SeatClass.SHOVON -> currentTrain.baseFareShovon
        SeatClass.S_CHAIR -> currentTrain.baseFareSChair
        SeatClass.SNIGDHA -> currentTrain.baseFareSnigdha
        SeatClass.AC_S -> currentTrain.baseFareAcSeat
        SeatClass.AC_B -> currentTrain.baseFareAcBerth
        SeatClass.F_BERTH -> currentTrain.baseFareAcBerth + 200
    }
    val totalBase = baseFareSingle * passengerCount
    val totalVat = (totalBase * 0.15).toInt()
    val totalService = 20 * passengerCount
    val grandTotal = totalBase + totalVat + totalService

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .testTag("screen_ticketing"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Sub-Tab Switcher
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    TicketingTab.values().forEach { tab ->
                        val isSelected = activeSubTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) BdRailGreenDark else Color.Transparent)
                                .clickable { activeSubTab = tab }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBengali) tab.labelBn else tab.labelEn,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondaryLight
                            )
                        }
                    }
                }
            }
        }

        when (activeSubTab) {
            TicketingTab.FARE_CALCULATOR -> {
                // Calculator Controls Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isBengali) "বাংলাদেশ রেলওয়ে সঠিক ভাড়া হিসাব" else "Bangladesh Railway Fare Calculation",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BdRailGreenDark
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val ticketingTfColors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimaryLight,
                                unfocusedTextColor = TextPrimaryLight,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = BdRailGreenDark,
                                unfocusedBorderColor = Color(0xFFB0BEC5),
                                focusedLabelColor = BdRailGreenDark,
                                unfocusedLabelColor = TextSecondaryLight,
                                cursorColor = BdRailGreenDark
                            )

                            // Stations Dropdowns
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Origin
                                ExposedDropdownMenuBox(
                                    expanded = showOriginMenu,
                                    onExpandedChange = { showOriginMenu = it },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = originInputText,
                                        onValueChange = { query ->
                                            originInputText = query
                                            showOriginMenu = true
                                        },
                                        label = { Text(if (isBengali) "হতে" else "From", fontSize = 10.sp) },
                                        placeholder = { Text(if (isBengali) "স্টেশন..." else "Station...", fontSize = 11.sp) },
                                        trailingIcon = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (originInputText.isNotBlank()) {
                                                    IconButton(
                                                        onClick = {
                                                            originInputText = ""
                                                            showOriginMenu = true
                                                        },
                                                        modifier = Modifier.size(20.dp)
                                                    ) {
                                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondaryLight, modifier = Modifier.size(14.dp))
                                                    }
                                                }
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showOriginMenu)
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ticketingTfColors,
                                        singleLine = true,
                                        modifier = Modifier.menuAnchor().fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = showOriginMenu,
                                        onDismissRequest = {
                                            showOriginMenu = false
                                            originInputText = originStation?.let { if (isBengali) it.nameBn else it.nameEn } ?: "Dhaka"
                                        },
                                        modifier = Modifier.background(Color.White).heightIn(max = 260.dp)
                                    ) {
                                        if (filteredOriginStations.isEmpty()) {
                                            DropdownMenuItem(
                                                text = { Text(if (isBengali) "পাওয়া যায়নি" else "No match", fontSize = 11.sp, color = TextSecondaryLight) },
                                                onClick = {}
                                            )
                                        } else {
                                            filteredOriginStations.forEach { st ->
                                                val isSelected = st.code == originStation?.code
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = if (isBengali) st.nameBn else st.nameEn,
                                                                fontSize = 12.sp,
                                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                                color = if (isSelected) BdRailGreenDark else TextPrimaryLight
                                                            )
                                                            Text(
                                                                text = st.code,
                                                                fontSize = 10.sp,
                                                                color = TextSecondaryLight
                                                            )
                                                        }
                                                    },
                                                    onClick = {
                                                        onSetOrigin(st)
                                                        originInputText = if (isBengali) st.nameBn else st.nameEn
                                                        showOriginMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Destination
                                ExposedDropdownMenuBox(
                                    expanded = showDestMenu,
                                    onExpandedChange = { showDestMenu = it },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = destInputText,
                                        onValueChange = { query ->
                                            destInputText = query
                                            showDestMenu = true
                                        },
                                        label = { Text(if (isBengali) "গন্তব্য" else "To", fontSize = 10.sp) },
                                        placeholder = { Text(if (isBengali) "স্টেশন..." else "Station...", fontSize = 11.sp) },
                                        trailingIcon = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (destInputText.isNotBlank()) {
                                                    IconButton(
                                                        onClick = {
                                                            destInputText = ""
                                                            showDestMenu = true
                                                        },
                                                        modifier = Modifier.size(20.dp)
                                                    ) {
                                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondaryLight, modifier = Modifier.size(14.dp))
                                                    }
                                                }
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDestMenu)
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ticketingTfColors,
                                        singleLine = true,
                                        modifier = Modifier.menuAnchor().fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = showDestMenu,
                                        onDismissRequest = {
                                            showDestMenu = false
                                            destInputText = destStation?.let { if (isBengali) it.nameBn else it.nameEn } ?: "Chattogram"
                                        },
                                        modifier = Modifier.background(Color.White).heightIn(max = 260.dp)
                                    ) {
                                        if (filteredDestStations.isEmpty()) {
                                            DropdownMenuItem(
                                                text = { Text(if (isBengali) "পাওয়া যায়নি" else "No match", fontSize = 11.sp, color = TextSecondaryLight) },
                                                onClick = {}
                                            )
                                        } else {
                                            filteredDestStations.forEach { st ->
                                                val isSelected = st.code == destStation?.code
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = if (isBengali) st.nameBn else st.nameEn,
                                                                fontSize = 12.sp,
                                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                                color = if (isSelected) BdRailOrangeAccent else TextPrimaryLight
                                                            )
                                                            Text(
                                                                text = st.code,
                                                                fontSize = 10.sp,
                                                                color = TextSecondaryLight
                                                            )
                                                        }
                                                    },
                                                    onClick = {
                                                        onSetDest(st)
                                                        destInputText = if (isBengali) st.nameBn else st.nameEn
                                                        showDestMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Seat Class Selector Chips
                            Text(
                                text = if (isBengali) "আসন শ্রেণি (Class):" else "Seat Class:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimaryLight
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SeatClass.values().forEach { sc ->
                                    val isSelected = selectedSeatClass == sc
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onSetSeatClass(sc) },
                                        label = {
                                            Text(
                                                text = if (isBengali) sc.nameBn else sc.nameEn,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BdRailGreenDark,
                                            selectedLabelColor = Color.White,
                                            containerColor = Color(0xFFF1F4F4),
                                            labelColor = TextPrimaryLight
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Passenger Count Counter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBengali) "যাত্রীর সংখ্যা (সর্বোচ্চ ৪ জন):" else "Passengers (Max 4):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimaryLight
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    IconButton(
                                        onClick = { onSetPassengers((passengerCount - 1).coerceAtLeast(1)) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFECEFF1))
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }

                                    Text(
                                        text = "$passengerCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = BdRailGreenDark
                                    )

                                    IconButton(
                                        onClick = { onSetPassengers((passengerCount + 1).coerceAtMost(4)) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFECEFF1))
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Fare Receipt Breakdown Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFDFA)),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isBengali) "ভাড়া বিবরণী (Receipt Breakdown)" else "Fare Breakdown Receipt",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BdRailGreenDark
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            ReceiptRow(
                                label = if (isBengali) "মূল ভাড়া (${selectedSeatClass.nameBn} x $passengerCount)" else "Base Fare (${selectedSeatClass.nameEn} x $passengerCount)",
                                value = "৳$totalBase"
                            )
                            ReceiptRow(
                                label = if (isBengali) "ভ্যাট (১৫% রেলওয়ে ট্যাক্স)" else "Railway VAT (15%)",
                                value = "৳$totalVat"
                            )
                            ReceiptRow(
                                label = if (isBengali) "ই-টিকিটিং সার্ভিস চার্জ (৳২০/টি)" else "E-Ticketing Processing Fee",
                                value = "৳$totalService"
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFCFD8DC))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBengali) "সর্বমোট প্রদেয় টাকা:" else "Total Payable:",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BdRailGreenDark
                                )
                                Text(
                                    text = "৳$grandTotal",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BdRailOrangeAccent
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Instant Book Button
                            Button(
                                onClick = { onBookTicket(currentTrain, selectedSeatClass) },
                                colors = ButtonDefaults.buttonColors(containerColor = BdRailOrangeAccent),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_book_now_calculator")
                            ) {
                                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBengali) "অনলাইনে টিকিট সংগ্রহ করুন (৳$grandTotal)" else "Book Online Pass (৳$grandTotal)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            TicketingTab.MY_TICKETS -> {
                if (savedTickets.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = Color(0xFFB0BEC5),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (isBengali) "কোন সংরক্ষিত টিকিট পাওয়া যায়নি" else "No saved tickets found in your wallet",
                                    fontSize = 14.sp,
                                    color = TextSecondaryLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBengali) "উপরে ভাড়া ক্যালকুলেটর থেকে টিকিট বুক করুন" else "Book a journey from Fare Calculator to save passes here",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    }
                } else {
                    items(savedTickets) { ticket ->
                        TicketPassCard(
                            ticket = ticket,
                            isBengali = isBengali,
                            onDelete = { onDeleteTicket(ticket.id) }
                        )
                    }
                }
            }

            TicketingTab.E_TICKET_PORTAL -> {
                // Official Portal Integrations and Buying Guide
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Launch, contentDescription = null, tint = BdRailGreenDark)
                                Text(
                                    text = if (isBengali) "বাংলাদেশ রেলওয়ে অফিসিয়াল ই-টিকিট পোর্টাল" else "Official BR E-Ticketing Portals",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = BdRailGreenDark
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // eticket.railway.gov.bd card
                            PortalLaunchCard(
                                title = "eticket.railway.gov.bd",
                                description = if (isBengali) "বাংলাদেশ রেলওয়ের প্রধান টিকিট সেবা পোর্টাল (Shohoz Synesis JV)" else "Main Bangladesh Railway Ticketing Portal",
                                url = "https://eticket.railway.gov.bd",
                                context = context,
                                isBengali = isBengali
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // bdtickets.com card
                            PortalLaunchCard(
                                title = "bdtickets.com",
                                description = if (isBengali) "অনলাইন টিকিট প্ল্যাটফর্ম ও বাস/লঞ্চ কানেকশন" else "Multi-modal transport ticketing platform",
                                url = "https://bdtickets.com",
                                context = context,
                                isBengali = isBengali
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFECEFF1))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Official Railway Booking Rules & Quota Times
                            Text(
                                text = if (isBengali) "📌 গুরুত্বপূর্ণ টিকিট ক্রয় নিয়মাবলী:" else "📌 Important Ticket Purchase Rules:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFE65100)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            BulletPoint(
                                if (isBengali) "পশ্চিমাঞ্চল (Western Zone): প্রতিদিন সকাল ৮:০০ টা থেকে টিকিট উন্মুক্ত হয়।"
                                else "Western Zone trains: Tickets open daily at 8:00 AM."
                            )
                            BulletPoint(
                                if (isBengali) "পূর্বাঞ্চল (Eastern Zone): প্রতিদিন দুপুর ২:০০ টা থেকে টিকিট উন্মুক্ত হয়।"
                                else "Eastern Zone trains: Tickets open daily at 2:00 PM."
                            )
                            BulletPoint(
                                if (isBengali) "এনআইডি ভেরিফিকেশন (NID Verification) ছাড়া টিকিট বুকিং সম্ভব নয়।"
                                else "NID verification is mandatory for e-ticket booking."
                            )
                            BulletPoint(
                                if (isBengali) "এক আইডি থেকে সর্বোচ্চ ৪টি টিকিট সংগ্রহ করা যায়।"
                                else "Maximum 4 tickets can be booked per NID account."
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TicketPassCard(
    ticket: SavedTicketEntity,
    isBengali: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("ticket_card_${ticket.pnr}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column {
            // Ticket Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BdRailGreenDark)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Train, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Text(
                        text = "${ticket.trainNo} - ${ticket.trainName}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Surface(
                    color = BdRailOrangeAccent,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "PNR: ${ticket.pnr}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Route & Journey Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = if (isBengali) "যাত্রা শুরুর স্টেশন" else "From Station", fontSize = 10.sp, color = TextSecondaryLight)
                        Text(text = ticket.fromStation, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BdRailGreenDark)
                    }

                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = BdRailOrangeAccent, modifier = Modifier.size(18.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = if (isBengali) "গন্তব্য স্টেশন" else "To Station", fontSize = 10.sp, color = TextSecondaryLight)
                        Text(text = ticket.toStation, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BdRailGreenDark)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(10.dp))

                // Passenger, Coach, Seat info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = if (isBengali) "যাত্রী" else "Passenger", fontSize = 10.sp, color = TextSecondaryLight)
                        Text(text = ticket.passengerName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryLight)
                    }
                    Column {
                        Text(text = if (isBengali) "বগি ও আসন" else "Coach & Seat", fontSize = 10.sp, color = TextSecondaryLight)
                        Text(text = "${ticket.coachName} - ${ticket.seatNumbers}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BdRailOrangeAccent)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = if (isBengali) "শ্রেণি ও ভাড়া" else "Class & Fare", fontSize = 10.sp, color = TextSecondaryLight)
                        Text(text = "${ticket.seatClass} (৳${ticket.totalFare})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BdRailGreenDark)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // QR Code Representation & Delete Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F8E9), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.QrCode2, contentDescription = "QR Code", tint = BdRailGreenDark, modifier = Modifier.size(22.dp))
                        Text(
                            text = if (isBengali) "টিটিই ভেরিফাইড ই-পাস" else "TTE Verified Digital Pass",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BdRailGreenDark
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondaryLight)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryLight)
    }
}

@Composable
fun PortalLaunchCard(
    title: String,
    description: String,
    url: String,
    context: android.content.Context,
    isBengali: Boolean
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF4F9F9),
        border = BorderStroke(1.dp, BdRailGreenContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BdRailGreenDark)
                Text(text = description, fontSize = 10.sp, color = TextSecondaryLight)
            }

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                colors = ButtonDefaults.buttonColors(containerColor = BdRailGreenDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = if (isBengali) "প্রবেশ করুন" else "Open", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = "•", fontSize = 12.sp, color = BdRailGreenDark)
        Text(text = text, fontSize = 11.sp, color = TextPrimaryLight)
    }
}

package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class FeedbackTab(val labelEn: String, val labelBn: String) {
    FEEDBACK_FORM("Feedback & Requests", "মতামত ও ফিচার রিকোয়েস্ট"),
    API_INFO("Public API Status", "পাবলিক এপিআই ও ট্র্যাকিং তথ্য"),
    HELPLINE("Helplines & Contacts", "জরুরি হেল্পলাইন ও সাপোর্ট")
}

data class UserFeedbackItem(
    val id: String,
    val name: String,
    val category: String,
    val rating: Int,
    val message: String,
    val timeAgo: String
)

@Composable
fun FeedbackScreen(
    isBengali: Boolean,
    onShowNotification: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(FeedbackTab.FEEDBACK_FORM) }

    // Feedback Form States
    var selectedCategory by remember { mutableStateOf(if (isBengali) "ফিচার রিকোয়েস্ট" else "Feature Request") }
    var passengerName by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var relatedTrainOrStation by remember { mutableStateOf("") }
    var ratingStars by remember { mutableIntStateOf(5) }
    var feedbackMessage by remember { mutableStateOf("") }
    var submittedSuccessfully by remember { mutableStateOf(false) }

    val recentFeedbacks = remember {
        mutableStateListOf(
            UserFeedbackItem("fb-1", "Tanvir Ahmed", "Feature Request", 5, "Please add coach interior 360 view for Snigdha & AC Berth.", "10 mins ago"),
            UserFeedbackItem("fb-2", "Nusrat Jahan", "Train Schedule", 5, "Added Chilahati Express (805) & Nilsagor stops accurately. Great app!", "1 hour ago"),
            UserFeedbackItem("fb-3", "Kamrul Islam", "Crowd Radar", 5, "GPS live speedometer matched my train speed near Ishwardi. Very helpful.", "3 hours ago")
        )
    }

    val categories = if (isBengali) {
        listOf("ফিচার রিকোয়েস্ট", "বাগ রিপোর্ট", "ট্রেন সময়সূচী সংশোধন", "স্টেশন তথ্য", "অন্যান্য")
    } else {
        listOf("Feature Request", "Bug Report", "Schedule Correction", "Station Info", "Other")
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimaryLight,
        unfocusedTextColor = TextPrimaryLight,
        disabledTextColor = TextPrimaryLight,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .testTag("screen_feedback"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header Banner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BdRailGreenDark)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Feedback,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = if (isBengali) "মহারেল সাপোর্ট ও মতামত কেন্দ্র" else "MohaRail Support & Feedback Hub",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Text(
                    text = if (isBengali) "আপনার মতামত, নতুন ফিচারের প্রস্তাবনা ও রেলওয়ে সেবা সহায়তা" else "User feedback, feature requests & BD Railway API information",
                    color = Color(0xFFB2DFDB),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FeedbackTab.values().forEach { tab ->
                        val isSelected = activeTab == tab
                        FilterChip(
                            selected = isSelected,
                            onClick = { activeTab = tab },
                            label = {
                                Text(
                                    text = if (isBengali) tab.labelBn else tab.labelEn,
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
                            border = null,
                            modifier = Modifier.testTag("tab_feedback_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }

        when (activeTab) {
            FeedbackTab.FEEDBACK_FORM -> {
                // Feature Request & Feedback Form
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isBengali) "মতামত বা নতুন ফিচারের প্রস্তাব দিন" else "Submit Feedback or Feature Request",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BdRailGreenDark
                            )
                            Text(
                                text = if (isBengali)
                                    "আপনার মতামত আমাদের অ্যাপকে আরও নিখুঁত করতে সাহায্য করে।"
                                else
                                    "Help us improve MohaRail for all Bangladesh Railway passengers.",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Category Selector
                            Text(
                                text = if (isBengali) "বিভাগ নির্বাচন করুন:" else "Select Category:",
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
                                categories.forEach { cat ->
                                    val isSel = selectedCategory == cat
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { selectedCategory = cat },
                                        label = { Text(cat, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BdRailGreenDark,
                                            selectedLabelColor = Color.White,
                                            containerColor = Color(0xFFF1F8E9),
                                            labelColor = TextPrimaryLight
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Name & Contact
                            OutlinedTextField(
                                value = passengerName,
                                onValueChange = { passengerName = it },
                                label = { Text(if (isBengali) "আপনার নাম" else "Your Name", fontSize = 11.sp) },
                                placeholder = { Text(if (isBengali) "যেমন: তানভীর আহমেদ" else "e.g. Tanvir Ahmed", fontSize = 12.sp) },
                                singleLine = true,
                                colors = textFieldColors,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("input_feedback_name")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = contactInfo,
                                    onValueChange = { contactInfo = it },
                                    label = { Text(if (isBengali) "ফোন / ইমেইল (ঐচ্ছিক)" else "Phone / Email (Optional)", fontSize = 11.sp) },
                                    placeholder = { Text("017XXXXXXXX", fontSize = 12.sp) },
                                    singleLine = true,
                                    colors = textFieldColors,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).testTag("input_feedback_contact")
                                )

                                OutlinedTextField(
                                    value = relatedTrainOrStation,
                                    onValueChange = { relatedTrainOrStation = it },
                                    label = { Text(if (isBengali) "ট্রেন / স্টেশন নং" else "Train / Station", fontSize = 11.sp) },
                                    placeholder = { Text("805 Chilahati", fontSize = 12.sp) },
                                    singleLine = true,
                                    colors = textFieldColors,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).testTag("input_feedback_train")
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Rating Stars
                            Text(
                                text = if (isBengali) "অ্যাপ অভিজ্ঞতা রেটিং:" else "App Experience Rating:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                (1..5).forEach { star ->
                                    IconButton(
                                        onClick = { ratingStars = star },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (star <= ratingStars) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Star $star",
                                            tint = if (star <= ratingStars) Color(0xFFFFB300) else Color(0xFFB0BEC5),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "$ratingStars / 5",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BdRailGreenDark
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Feedback Message Text Field
                            OutlinedTextField(
                                value = feedbackMessage,
                                onValueChange = { feedbackMessage = it },
                                label = { Text(if (isBengali) "আপনার বার্তা / ফিচারের বিস্তারিত লিখুন" else "Your Feedback / Suggestion Details", fontSize = 11.sp) },
                                placeholder = {
                                    Text(
                                        text = if (isBengali)
                                            "যেমন: চিলাহাটি এক্সপ্রেস ট্রেনের নতুন স্টপেজ যুক্ত হয়েছে, অনুগ্রহ করে হালনাগাদ করুন..."
                                        else
                                            "e.g. Please add offline timetable export or coach seating guide...",
                                        fontSize = 12.sp
                                    )
                                },
                                minLines = 3,
                                maxLines = 6,
                                colors = textFieldColors,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("input_feedback_message")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    if (feedbackMessage.isNotBlank()) {
                                        val newFb = UserFeedbackItem(
                                            id = "fb-${System.currentTimeMillis()}",
                                            name = passengerName.ifBlank { if (isBengali) "রেল যাত্রী" else "Railway Passenger" },
                                            category = selectedCategory,
                                            rating = ratingStars,
                                            message = feedbackMessage,
                                            timeAgo = if (isBengali) "এইমাত্র" else "Just now"
                                        )
                                        recentFeedbacks.add(0, newFb)
                                        submittedSuccessfully = true
                                        feedbackMessage = ""
                                        onShowNotification(if (isBengali) "ধন্যবাদ! আপনার মূল্যবান মতামত গৃহীত হয়েছে।" else "Thank you! Your feedback was received.")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BdRailGreenDark,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_submit_feedback")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBengali) "মতামত জমা দিন" else "Submit Feedback",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            AnimatedVisibility(visible = submittedSuccessfully) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BdRailGreenOnTime, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = if (isBengali) "আপনার ফিডব্যাক সফলভাবে জমা হয়েছে। ধন্যবাদ!" else "Feedback submitted successfully. Thank you!",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BdRailGreenDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Recent Community Feedbacks
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = if (isBengali) "সাম্প্রতিক যাত্রী মতামত ও প্রস্তাবনা" else "Recent Community Suggestions",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BdRailGreenDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                items(recentFeedbacks) { fb ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
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
                                            text = fb.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BdRailGreenDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = fb.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryLight
                                    )
                                }
                                Text(
                                    text = fb.timeAgo,
                                    fontSize = 10.sp,
                                    color = TextSecondaryLight
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = fb.message,
                                fontSize = 12.sp,
                                color = TextPrimaryLight
                            )
                        }
                    }
                }
            }

            FeedbackTab.API_INFO -> {
                // Bangladesh Railway Public API Status & Realtime Data Architecture
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = BdRailOrangeAccent)
                                Text(
                                    text = if (isBengali) "বাংলাদেশ রেলওয়ে পাবলিক এপিআই রিপোর্ট" else "Bangladesh Railway Official API Status",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = BdRailGreenDark
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                color = Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (isBengali) "সরকারি উন্মুক্ত (Open REST API) এর বর্তমান অবস্থা:" else "Public Open REST API Status:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFFE65100)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isBengali)
                                            "বাংলাদেশ রেলওয়ে (Ministry of Railways) বর্তমানে ডেভেলপারদের জন্য কোনো উন্মুক্ত ও পাবলিক লাইভ জিপিএস রেস্ট এপিআই (Unauthenticated Public REST API) প্রদান করে না। সরকারি টিকিটিং সিস্টেম (সহজ-সিনেসিস-ভিনসেন জেভি) সম্পূর্ণ সুরক্ষিত সেশন ও ক্যাপচা ভিত্তিক পোর্টাল (eticket.railway.gov.bd) এর মাধ্যমে পরিচালিত হয়।"
                                        else
                                            "Bangladesh Railway (BR) currently does not provide an open public developer REST API for live train tracking. The official ticketing backend is operated by Shohoz-Synesis-Vincen JV via session-secured endpoints (eticket.railway.gov.bd).",
                                        fontSize = 12.sp,
                                        color = TextPrimaryLight
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = if (isBengali) "বাংলাদেশে ট্রেন ট্র্যাকিংয়ের বাস্তবসম্মত পদ্ধতিসমূহ:" else "Viable Real-time Train Tracking Methods in BD:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BdRailGreenDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            MethodCard(
                                number = "1",
                                title = if (isBengali) "অফিসিয়াল এসএমএস সেলুলার ট্র্যাকিং (১৬৩১৮)" else "Official Cellular SMS Tracking (16318)",
                                desc = if (isBengali)
                                    "গ্রামীণফোন, বাংলালিংক, রবি ও টেলিটক থেকে 'TR <TrainNo>' লিখে ১৬৩১৮ নম্বরে পাঠালে ট্রেনের বর্তমান সেল-টাওয়ার অবস্থান ও পরবর্তী স্টেশনের এসএমএস রিপ্লাই আসে।"
                                else
                                    "Sending 'TR <Train_No>' to 16318 via GP/BL/Robi/Teletalk returns the train's cell-tower coordinates and delay info."
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            MethodCard(
                                number = "2",
                                title = if (isBengali) "মহারেল প্যাসেঞ্জার ক্রাউডসোর্সড জিপিএস রাডার" else "MohaRail Crowdsourced GPS Radar",
                                desc = if (isBengali)
                                    "ট্রেনে ভ্রমণরত যাত্রীরা যখন অনবোর্ড থেকে গতি ও অবস্থান শেয়ার করেন, তখন স্বয়ংক্রিয়ভাবে লাইভ স্পিড ও আউটার সিগনাল অবস্থান অন্যান্য সকল যাত্রীদের কাছে পৌঁছে যায়।"
                                else
                                    "Onboard passengers share real-time GPS telemetry and speed directly from moving coaches, providing sub-second speed & stoppage accuracy."
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            MethodCard(
                                number = "3",
                                title = if (isBengali) "অফিসিয়াল ই-টিকিটিং ওয়েব পোর্টাল" else "Official E-Ticketing Integration",
                                desc = if (isBengali)
                                    "বাংলাদেশ রেলওয়ের অফিসিয়াল ওয়েবসাইট eticket.railway.gov.bd এর সাথে সমন্বিত বুকিং গাইড ও আসন কোটা তথ্য।"
                                else
                                    "Direct integration with eticket.railway.gov.bd for seat reservation, quota rules and NID-verified ticket downloads."
                            )
                        }
                    }
                }
            }

            FeedbackTab.HELPLINE -> {
                // Emergency Helplines & Contacts
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isBengali) "বাংলাদেশ রেলওয়ে অফিসিয়াল হেল্পলাইন" else "Bangladesh Railway Official Helplines",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BdRailGreenDark
                            )
                            Text(
                                text = if (isBengali) "প্রয়োজনে সরাসরি কল করুন বা অফিসিয়াল পোর্টালে যোগাযোগ করুন" else "Tap to call direct helpline numbers or visit official portals",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Helpline 131
                            HelplineItem(
                                icon = Icons.Default.PhoneInTalk,
                                title = if (isBengali) "রেলওয়ে কাস্টমার কেয়ার" else "BR Customer Service",
                                number = "131",
                                desc = if (isBengali) "ট্রেন সিডিউল ও টিকিটিং সহায়তা" else "Schedule & ticket queries",
                                onClick = {
                                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:131"))
                                    try { context.startActivity(callIntent) } catch (_: Exception) {}
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // SMS 16318
                            HelplineItem(
                                icon = Icons.Default.Sms,
                                title = if (isBengali) "এসএমএস ট্রেন ট্র্যাকার" else "SMS Train Tracker",
                                number = "16318",
                                desc = if (isBengali) "TR [TrainNo] পাঠিয়ে লাইভ ট্র্যাক" else "Send TR <TrainNo> to 16318",
                                onClick = {
                                    val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:16318?body=TR 701"))
                                    try { context.startActivity(smsIntent) } catch (_: Exception) {}
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Railway Police 01711-691550 / 999
                            HelplineItem(
                                icon = Icons.Default.LocalPolice,
                                title = if (isBengali) "রেলওয়ে পুলিশ (জিআরপি) ও জাতীয় জরুরি সেবা" else "Railway Police & National Emergency",
                                number = "999",
                                desc = if (isBengali) "নিরাপত্তা ও জরুরি উদ্ধার সহায়তা" else "Railway safety & emergency rescue",
                                onClick = {
                                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:999"))
                                    try { context.startActivity(callIntent) } catch (_: Exception) {}
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // National Info 333
                            HelplineItem(
                                icon = Icons.Default.Call,
                                title = if (isBengali) "জাতীয় তথ্য বাতায়ন" else "National Info Center",
                                number = "333",
                                desc = if (isBengali) "সরকারি সকল সেবা ও তথ্য" else "Government services info",
                                onClick = {
                                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:333"))
                                    try { context.startActivity(callIntent) } catch (_: Exception) {}
                                }
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color(0xFFECEFF1))
                            Spacer(modifier = Modifier.height(14.dp))

                            // Official Web Links
                            Text(
                                text = if (isBengali) "অফিসিয়াল ওয়েবসাইট ও পোর্টালসমূহ:" else "Official Websites & Portals:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BdRailGreenDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            WebLinkButton(
                                title = if (isBengali) "রেলওয়ে ই-টিকেট পোর্টাল (eticket.railway.gov.bd)" else "Official E-Ticket Portal (eticket.railway.gov.bd)",
                                url = "https://eticket.railway.gov.bd",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://eticket.railway.gov.bd"))
                                    try { context.startActivity(intent) } catch (_: Exception) {}
                                }
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            WebLinkButton(
                                title = if (isBengali) "বাংলাদেশ রেলওয়ে ওয়েবসাইট (railway.gov.bd)" else "Bangladesh Railway Portal (railway.gov.bd)",
                                url = "https://railway.gov.bd",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://railway.gov.bd"))
                                    try { context.startActivity(intent) } catch (_: Exception) {}
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MethodCard(number: String, title: String, desc: String) {
    Surface(
        color = Color(0xFFF1F8E9),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, Color(0xFFC8E6C9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(BdRailGreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(text = number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BdRailGreenDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = desc, fontSize = 11.sp, color = TextPrimaryLight)
            }
        }
    }
}

@Composable
fun HelplineItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    number: String,
    desc: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xFFF9FBE7),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(0.5.dp, Color(0xFFDCEDC8)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BdRailGreenDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryLight)
                    Text(text = desc, fontSize = 10.sp, color = TextSecondaryLight)
                }
            }

            Surface(
                color = BdRailOrangeAccent,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = number,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun WebLinkButton(
    title: String,
    url: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BdRailGreenDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.Language, contentDescription = null, tint = BdRailGreenDark, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = title, fontSize = 11.sp, color = BdRailGreenDark, fontWeight = FontWeight.Bold)
    }
}

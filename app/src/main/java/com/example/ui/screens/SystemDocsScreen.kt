package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class DocsSection(val labelEn: String, val labelBn: String) {
    ARCHITECTURE("Architecture", "সিস্টেম আর্কিটেকচার"),
    DB_SCHEMA("Database Schema", "ডাটাবেস স্কিমা"),
    API_DOCS("API Documentation", "এপিআই ডকুমেন্টস"),
    ROADMAP("Roadmap & Ticketing", "টিকিটিং ও রোডম্যাপ")
}

@Composable
fun SystemDocsScreen(
    isBengali: Boolean
) {
    var activeSection by remember { mutableStateOf(DocsSection.ARCHITECTURE) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .testTag("screen_system_docs"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Header
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
                    Icon(Icons.Default.Architecture, contentDescription = null, tint = Color.White)
                    Text(
                        text = if (isBengali) "ট্রেন কোথায়: কারিগরি পরিকল্পনা ও এপিআই" else "Train Kothay: System Specs & API Roadmap",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Text(
                    text = if (isBengali) "লাইভ ট্র্যাকিং পাইপলাইন, ডাটাবেস আর্কিটেকচার ও টিকেটিং ইন্টিগ্রেশন স্পেক" else "Backend Tracking Pipeline, DB Schemas & E-Ticketing Integration",
                    color = Color(0xFFB2DFDB),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DocsSection.values().forEach { sec ->
                        val isSelected = activeSection == sec
                        FilterChip(
                            selected = isSelected,
                            onClick = { activeSection = sec },
                            label = {
                                Text(
                                    text = if (isBengali) sec.labelBn else sec.labelEn,
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

        when (activeSection) {
            DocsSection.ARCHITECTURE -> {
                item {
                    DocCard(title = if (isBengali) "১. হাই-লেভেল আর্কিটেকচার ও ডাটা ফ্লো" else "1. High-Level Architecture & Data Flow") {
                        Text(
                            text = if (isBengali)
                                "ট্রেন কোথায় একটি ডিস্ট্রিবিউটেড রিয়েল-টাইম জিও-লোকেশন ও কমিউনিটি ক্রাউডসোর্সিং প্ল্যাটফর্ম। এটি ৩ স্তরের সমন্বয়ে কাজ করে:"
                            else
                                "Train Kothay is an ultra-resilient distributed real-time geo-tracking platform featuring 3-tier telemetry ingestion:",
                            fontSize = 12.sp,
                            color = TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CodeBlock(
                            """
[Mobile GPS Sensors] ───┐
                         ├──► [FastAPI / Go Telemetry Gateway] ──► [Redis Geo Spatial Index]
[Crowdsourced Reports] ──┤                                                     │
                         │                                                     ▼
[BR 16318 SMS Webhook] ──┘                                           [Kafka Event Stream]
                                                                               │
[E-Ticket Gateway / Shohoz] ◄── [Spring / Ktor Engine] ◄───────────────┤
             │                                                                 ▼
             ▼                                                    [WebSocket Live Broadcaster]
   [PostgreSQL Database]                                                       │
                                                                               ▼
                                                                 [Train Kothay Android App]
                            """.trimIndent()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ArchitectureBullet(
                            "Telemetry Gateway",
                            "Handles 100k+ incoming GPS coordinates/sec from active train passengers with Kalman filtering for speed anomalies."
                        )
                        ArchitectureBullet(
                            "Redis GeoHash",
                            "Maintains real-time distance from railway mileposts and next station ETA countdowns in <2ms."
                        )
                        ArchitectureBullet(
                            "BR 16318 SMS Fallback Parser",
                            "Directly parses official SMS responses to guarantee tracking in zero-internet remote railway zones."
                        )
                    }
                }
            }

            DocsSection.DB_SCHEMA -> {
                item {
                    DocCard(title = if (isBengali) "২. সম্পূর্ণ ডাটাবেস স্কিমা (PostgreSQL / Room ERD)" else "2. Database Schema (PostgreSQL / SQLite Room)") {
                        Text(
                            text = if (isBengali) "সিস্টেমের কোর রিলেশনাল ডাটাবেস টেবিল ও রিলেশনশিপ:" else "Core Relational Schemas & Entities:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        CodeBlock(
                            """
-- 1. Trains Table
CREATE TABLE trains (
    train_no VARCHAR(10) PRIMARY KEY,
    name_en VARCHAR(100) NOT NULL,
    name_bn VARCHAR(100) NOT NULL,
    train_type VARCHAR(30) NOT NULL,
    origin_station_code VARCHAR(10) REFERENCES stations(code),
    dest_station_code VARCHAR(10) REFERENCES stations(code),
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    off_day_en VARCHAR(20),
    off_day_bn VARCHAR(20),
    total_distance_km INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Route Stoppages Table
CREATE TABLE route_stoppages (
    id SERIAL PRIMARY KEY,
    train_no VARCHAR(10) REFERENCES trains(train_no),
    station_code VARCHAR(10) REFERENCES stations(code),
    stop_sequence INT NOT NULL,
    scheduled_arrival TIME,
    scheduled_departure TIME,
    halt_minutes INT DEFAULT 0,
    distance_from_origin_km INT NOT NULL,
    platform_no VARCHAR(5) DEFAULT '1',
    UNIQUE (train_no, stop_sequence)
);

-- 3. Live Train Telemetry (High-write table)
CREATE TABLE live_train_telemetry (
    train_no VARCHAR(10) PRIMARY KEY REFERENCES trains(train_no),
    current_latitude DOUBLE PRECISION,
    current_longitude DOUBLE PRECISION,
    current_speed_kmh INT DEFAULT 0,
    delay_minutes INT DEFAULT 0,
    current_station_code VARCHAR(10),
    next_station_code VARCHAR(10),
    active_reporters_count INT DEFAULT 1,
    last_ping_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. Crowdsourced Passenger Reports
CREATE TABLE crowd_passenger_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    train_no VARCHAR(10) REFERENCES trains(train_no),
    reporter_user_id VARCHAR(50),
    coach_name VARCHAR(20),
    speed_kmh INT,
    passed_landmark TEXT,
    condition_note TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 5. Ticket Reservations & Passes
CREATE TABLE ticket_reservations (
    pnr VARCHAR(20) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    train_no VARCHAR(10) REFERENCES trains(train_no),
    from_station_code VARCHAR(10),
    to_station_code VARCHAR(10),
    journey_date DATE NOT NULL,
    seat_class VARCHAR(20) NOT NULL,
    coach_no VARCHAR(10),
    seat_numbers VARCHAR(50),
    passenger_name VARCHAR(100),
    passenger_nid VARCHAR(30),
    fare_total NUMERIC(10,2) NOT NULL,
    booking_status VARCHAR(20) DEFAULT 'CONFIRMED'
);
                            """.trimIndent()
                        )
                    }
                }
            }

            DocsSection.API_DOCS -> {
                item {
                    DocCard(title = if (isBengali) "৩. REST ও WebSocket এপিআই ডকুমেন্টেশন" else "3. REST & WebSocket API Documentation") {
                        Text(
                            text = if (isBengali) "এন্ডপয়েন্ট স্পেসিফিকেশন ও JSON রেসপন্স:" else "API Endpoints & Payloads:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ApiEndpointItem(
                            method = "GET",
                            path = "/api/v1/trains/live/{train_no}",
                            description = "Retrieve live tracking status, delay, speed, and passing station",
                            responseSample = """
{
  "train_no": "701",
  "name": "Suborno Express",
  "current_station": "Dhaka Airport",
  "next_station": "Dhaka (Kamalapur)",
  "speed_kmh": 72,
  "delay_minutes": 17,
  "status": "RUNNING",
  "active_reporters": 42,
  "last_updated": "2026-09-01T12:05:00Z"
}
                            """.trimIndent()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ApiEndpointItem(
                            method = "POST",
                            path = "/api/v1/tracking/crowd-beacon",
                            description = "Submit passenger GPS speed, coach, and track conditions",
                            responseSample = """
// Request Body:
{
  "train_no": "813",
  "coach": "Cha (চ)",
  "speed_kmh": 78,
  "station_passed": "Feni Junction",
  "note": "Crossing completed, clear green signal"
}
// Response: 201 Created
{ "status": "success", "report_id": "cr-98213", "reputation_points_awarded": 10 }
                            """.trimIndent()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ApiEndpointItem(
                            method = "WS",
                            path = "/ws/v1/trains/{train_no}/stream",
                            description = "Real-time bidirectional WebSocket train position stream (1-sec interval)",
                            responseSample = """
// WebSocket Stream Frame:
{
  "type": "LOCATION_UPDATE",
  "lat": 23.8512,
  "lng": 90.4074,
  "speed": 74,
  "delay": 17,
  "next_station_eta_seconds": 780
}
                            """.trimIndent()
                        )
                    }
                }
            }

            DocsSection.ROADMAP -> {
                item {
                    DocCard(title = if (isBengali) "৪. সিমলেস টিকেটিং ও ফিচার রোডম্যাপ" else "4. Seamless E-Ticketing Integration & Roadmap") {
                        Text(
                            text = if (isBengali) "পরবর্তী পর্বের বাস্তবায়ন রোডম্যাপ:" else "Implementation Roadmap & Integration Milestones:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        RoadmapMilestone(
                            phase = "Phase 1 (Current)",
                            title = "Real-time Tracking & Local Persistence",
                            detail = "Room DB caching, Live GPS crowdsourced speed beacon, BR 16318 SMS fallback, Interactive journey timetable, and digital station board."
                        )
                        RoadmapMilestone(
                            phase = "Phase 2 (Immediate Next)",
                            title = "Seamless E-Ticketing OAuth & Instant Sync",
                            detail = "Direct Shohoz/Railway.gov.bd account linking via SSO, auto-fetching booked PNR tickets with digital QR gate-pass verification."
                        )
                        RoadmapMilestone(
                            phase = "Phase 3",
                            title = "On-board Train Food & Station Porter Booking",
                            detail = "Station-specific catering delivery to your coach seat (e.g. Akhaura Biryani, Sreemangal tea) and licensed porter booking at Kamalapur/Chattogram."
                        )
                        RoadmapMilestone(
                            phase = "Phase 4",
                            title = "Mesh Offline BLE Sharing & Predictive Delay AI",
                            detail = "Peer-to-peer Bluetooth mesh networking across train coaches to propagate location pings in dead-zones without cellular signal."
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DocCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BdRailGreenDark
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun CodeBlock(code: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E293B),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = code,
            color = Color(0xFF38BDF8),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.5.sp,
            lineHeight = 15.sp,
            modifier = Modifier
                .padding(12.dp)
                .horizontalScroll(rememberScrollState())
        )
    }
}

@Composable
fun ArchitectureBullet(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = "✔", fontSize = 11.sp, color = BdRailGreenPrimary, fontWeight = FontWeight.Bold)
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
            Text(text = desc, fontSize = 11.sp, color = TextSecondaryLight)
        }
    }
}

@Composable
fun ApiEndpointItem(method: String, path: String, description: String, responseSample: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = when (method) {
                        "GET" -> BdRailGreenPrimary
                        "POST" -> BdRailOrangeAccent
                        "WS" -> Color(0xFF7C3AED)
                        else -> Color.Gray
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = method,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = path,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, fontSize = 11.sp, color = TextSecondaryLight)
            Spacer(modifier = Modifier.height(6.dp))
            CodeBlock(responseSample)
        }
    }
}

@Composable
fun RoadmapMilestone(phase: String, title: String, detail: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF1F8E9),
        border = BorderStroke(1.dp, BdRailGreenContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = phase, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BdRailGreenDark)
                Text(text = "Target 2026", fontSize = 10.sp, color = TextSecondaryLight)
            }
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryLight)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = detail, fontSize = 11.sp, color = TextSecondaryLight)
        }
    }
}

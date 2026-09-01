package com.example.data.repository

import com.example.data.local.dao.TrainAppDao
import com.example.data.local.entity.SavedTicketEntity
import com.example.data.local.entity.SearchHistoryEntity
import com.example.data.local.entity.TrainAlarmEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TrainRepository(private val dao: TrainAppDao) {

    // All major Bangladesh Railway Stations
    val allStations = listOf(
        Station("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "Dhaka", "Dhaka", true, "01711691550"),
        Station("DAA", "Dhaka (Biman Bandar)", "ঢাকা (বিমানবন্দর)", "Dhaka", "Dhaka", true, "01711691551"),
        Station("CTG", "Chattogram", "চট্টগ্রাম", "Chattogram", "Chattogram", true, "01711691600"),
        Station("SYL", "Sylhet", "সিলেট", "Sylhet", "Sylhet", true, "01711691700"),
        Station("RAJ", "Rajshahi", "রাজশাহী", "Rajshahi", "Rajshahi", true, "01711691800"),
        Station("CXB", "Cox's Bazar", "কক্সবাজার", "Cox's Bazar", "Chattogram", true, "01711691900"),
        Station("KHL", "Khulna", "খুলনা", "Khulna", "Khulna", true, "01711691400"),
        Station("MYM", "Mymensingh", "ময়মনসিংহ", "Mymensingh", "Mymensingh", true, "01711691300"),
        Station("RNP", "Rangpur", "রংপুর", "Rangpur", "Rangpur", true, "01711691200"),
        Station("BOG", "Bogura", "বগুড়া", "Bogura", "Rajshahi", true, "01711691100"),
        Station("BBA", "Brahmanbaria", "ব্রাহ্মণবাড়িয়া", "Brahmanbaria", "Chattogram", true, "01711691250"),
        Station("CUM", "Cumilla", "কুমিল্লা", "Cumilla", "Chattogram", true, "01711691350"),
        Station("FENI", "Feni", "ফেনী", "Feni", "Chattogram", true, "01711691450"),
        Station("SRM", "Sreemangal", "শ্রীমঙ্গল", "Moulvibazar", "Sylhet", true, "01711691520"),
        Station("ISD", "Ishwardi", "ঈশ্বরদী", "Pabna", "Rajshahi", true, "01711691620"),
        Station("NAT", "Natore", "নাটোর", "Natore", "Rajshahi", true, "01711691720"),
        Station("SAN", "Santahar", "সান্তাহার", "Bogura", "Rajshahi", true, "01711691820"),
        Station("BPO", "Benapole", "বেনাপোল", "Jashore", "Khulna", true, "01711691920")
    )

    // Complete collection of iconic BD Railway trains
    val allTrains = listOf(
        Train(
            trainNo = "701",
            nameEn = "Suborno Express",
            nameBn = "সুবর্ণ এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "CTG",
            originStationNameEn = "Chattogram",
            originStationNameBn = "চট্টগ্রাম",
            destStationCode = "DA",
            destStationNameEn = "Dhaka (Kamalapur)",
            destStationNameBn = "ঢাকা (কমলাপুর)",
            departureTime = "07:00 AM",
            arrivalTime = "12:15 PM",
            offDayEn = "Monday",
            offDayBn = "সোমবার",
            totalDistanceKm = 320,
            baseFareSChair = 405,
            baseFareSnigdha = 777,
            baseFareAcSeat = 932,
            baseFareAcBerth = 1398,
            baseFareShovon = 340,
            routeStops = listOf(
                RouteStop("CTG", "Chattogram", "চট্টগ্রাম", "06:30 AM", "07:00 AM", 30, 0, "1", "07:00 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "11:45 AM", "11:50 AM", 5, 305, "2", "12:02 PM", StopStatus.CURRENT),
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "12:15 PM", "12:15 PM", 0, 320, "4", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "702",
            nameEn = "Suborno Express",
            nameBn = "সুবর্ণ এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "CTG",
            destStationNameEn = "Chattogram",
            destStationNameBn = "চট্টগ্রাম",
            departureTime = "04:30 PM",
            arrivalTime = "09:50 PM",
            offDayEn = "Monday",
            offDayBn = "সোমবার",
            totalDistanceKm = 320,
            baseFareSChair = 405,
            baseFareSnigdha = 777,
            baseFareAcSeat = 932,
            baseFareAcBerth = 1398,
            baseFareShovon = 340,
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "04:00 PM", "04:30 PM", 30, 0, "4", "04:30 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "04:55 PM", "05:00 PM", 5, 15, "1", "05:05 PM", StopStatus.PASSED),
                RouteStop("CTG", "Chattogram", "চট্টগ্রাম", "09:50 PM", "09:50 PM", 0, 320, "2", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "813",
            nameEn = "Cox's Bazar Express",
            nameBn = "কক্সবাজার এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "CXB",
            originStationNameEn = "Cox's Bazar",
            originStationNameBn = "কক্সবাজার",
            destStationCode = "DA",
            destStationNameEn = "Dhaka (Kamalapur)",
            destStationNameBn = "ঢাকা (কমলাপুর)",
            departureTime = "12:30 PM",
            arrivalTime = "09:10 PM",
            offDayEn = "Tuesday",
            offDayBn = "মঙ্গলবার",
            totalDistanceKm = 480,
            baseFareSChair = 695,
            baseFareSnigdha = 1325,
            baseFareAcSeat = 1590,
            baseFareAcBerth = 2380,
            baseFareShovon = 550,
            routeStops = listOf(
                RouteStop("CXB", "Cox's Bazar", "কক্সবাজার", "12:00 PM", "12:30 PM", 30, 0, "1", "12:30 PM", StopStatus.PASSED),
                RouteStop("CTG", "Chattogram", "চট্টগ্রাম", "03:40 PM", "04:00 PM", 20, 160, "3", "04:12 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "08:40 PM", "08:45 PM", 5, 465, "2", "09:00 PM", StopStatus.CURRENT),
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "09:10 PM", "09:10 PM", 0, 480, "5", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "814",
            nameEn = "Cox's Bazar Express",
            nameBn = "কক্সবাজার এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "CXB",
            destStationNameEn = "Cox's Bazar",
            destStationNameBn = "কক্সবাজার",
            departureTime = "10:30 PM",
            arrivalTime = "06:40 AM",
            offDayEn = "Monday",
            offDayBn = "সোমবার",
            totalDistanceKm = 480,
            baseFareSChair = 695,
            baseFareSnigdha = 1325,
            baseFareAcSeat = 1590,
            baseFareAcBerth = 2380,
            baseFareShovon = 550,
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "10:00 PM", "10:30 PM", 30, 0, "3", "10:30 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "10:55 PM", "11:00 PM", 5, 15, "1", "11:02 PM", StopStatus.PASSED),
                RouteStop("CTG", "Chattogram", "চট্টগ্রাম", "03:40 AM", "04:00 AM", 20, 320, "2", "04:05 AM", StopStatus.PASSED),
                RouteStop("CXB", "Cox's Bazar", "কক্সবাজার", "06:40 AM", "06:40 AM", 0, 480, "1", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "709",
            nameEn = "Parabat Express",
            nameBn = "পারাবত এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "SYL",
            destStationNameEn = "Sylhet",
            destStationNameBn = "সিলেট",
            departureTime = "06:20 AM",
            arrivalTime = "01:00 PM",
            offDayEn = "Tuesday",
            offDayBn = "মঙ্গলবার",
            totalDistanceKm = 319,
            baseFareSChair = 395,
            baseFareSnigdha = 759,
            baseFareAcSeat = 910,
            baseFareAcBerth = 1365,
            baseFareShovon = 320,
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "05:50 AM", "06:20 AM", 30, 0, "6", "06:20 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "06:45 AM", "06:50 AM", 5, 15, "1", "06:50 AM", StopStatus.PASSED),
                RouteStop("BBA", "Brahmanbaria", "ব্রাহ্মণবাড়িয়া", "08:45 AM", "08:50 AM", 5, 115, "2", "09:05 AM", StopStatus.PASSED),
                RouteStop("SRM", "Sreemangal", "শ্রীমঙ্গল", "11:15 AM", "11:20 AM", 5, 230, "1", "11:38 AM", StopStatus.CURRENT),
                RouteStop("SYL", "Sylhet", "সিলেট", "01:00 PM", "01:00 PM", 0, 319, "2", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "753",
            nameEn = "Silk City Express",
            nameBn = "সিল্কসিটি এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "RAJ",
            destStationNameEn = "Rajshahi",
            destStationNameBn = "রাজশাহী",
            departureTime = "02:45 PM",
            arrivalTime = "08:35 PM",
            offDayEn = "Sunday",
            offDayBn = "রবিবার",
            totalDistanceKm = 343,
            baseFareSChair = 405,
            baseFareSnigdha = 777,
            baseFareAcSeat = 932,
            baseFareAcBerth = 1398,
            baseFareShovon = 340,
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "02:15 PM", "02:45 PM", 30, 0, "7", "02:45 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "03:10 PM", "03:15 PM", 5, 15, "1", "03:15 PM", StopStatus.PASSED),
                RouteStop("ISD", "Ishwardi", "ঈশ্বরদী", "06:40 PM", "06:45 PM", 5, 260, "2", "06:55 PM", StopStatus.PASSED),
                RouteStop("NAT", "Natore", "নাটোর", "07:30 PM", "07:35 PM", 5, 295, "1", "07:48 PM", StopStatus.CURRENT),
                RouteStop("RAJ", "Rajshahi", "রাজশাহী", "08:35 PM", "08:35 PM", 0, 343, "3", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "725",
            nameEn = "Sundarban Express",
            nameBn = "সুন্দরবন এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "KHL",
            destStationNameEn = "Khulna",
            destStationNameBn = "খুলনা",
            departureTime = "08:15 AM",
            arrivalTime = "03:50 PM",
            offDayEn = "Wednesday",
            offDayBn = "বুধবার",
            totalDistanceKm = 412,
            baseFareSChair = 505,
            baseFareSnigdha = 970,
            baseFareAcSeat = 1160,
            baseFareAcBerth = 1740,
            baseFareShovon = 420,
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "07:45 AM", "08:15 AM", 30, 0, "2", "08:15 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "08:40 AM", "08:45 AM", 5, 15, "1", "08:45 AM", StopStatus.PASSED),
                RouteStop("ISD", "Ishwardi", "ঈশ্বরদী", "12:30 PM", "12:35 PM", 5, 260, "2", "12:40 PM", StopStatus.PASSED),
                RouteStop("KHL", "Khulna", "খুলনা", "03:50 PM", "03:50 PM", 0, 412, "1", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "771",
            nameEn = "Rangpur Express",
            nameBn = "রংপুর এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "RNP",
            destStationNameEn = "Rangpur",
            destStationNameBn = "রংপুর",
            departureTime = "09:10 AM",
            arrivalTime = "07:05 PM",
            offDayEn = "Sunday",
            offDayBn = "রবিবার",
            totalDistanceKm = 445,
            baseFareSChair = 550,
            baseFareSnigdha = 1050,
            baseFareAcSeat = 1260,
            baseFareAcBerth = 1890,
            baseFareShovon = 450,
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "08:40 AM", "09:10 AM", 30, 0, "5", "09:10 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "09:35 AM", "09:40 AM", 5, 15, "1", "09:45 AM", StopStatus.PASSED),
                RouteStop("SAN", "Santahar", "সান্তাহার", "03:30 PM", "03:40 PM", 10, 310, "3", "04:02 PM", StopStatus.PASSED),
                RouteStop("BOG", "Bogura", "বগুড়া", "04:30 PM", "04:35 PM", 5, 350, "1", "05:05 PM", StopStatus.CURRENT),
                RouteStop("RNP", "Rangpur", "রংপুর", "07:05 PM", "07:05 PM", 0, 445, "2", null, StopStatus.UPCOMING)
            )
        )
    )

    // Crowd reports state flow
    private val _crowdReports = MutableStateFlow(
        listOf(
            CrowdLocationReport("cr-1", "701", "Tanvir Ahmed", "Cha (চ)", "Approaching Tongi outer loop", 68, "Track is clear, AC working well", "2 mins ago"),
            CrowdLocationReport("cr-2", "813", "Sabbir Hossain", "Kha (খ)", "Crossed Feni Junction", 74, "Speed picked up after signal clear", "5 mins ago"),
            CrowdLocationReport("cr-3", "709", "Rahim Uddin", "Ga (গ)", "At Sreemangal platform 1", 0, "Halt extended by 4 mins for crossing", "8 mins ago"),
            CrowdLocationReport("cr-4", "753", "Nazmul Hasan", "Gha (ঘ)", "Near Natore bypass", 78, "Smooth ride, water available in coach", "12 mins ago")
        )
    )
    val crowdReports = _crowdReports.asStateFlow()

    fun getTrainByNo(trainNo: String): Train? {
        return allTrains.find { it.trainNo.equals(trainNo.trim(), ignoreCase = true) }
            ?: allTrains.find { it.nameEn.contains(trainNo.trim(), ignoreCase = true) || it.nameBn.contains(trainNo.trim()) }
    }

    fun getLiveStatusForTrain(trainNo: String): LiveTrainStatus {
        val train = getTrainByNo(trainNo) ?: allTrains.first()
        val stops = train.routeStops

        // Dynamic status based on train
        val delay = when (train.trainNo) {
            "701" -> 17
            "813" -> 20
            "709" -> 23
            "753" -> 13
            "771" -> 35
            else -> 8
        }
        val speed = when (train.trainNo) {
            "701" -> 72
            "813" -> 78
            "709" -> 45
            "753" -> 64
            else -> 60
        }

        val currentIndex = stops.indexOfFirst { it.status == StopStatus.CURRENT }.let { if (it >= 0) it else 1 }
        val currentStop = stops.getOrNull(currentIndex) ?: stops.first()
        val nextStop = stops.getOrNull(currentIndex + 1) ?: stops.last()
        val progress = ((currentIndex + 0.5f) / stops.size.toFloat()).coerceIn(0.1f, 0.95f)

        return LiveTrainStatus(
            trainNo = train.trainNo,
            trainNameEn = train.nameEn,
            trainNameBn = train.nameBn,
            currentStationEn = currentStop.stationNameEn,
            currentStationBn = currentStop.stationNameBn,
            nextStationEn = nextStop.stationNameEn,
            nextStationBn = nextStop.stationNameBn,
            currentSpeedKmh = speed,
            delayMinutes = delay,
            lastUpdatedTextEn = "Just now via Live GPS & Crowd Radar",
            lastUpdatedTextBn = "এইমাত্র লাইভ জিপিএস ও ক্রাউড রাডার হতে হালনাগাদকৃত",
            currentStopIndex = currentIndex,
            progressPercent = progress,
            activeCrowdReportersCount = 42,
            liveGpsActive = true,
            stopsWithLiveStatus = stops
        )
    }

    fun searchTrains(originCode: String, destCode: String): List<Train> {
        val direct = allTrains.filter { train ->
            val originIdx = train.routeStops.indexOfFirst { it.stationCode == originCode }
            val destIdx = train.routeStops.indexOfFirst { it.stationCode == destCode }
            originIdx != -1 && destIdx != -1 && originIdx < destIdx
        }
        if (direct.isNotEmpty()) return direct

        // Match origin or destination fallback
        return allTrains.filter { train ->
            train.originStationCode == originCode ||
                    train.destStationCode == destCode ||
                    train.routeStops.any { it.stationCode == originCode || it.stationCode == destCode }
        }.ifEmpty { allTrains.take(3) }
    }

    fun getStationBoard(stationCode: String): List<StationArrivalDeparture> {
        val stn = allStations.find { it.code == stationCode } ?: allStations.first()
        val list = mutableListOf<StationArrivalDeparture>()

        allTrains.forEach { tr ->
            if (tr.originStationCode == stn.code) {
                list.add(
                    StationArrivalDeparture(
                        trainNo = tr.trainNo,
                        trainNameEn = tr.nameEn,
                        trainNameBn = tr.nameBn,
                        type = tr.type,
                        originOrDest = "To: ${tr.destStationNameEn} (${tr.destStationNameBn})",
                        scheduledTime = tr.departureTime,
                        expectedTime = tr.departureTime,
                        delayMinutes = 0,
                        platform = "1",
                        isArrival = false
                    )
                )
            } else if (tr.destStationCode == stn.code) {
                list.add(
                    StationArrivalDeparture(
                        trainNo = tr.trainNo,
                        trainNameEn = tr.nameEn,
                        trainNameBn = tr.nameBn,
                        type = tr.type,
                        originOrDest = "From: ${tr.originStationNameEn} (${tr.originStationNameBn})",
                        scheduledTime = tr.arrivalTime,
                        expectedTime = tr.arrivalTime,
                        delayMinutes = 15,
                        platform = "2",
                        isArrival = true
                    )
                )
            } else if (tr.routeStops.any { it.stationCode == stn.code }) {
                val stop = tr.routeStops.first { it.stationCode == stn.code }
                list.add(
                    StationArrivalDeparture(
                        trainNo = tr.trainNo,
                        trainNameEn = tr.nameEn,
                        trainNameBn = tr.nameBn,
                        type = tr.type,
                        originOrDest = "${tr.originStationNameEn} -> ${tr.destStationNameEn}",
                        scheduledTime = stop.scheduledArrival,
                        expectedTime = stop.actualArrivalTime ?: stop.scheduledArrival,
                        delayMinutes = 12,
                        platform = stop.platform,
                        isArrival = true
                    )
                )
            }
        }
        return list.ifEmpty {
            listOf(
                StationArrivalDeparture(
                    "701", "Suborno Express", "সুবর্ণ এক্সপ্রেস", TrainType.INTERCITY,
                    "Chattogram -> Dhaka", "11:45 AM", "12:02 PM", 17, "2", true
                ),
                StationArrivalDeparture(
                    "813", "Cox's Bazar Express", "কক্সবাজার এক্সপ্রেস", TrainType.INTERCITY,
                    "Cox's Bazar -> Dhaka", "08:40 PM", "09:00 PM", 20, "1", true
                )
            )
        }
    }

    fun submitCrowdReport(trainNo: String, reporter: String, coach: String, note: String, speed: Int, passedStation: String) {
        val newReport = CrowdLocationReport(
            id = "cr-${UUID.randomUUID()}",
            trainNo = trainNo,
            reporterName = reporter.ifBlank { "BR Passenger" },
            coachName = coach.ifBlank { "Coach Ga" },
            passedStation = passedStation.ifBlank { "Outer Signal" },
            currentSpeedKmh = speed,
            conditionNote = note.ifBlank { "Train running steadily on main track." },
            timeAgo = "Just now"
        )
        val current = _crowdReports.value.toMutableList()
        current.add(0, newReport)
        _crowdReports.value = current
    }

    // Room Database bindings
    fun getSavedTickets(): Flow<List<SavedTicketEntity>> = dao.getAllTickets()
    suspend fun saveTicket(ticket: SavedTicketEntity) = dao.insertTicket(ticket)
    suspend fun deleteTicket(id: Long) = dao.deleteTicket(id)

    fun getSearchHistory(): Flow<List<SearchHistoryEntity>> = dao.getRecentSearches()
    suspend fun saveSearchHistory(search: SearchHistoryEntity) = dao.insertSearch(search)

    fun getAllAlarms(): Flow<List<TrainAlarmEntity>> = dao.getAllAlarms()
    suspend fun addAlarm(alarm: TrainAlarmEntity) = dao.insertAlarm(alarm)
    suspend fun toggleAlarm(id: Long, isEnabled: Boolean) = dao.updateAlarmStatus(id, isEnabled)
    suspend fun deleteAlarm(id: Long) = dao.deleteAlarm(id)
}

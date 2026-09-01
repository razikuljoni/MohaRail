package com.example.data.model

data class Station(
    val code: String,
    val nameEn: String,
    val nameBn: String,
    val district: String,
    val division: String,
    val hasFoodFacility: Boolean = true,
    val emergencyPhone: String = "01711691234"
)

data class RouteStop(
    val stationCode: String,
    val stationNameEn: String,
    val stationNameBn: String,
    val scheduledArrival: String,
    val scheduledDeparture: String,
    val haltMinutes: Int,
    val distanceFromOriginKm: Int,
    val platform: String = "1",
    val actualArrivalTime: String? = null,
    val status: StopStatus = StopStatus.SCHEDULED
)

enum class StopStatus {
    PASSED,
    CURRENT,
    UPCOMING,
    SCHEDULED
}

enum class TrainType(val labelEn: String, val labelBn: String) {
    INTERCITY("Intercity Express", "আন্তঃনগর এক্সপ্রেস"),
    MAIL_EXPRESS("Mail / Express", "মেইল / এক্সপ্রেস"),
    COMMUTER("Commuter", "কমিউটার"),
    SPECIAL("Tourist / Special", "ট্যুরিস্ট / স্পেশাল")
}

data class Train(
    val trainNo: String,
    val nameEn: String,
    val nameBn: String,
    val type: TrainType,
    val originStationCode: String,
    val originStationNameEn: String,
    val originStationNameBn: String,
    val destStationCode: String,
    val destStationNameEn: String,
    val destStationNameBn: String,
    val departureTime: String,
    val arrivalTime: String,
    val offDayEn: String,
    val offDayBn: String,
    val totalDistanceKm: Int,
    val routeStops: List<RouteStop>,
    val baseFareSChair: Int,
    val baseFareSnigdha: Int,
    val baseFareAcSeat: Int,
    val baseFareAcBerth: Int,
    val baseFareShovon: Int
)

data class LiveTrainStatus(
    val trainNo: String,
    val trainNameEn: String,
    val trainNameBn: String,
    val currentStationEn: String,
    val currentStationBn: String,
    val nextStationEn: String,
    val nextStationBn: String,
    val currentSpeedKmh: Int,
    val delayMinutes: Int,
    val lastUpdatedTextEn: String,
    val lastUpdatedTextBn: String,
    val currentStopIndex: Int,
    val progressPercent: Float,
    val activeCrowdReportersCount: Int,
    val liveGpsActive: Boolean,
    val stopsWithLiveStatus: List<RouteStop>
)

data class StationArrivalDeparture(
    val trainNo: String,
    val trainNameEn: String,
    val trainNameBn: String,
    val type: TrainType,
    val originOrDest: String,
    val scheduledTime: String,
    val expectedTime: String,
    val delayMinutes: Int,
    val platform: String,
    val isArrival: Boolean
)

data class CrowdLocationReport(
    val id: String,
    val trainNo: String,
    val reporterName: String,
    val coachName: String,
    val passedStation: String,
    val currentSpeedKmh: Int,
    val conditionNote: String,
    val timeAgo: String
)

enum class SeatClass(val code: String, val nameEn: String, val nameBn: String) {
    SHOVON("SHOVON", "Shovon", "শোভন"),
    S_CHAIR("S_CHAIR", "Shovon Chair", "শোভন চেয়ার"),
    SNIGDHA("SNIGDHA", "Snigdha (AC)", "স্নিগ্ধা (এসি)"),
    AC_S("AC_S", "AC Seat", "এসি সিট"),
    AC_B("AC_B", "AC Berth", "এসি বার্থ"),
    F_BERTH("F_BERTH", "First Class Berth", "ফার্স্ট ক্লাস বার্থ")
}

data class TicketAvailability(
    val seatClass: SeatClass,
    val availableCount: Int,
    val fareAmount: Int,
    val vatAmount: Int,
    val serviceFee: Int = 20
) {
    val totalFare: Int get() = fareAmount + vatAmount + serviceFee
}

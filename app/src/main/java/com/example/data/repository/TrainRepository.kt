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

    private fun generateCoaches(hasSnigdha: Boolean = true, hasAcBerth: Boolean = false, isAllAc: Boolean = false): List<CoachInfo> {
        val list = mutableListOf<CoachInfo>()
        if (isAllAc) {
            list.add(CoachInfo("Ka", "ক", SeatClass.SNIGDHA, 55, isAc = true))
            list.add(CoachInfo("Kha", "খ", SeatClass.SNIGDHA, 55, isAc = true))
            list.add(CoachInfo("Ga", "গ", SeatClass.SNIGDHA, 50, isAc = true, isPantry = true))
            list.add(CoachInfo("Gha", "ঘ", SeatClass.SNIGDHA, 55, isAc = true))
            list.add(CoachInfo("Uma", "ঙ", SeatClass.SNIGDHA, 55, isAc = true))
            list.add(CoachInfo("Cha", "চ", SeatClass.AC_S, 48, isAc = true))
            list.add(CoachInfo("Chha", "ছ", SeatClass.AC_S, 48, isAc = true))
            list.add(CoachInfo("Ja", "জ", SeatClass.AC_B, 36, isAc = true))
            list.add(CoachInfo("Jha", "ঝ", SeatClass.AC_B, 36, isAc = true))
            list.add(CoachInfo("Ta", "ট", SeatClass.SNIGDHA, 55, isAc = true))
        } else {
            list.add(CoachInfo("Ka", "ক", SeatClass.S_CHAIR, 60, isAc = false))
            list.add(CoachInfo("Kha", "খ", SeatClass.S_CHAIR, 60, isAc = false))
            list.add(CoachInfo("Ga", "গ", SeatClass.S_CHAIR, 45, isAc = false, isPantry = true))
            list.add(CoachInfo("Gha", "ঘ", SeatClass.S_CHAIR, 60, isAc = false))
            list.add(CoachInfo("Uma", "ঙ", SeatClass.S_CHAIR, 60, isAc = false))
            if (hasSnigdha) {
                list.add(CoachInfo("Cha", "চ", SeatClass.SNIGDHA, 55, isAc = true))
                list.add(CoachInfo("Chha", "ছ", SeatClass.SNIGDHA, 55, isAc = true))
                list.add(CoachInfo("Ja", "জ", SeatClass.AC_S, 48, isAc = true))
            }
            if (hasAcBerth) {
                list.add(CoachInfo("Jha", "ঝ", SeatClass.AC_B, 36, isAc = true))
            }
            list.add(CoachInfo("Ta", "ট", SeatClass.S_CHAIR, 60, isAc = false))
            list.add(CoachInfo("Tha", "ঠ", SeatClass.S_CHAIR, 60, isAc = false))
            list.add(CoachInfo("Da", "ড", SeatClass.S_CHAIR, 60, isAc = false))
        }
        return list
    }

    // Comprehensive Bangladesh Railway Stations (All major railway zones and junction stations)
    val allStations = listOf(
        Station("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "Dhaka", "Dhaka", true, "01711691550"),
        Station("DAA", "Dhaka (Biman Bandar)", "ঢাকা (বিমানবন্দর)", "Dhaka", "Dhaka", true, "01711691551"),
        Station("DAC", "Dhaka Cantonment", "ঢাকা ক্যান্টনমেন্ট", "Dhaka", "Dhaka", true, "01711691552"),
        Station("CTG", "Chattogram", "চট্টগ্রাম", "Chattogram", "Chattogram", true, "01711691600"),
        Station("CXB", "Cox's Bazar", "কক্সবাজার", "Cox's Bazar", "Chattogram", true, "01711691900"),
        Station("SYL", "Sylhet", "সিলেট", "Sylhet", "Sylhet", true, "01711691700"),
        Station("RAJ", "Rajshahi", "রাজশাহী", "Rajshahi", "Rajshahi", true, "01711691800"),
        Station("KHL", "Khulna", "খুলনা", "Khulna", "Khulna", true, "01711691400"),
        Station("MYM", "Mymensingh", "ময়মনসিংহ", "Mymensingh", "Mymensingh", true, "01711691300"),
        Station("RNP", "Rangpur", "রংপুর", "Rangpur", "Rangpur", true, "01711691200"),
        Station("BOG", "Bogura", "বগুড়া", "Bogura", "Rajshahi", true, "01711691100"),
        Station("DIN", "Dinajpur", "দিনাজপুর", "Dinajpur", "Rangpur", true, "01711691220"),
        Station("PAN", "Panchagarh (Sirajul Islam)", "পঞ্চগড় (সিরাজুল ইসলাম)", "Panchagarh", "Rangpur", true, "01711691230"),
        Station("KUR", "Kurigram", "কুড়িগ্রাম", "Kurigram", "Rangpur", true, "01711691240"),
        Station("BPO", "Benapole", "বেনাপোল", "Jashore", "Khulna", true, "01711691920"),
        Station("JAS", "Jashore", "যশোর", "Jashore", "Khulna", true, "01711691930"),
        Station("BBA", "Brahmanbaria", "ব্রাহ্মণবাড়িয়া", "Brahmanbaria", "Chattogram", true, "01711691250"),
        Station("CUM", "Cumilla", "কুমিল্লা", "Cumilla", "Chattogram", true, "01711691350"),
        Station("FENI", "Feni", "ফেনী", "Feni", "Chattogram", true, "01711691450"),
        Station("SRM", "Sreemangal", "শ্রীমঙ্গল", "Moulvibazar", "Sylhet", true, "01711691520"),
        Station("ISD", "Ishwardi", "ঈশ্বরদী", "Pabna", "Rajshahi", true, "01711691620"),
        Station("NAT", "Natore", "নাটোর", "Natore", "Rajshahi", true, "01711691720"),
        Station("SAN", "Santahar", "সান্তাহার", "Bogura", "Rajshahi", true, "01711691820"),
        Station("BHB", "Bhairab Bazar", "ভৈরব বাজার", "Kishoreganj", "Dhaka", true, "01711691560"),
        Station("AKH", "Akhaura", "আখাউড়া", "Brahmanbaria", "Chattogram", true, "01711691570"),
        Station("JAM", "Jamalpur", "জামালপুর", "Jamalpur", "Mymensingh", true, "01711691310"),
        Station("KIS", "Kishoreganj", "কিশোরগঞ্জ", "Kishoreganj", "Dhaka", true, "01711691320"),
        Station("NOA", "Noakhali", "নোয়াখালী", "Noakhali", "Chattogram", true, "01711691460"),
        Station("CHA", "Chandpur", "চাঁদপুর", "Chandpur", "Chattogram", true, "01711691470"),
        Station("TNG", "Tangail", "টাঙ্গাইল", "Tangail", "Dhaka", true, "01711691580"),
        Station("SRJ", "Sirajganj (M. Monsur Ali)", "সিরাজগঞ্জ (এম মনসুর আলী)", "Sirajganj", "Rajshahi", true, "01711691590"),
        Station("BUR", "Burimari", "বুড়িমারী", "Lalmonirhat", "Rangpur", true, "01711691260")
    )

    // Complete collection of authentic Bangladesh Railway trains
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
            totalCoaches = 16,
            hasDiningCar = true,
            rakeType = "Indonesian Stainless Steel Air-Brake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = false),
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
            totalCoaches = 16,
            hasDiningCar = true,
            rakeType = "Indonesian Stainless Steel Air-Brake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = false),
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
            totalCoaches = 18,
            hasDiningCar = true,
            rakeType = "Korean Hyundai Rotem Deluxe Express",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
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
            totalCoaches = 18,
            hasDiningCar = true,
            rakeType = "Korean Hyundai Rotem Deluxe Express",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "10:00 PM", "10:30 PM", 30, 0, "3", "10:30 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "10:55 PM", "11:00 PM", 5, 15, "1", "11:02 PM", StopStatus.PASSED),
                RouteStop("CTG", "Chattogram", "চট্টগ্রাম", "03:40 AM", "04:00 AM", 20, 320, "2", "04:05 AM", StopStatus.PASSED),
                RouteStop("CXB", "Cox's Bazar", "কক্সবাজার", "06:40 AM", "06:40 AM", 0, 480, "1", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "815",
            nameEn = "Parjotok Express",
            nameBn = "পর্যটক এক্সপ্রেস",
            type = TrainType.SPECIAL,
            originStationCode = "CXB",
            originStationNameEn = "Cox's Bazar",
            originStationNameBn = "কক্সবাজার",
            destStationCode = "DA",
            destStationNameEn = "Dhaka (Kamalapur)",
            destStationNameBn = "ঢাকা (কমলাপুর)",
            departureTime = "08:00 PM",
            arrivalTime = "04:30 AM",
            offDayEn = "Sunday",
            offDayBn = "রবিবার",
            totalDistanceKm = 480,
            baseFareSChair = 695,
            baseFareSnigdha = 1325,
            baseFareAcSeat = 1590,
            baseFareAcBerth = 2380,
            baseFareShovon = 550,
            totalCoaches = 18,
            hasDiningCar = true,
            rakeType = "Korean Hyundai Rotem Tourist Rake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("CXB", "Cox's Bazar", "কক্সবাজার", "07:30 PM", "08:00 PM", 30, 0, "1", "08:00 PM", StopStatus.PASSED),
                RouteStop("CTG", "Chattogram", "চট্টগ্রাম", "10:50 PM", "11:15 PM", 25, 160, "1", "11:15 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "03:50 AM", "03:55 AM", 5, 465, "2", "04:05 AM", StopStatus.CURRENT),
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "04:30 AM", "04:30 AM", 0, 480, "3", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "787",
            nameEn = "Sonar Bangla Express",
            nameBn = "সোনার বাংলা এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "CTG",
            destStationNameEn = "Chattogram",
            destStationNameBn = "চট্টগ্রাম",
            departureTime = "07:00 AM",
            arrivalTime = "12:15 PM",
            offDayEn = "Wednesday",
            offDayBn = "বুধবার",
            totalDistanceKm = 320,
            baseFareSChair = 405,
            baseFareSnigdha = 777,
            baseFareAcSeat = 932,
            baseFareAcBerth = 1398,
            baseFareShovon = 340,
            totalCoaches = 16,
            hasDiningCar = true,
            rakeType = "Indonesian Red-Green Premium Express",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = false),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "06:30 AM", "07:00 AM", 30, 0, "5", "07:00 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "07:25 AM", "07:30 AM", 5, 15, "1", "07:30 AM", StopStatus.PASSED),
                RouteStop("CTG", "Chattogram", "চট্টগ্রাম", "12:15 PM", "12:15 PM", 0, 320, "1", null, StopStatus.UPCOMING)
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
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Indonesian Meter Gauge Express",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = false),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "05:50 AM", "06:20 AM", 30, 0, "6", "06:20 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "06:45 AM", "06:50 AM", 5, 15, "1", "06:50 AM", StopStatus.PASSED),
                RouteStop("BHB", "Bhairab Bazar", "ভৈরব বাজার", "07:55 AM", "08:00 AM", 5, 85, "2", "08:05 AM", StopStatus.PASSED),
                RouteStop("BBA", "Brahmanbaria", "ব্রাহ্মণবাড়িয়া", "08:45 AM", "08:50 AM", 5, 115, "2", "09:05 AM", StopStatus.PASSED),
                RouteStop("AKH", "Akhaura", "আখাউড়া", "09:20 AM", "09:25 AM", 5, 140, "1", "09:35 AM", StopStatus.PASSED),
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
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Broad Gauge LHB Air Brake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "02:15 PM", "02:45 PM", 30, 0, "7", "02:45 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "03:10 PM", "03:15 PM", 5, 15, "1", "03:15 PM", StopStatus.PASSED),
                RouteStop("TNG", "Tangail", "টাঙ্গাইল", "04:30 PM", "04:35 PM", 5, 110, "1", "04:40 PM", StopStatus.PASSED),
                RouteStop("SRJ", "Sirajganj", "সিরাজগঞ্জ", "05:30 PM", "05:35 PM", 5, 175, "2", "05:45 PM", StopStatus.PASSED),
                RouteStop("ISD", "Ishwardi", "ঈশ্বরদী", "06:40 PM", "06:45 PM", 5, 260, "2", "06:55 PM", StopStatus.PASSED),
                RouteStop("NAT", "Natore", "নাটোর", "07:30 PM", "07:35 PM", 5, 295, "1", "07:48 PM", StopStatus.CURRENT),
                RouteStop("RAJ", "Rajshahi", "রাজশাহী", "08:35 PM", "08:35 PM", 0, 343, "3", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "791",
            nameEn = "Banalata Express",
            nameBn = "বনলতা এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "RAJ",
            destStationNameEn = "Rajshahi",
            destStationNameBn = "রাজশাহী",
            departureTime = "01:30 PM",
            arrivalTime = "06:00 PM",
            offDayEn = "Friday",
            offDayBn = "শুক্রবার",
            totalDistanceKm = 343,
            baseFareSChair = 425,
            baseFareSnigdha = 815,
            baseFareAcSeat = 975,
            baseFareAcBerth = 1460,
            baseFareShovon = 360,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Indonesian High Speed Broad Gauge",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "01:00 PM", "01:30 PM", 30, 0, "8", "01:30 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "01:55 PM", "02:00 PM", 5, 15, "1", "02:00 PM", StopStatus.PASSED),
                RouteStop("RAJ", "Rajshahi", "রাজশাহী", "06:00 PM", "06:00 PM", 0, 343, "1", null, StopStatus.UPCOMING)
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
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Padma Bridge Route Broad Gauge",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "07:45 AM", "08:15 AM", 30, 0, "2", "08:15 AM", StopStatus.PASSED),
                RouteStop("JAS", "Jashore", "যশোর", "01:45 PM", "01:50 PM", 5, 355, "1", "01:55 PM", StopStatus.PASSED),
                RouteStop("KHL", "Khulna", "খুলনা", "03:50 PM", "03:50 PM", 0, 412, "1", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "795",
            nameEn = "Benapole Express",
            nameBn = "বেনাপোল এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "BPO",
            destStationNameEn = "Benapole",
            destStationNameBn = "বেনাপোল",
            departureTime = "11:45 PM",
            arrivalTime = "07:20 AM",
            offDayEn = "Wednesday",
            offDayBn = "বুধবার",
            totalDistanceKm = 430,
            baseFareSChair = 530,
            baseFareSnigdha = 1015,
            baseFareAcSeat = 1220,
            baseFareAcBerth = 1830,
            baseFareShovon = 440,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Indonesian Broad Gauge Air-Brake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "11:15 PM", "11:45 PM", 30, 0, "4", "11:45 PM", StopStatus.PASSED),
                RouteStop("JAS", "Jashore", "যশোর", "05:30 AM", "05:35 AM", 5, 390, "2", "05:40 AM", StopStatus.PASSED),
                RouteStop("BPO", "Benapole", "বেনাপোল", "07:20 AM", "07:20 AM", 0, 430, "1", null, StopStatus.UPCOMING)
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
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Broad Gauge Heavy Intercity",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "08:40 AM", "09:10 AM", 30, 0, "5", "09:10 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "09:35 AM", "09:40 AM", 5, 15, "1", "09:45 AM", StopStatus.PASSED),
                RouteStop("SAN", "Santahar", "সান্তাহার", "03:30 PM", "03:40 PM", 10, 310, "3", "04:02 PM", StopStatus.PASSED),
                RouteStop("BOG", "Bogura", "বগুড়া", "04:30 PM", "04:35 PM", 5, 350, "1", "05:05 PM", StopStatus.CURRENT),
                RouteStop("RNP", "Rangpur", "রংপুর", "07:05 PM", "07:05 PM", 0, 445, "2", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "793",
            nameEn = "Panchagarh Express",
            nameBn = "পঞ্চগড় এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "PAN",
            destStationNameEn = "Panchagarh",
            destStationNameBn = "পঞ্চগড়",
            departureTime = "10:45 PM",
            arrivalTime = "08:50 AM",
            offDayEn = "None (Daily)",
            offDayBn = "নেই (প্রতিদিন)",
            totalDistanceKm = 590,
            baseFareSChair = 650,
            baseFareSnigdha = 1245,
            baseFareAcSeat = 1495,
            baseFareAcBerth = 2240,
            baseFareShovon = 530,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Long Distance Intercity Rake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "10:15 PM", "10:45 PM", 30, 0, "6", "10:45 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "11:10 PM", "11:15 PM", 5, 15, "1", "11:20 PM", StopStatus.PASSED),
                RouteStop("SAN", "Santahar", "সান্তাহার", "04:05 AM", "04:15 AM", 10, 310, "2", "04:15 AM", StopStatus.PASSED),
                RouteStop("DIN", "Dinajpur", "দিনাজপুর", "06:45 AM", "06:55 AM", 10, 500, "1", "07:05 AM", StopStatus.CURRENT),
                RouteStop("PAN", "Panchagarh", "পঞ্চগড়", "08:50 AM", "08:50 AM", 0, 590, "1", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "735",
            nameEn = "Agnibina Express",
            nameBn = "অগ্নিবীণা এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "JAM",
            destStationNameEn = "Jamalpur",
            destStationNameBn = "জামালপুর",
            departureTime = "11:00 AM",
            arrivalTime = "03:30 PM",
            offDayEn = "None (Daily)",
            offDayBn = "নেই (প্রতিদিন)",
            totalDistanceKm = 210,
            baseFareSChair = 245,
            baseFareSnigdha = 470,
            baseFareAcSeat = 560,
            baseFareAcBerth = 840,
            baseFareShovon = 190,
            totalCoaches = 12,
            hasDiningCar = true,
            rakeType = "Meter Gauge Intercity",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = false),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "10:30 AM", "11:00 AM", 30, 0, "3", "11:00 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "11:25 AM", "11:30 AM", 5, 15, "1", "11:30 AM", StopStatus.PASSED),
                RouteStop("MYM", "Mymensingh", "ময়মনসিংহ", "01:45 PM", "01:55 PM", 10, 150, "1", "01:55 PM", StopStatus.PASSED),
                RouteStop("JAM", "Jamalpur", "জামালপুর", "03:30 PM", "03:30 PM", 0, 210, "2", null, StopStatus.UPCOMING)
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

    fun getTrainByNo(query: String): Train? {
        val q = query.trim()
        return allTrains.find { it.trainNo.equals(q, ignoreCase = true) }
            ?: allTrains.find { it.nameEn.contains(q, ignoreCase = true) || it.nameBn.contains(q) }
    }

    // Universal Global Search: Searches Train Name (EN/BN), Train Number, Station Name (EN/BN), District, Division, or Stop
    fun globalSearch(query: String): List<Train> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return allTrains

        return allTrains.filter { train ->
            train.trainNo.lowercase().contains(q) ||
            train.nameEn.lowercase().contains(q) ||
            train.nameBn.contains(q) ||
            train.originStationNameEn.lowercase().contains(q) ||
            train.originStationNameBn.contains(q) ||
            train.destStationNameEn.lowercase().contains(q) ||
            train.destStationNameBn.contains(q) ||
            train.originStationCode.lowercase().contains(q) ||
            train.destStationCode.lowercase().contains(q) ||
            train.routeStops.any { stop ->
                stop.stationNameEn.lowercase().contains(q) ||
                stop.stationNameBn.contains(q) ||
                stop.stationCode.lowercase().contains(q)
            }
        }
    }

    fun getLiveStatusForTrain(trainNo: String): LiveTrainStatus {
        val train = getTrainByNo(trainNo) ?: allTrains.first()
        val stops = train.routeStops

        val delay = when (train.trainNo) {
            "701" -> 17
            "813" -> 20
            "815" -> 12
            "787" -> 5
            "709" -> 23
            "753" -> 13
            "791" -> 8
            "771" -> 35
            "793" -> 28
            else -> 10
        }
        val speed = when (train.trainNo) {
            "701" -> 72
            "813" -> 78
            "815" -> 82
            "787" -> 85
            "709" -> 45
            "753" -> 64
            "791" -> 90
            else -> 60
        }

        val currentIndex = stops.indexOfFirst { it.status == StopStatus.CURRENT }.let { if (it >= 0) it else 1.coerceAtMost(stops.size - 1) }
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

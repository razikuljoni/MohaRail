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

    // Comprehensive Bangladesh Railway Stations across all zones & districts
    val allStations = listOf(
        Station("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "Dhaka", "Dhaka", true, "01711691550"),
        Station("DAA", "Dhaka (Biman Bandar)", "ঢাকা (বিমানবন্দর)", "Dhaka", "Dhaka", true, "01711691551"),
        Station("DAC", "Dhaka Cantonment", "ঢাকা ক্যান্টনমেন্ট", "Dhaka", "Dhaka", true, "01711691552"),
        Station("JOY", "Joydebpur", "জয়দেবপুর", "Gazipur", "Dhaka", true, "01711691553"),
        Station("CTG", "Chattogram", "চট্টগ্রাম", "Chattogram", "Chattogram", true, "01711691600"),
        Station("CXB", "Cox's Bazar", "কক্সবাজার", "Cox's Bazar", "Chattogram", true, "01711691900"),
        Station("SYL", "Sylhet", "সিলেট", "Sylhet", "Sylhet", true, "01711691700"),
        Station("RAJ", "Rajshahi", "রাজশাহী", "Rajshahi", "Rajshahi", true, "01711691800"),
        Station("KHL", "Khulna", "খুলনা", "Khulna", "Khulna", true, "01711691400"),
        Station("CHI", "Chilahati", "চিলাহাটি", "Nilphamari", "Rangpur", true, "01711691270"),
        Station("NIL", "Nilphamari", "নীলফামারী", "Nilphamari", "Rangpur", true, "01711691271"),
        Station("SAI", "Saidpur", "সৈয়দপুর", "Nilphamari", "Rangpur", true, "01711691272"),
        Station("PAR", "Parbatipur", "পার্বতীপুর", "Dinajpur", "Rangpur", true, "01711691273"),
        Station("RNP", "Rangpur", "রংপুর", "Rangpur", "Rangpur", true, "01711691200"),
        Station("KUR", "Kurigram", "কুড়িগ্রাম", "Kurigram", "Rangpur", true, "01711691240"),
        Station("DIN", "Dinajpur", "দিনাজপুর", "Dinajpur", "Rangpur", true, "01711691220"),
        Station("PAN", "Panchagarh (Sirajul Islam)", "পঞ্চগড় (সিরাজুল ইসলাম)", "Panchagarh", "Rangpur", true, "01711691230"),
        Station("BUR", "Burimari", "বুড়িমারী", "Lalmonirhat", "Rangpur", true, "01711691260"),
        Station("LAL", "Lalmonirhat", "লালমনিরহাট", "Lalmonirhat", "Rangpur", true, "01711691261"),
        Station("BOG", "Bogura", "বগুড়া", "Bogura", "Rajshahi", true, "01711691100"),
        Station("SAN", "Santahar", "সান্তাহার", "Bogura", "Rajshahi", true, "01711691820"),
        Station("ISD", "Ishwardi", "ঈশ্বরদী", "Pabna", "Rajshahi", true, "01711691620"),
        Station("NAT", "Natore", "নাটোর", "Natore", "Rajshahi", true, "01711691720"),
        Station("SRJ", "Sirajganj (M. Monsur Ali)", "সিরাজগঞ্জ (এম মনসুর আলী)", "Sirajganj", "Rajshahi", true, "01711691590"),
        Station("TNG", "Tangail", "টাঙ্গাইল", "Tangail", "Dhaka", true, "01711691580"),
        Station("BPO", "Benapole", "বেনাপোল", "Jashore", "Khulna", true, "01711691920"),
        Station("JAS", "Jashore", "যশোর", "Jashore", "Khulna", true, "01711691930"),
        Station("KUS", "Kushtia Court", "কুষ্টিয়া কোর্ট", "Kushtia", "Khulna", true, "01711691940"),
        Station("POR", "Poradah", "পোড়াদহ", "Kushtia", "Khulna", true, "01711691941"),
        Station("CHU", "Chuadanga", "চুয়াডাঙ্গা", "Chuadanga", "Khulna", true, "01711691942"),
        Station("BBA", "Brahmanbaria", "ব্রাহ্মণবাড়িয়া", "Brahmanbaria", "Chattogram", true, "01711691250"),
        Station("CUM", "Cumilla", "কুমিল্লা", "Cumilla", "Chattogram", true, "01711691350"),
        Station("FENI", "Feni", "ফেনী", "Feni", "Chattogram", true, "01711691450"),
        Station("SRM", "Sreemangal", "শ্রীমঙ্গল", "Moulvibazar", "Sylhet", true, "01711691520"),
        Station("BHB", "Bhairab Bazar", "ভৈরব বাজার", "Kishoreganj", "Dhaka", true, "01711691560"),
        Station("AKH", "Akhaura", "আখাউড়া", "Brahmanbaria", "Chattogram", true, "01711691570"),
        Station("MYM", "Mymensingh", "ময়মনসিংহ", "Mymensingh", "Mymensingh", true, "01711691300"),
        Station("JAM", "Jamalpur", "জামালপুর", "Jamalpur", "Mymensingh", true, "01711691310"),
        Station("DEW", "Dewanganj Bazar", "দেওয়ানগঞ্জ বাজার", "Jamalpur", "Mymensingh", true, "01711691311"),
        Station("TAR", "Tarakandi", "তারাকান্দি", "Jamalpur", "Mymensingh", true, "01711691312"),
        Station("MOH", "Mohanganj", "মোহনগঞ্জ", "Netrokona", "Mymensingh", true, "01711691313"),
        Station("KIS", "Kishoreganj", "কিশোরগঞ্জ", "Kishoreganj", "Dhaka", true, "01711691320"),
        Station("NOA", "Noakhali", "নোয়াখালী", "Noakhali", "Chattogram", true, "01711691460"),
        Station("CHA", "Chandpur", "চাঁদপুর", "Chandpur", "Chattogram", true, "01711691470"),
        Station("GOB", "Gobra (Gopalganj)", "গোবরা (গোপালগঞ্জ)", "Gopalganj", "Dhaka", true, "01711691595")
    )

    // Complete Verified Bangladesh Railway Intercity & Special Trains
    val allTrains = listOf(
        // Chilahati Express (805 / 806)
        Train(
            trainNo = "805",
            nameEn = "Chilahati Express",
            nameBn = "চিলাহাটি এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "CHI",
            destStationNameEn = "Chilahati",
            destStationNameBn = "চিলাহাটি",
            departureTime = "05:00 PM",
            arrivalTime = "02:40 AM",
            offDayEn = "Saturday",
            offDayBn = "শনিবার",
            totalDistanceKm = 492,
            baseFareSChair = 580,
            baseFareSnigdha = 1110,
            baseFareAcSeat = 1335,
            baseFareAcBerth = 2000,
            baseFareShovon = 480,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Indonesian Stainless Steel Broad Gauge",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "04:30 PM", "05:00 PM", 30, 0, "4", "05:00 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "05:25 PM", "05:30 PM", 5, 15, "1", "05:30 PM", StopStatus.PASSED),
                RouteStop("JOY", "Joydebpur", "জয়দেবপুর", "05:55 PM", "06:00 PM", 5, 35, "2", "06:05 PM", StopStatus.PASSED),
                RouteStop("SAN", "Santahar", "সান্তাহার", "10:45 PM", "10:55 PM", 10, 310, "2", "11:05 PM", StopStatus.CURRENT),
                RouteStop("BOG", "Bogura", "বগুড়া", "11:35 PM", "11:40 PM", 5, 350, "1", null, StopStatus.UPCOMING),
                RouteStop("PAR", "Parbatipur", "পার্বতীপুর", "01:05 AM", "01:15 AM", 10, 440, "3", null, StopStatus.UPCOMING),
                RouteStop("SAI", "Saidpur", "সৈয়দপুর", "01:35 AM", "01:40 AM", 5, 455, "1", null, StopStatus.UPCOMING),
                RouteStop("NIL", "Nilphamari", "নীলফামারী", "02:00 AM", "02:05 AM", 5, 470, "1", null, StopStatus.UPCOMING),
                RouteStop("CHI", "Chilahati", "চিলাহাটি", "02:40 AM", "02:40 AM", 0, 492, "1", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "806",
            nameEn = "Chilahati Express",
            nameBn = "চিলাহাটি এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "CHI",
            originStationNameEn = "Chilahati",
            originStationNameBn = "চিলাহাটি",
            destStationCode = "DA",
            destStationNameEn = "Dhaka (Kamalapur)",
            destStationNameBn = "ঢাকা (কমলাপুর)",
            departureTime = "06:00 AM",
            arrivalTime = "03:10 PM",
            offDayEn = "Saturday",
            offDayBn = "শনিবার",
            totalDistanceKm = 492,
            baseFareSChair = 580,
            baseFareSnigdha = 1110,
            baseFareAcSeat = 1335,
            baseFareAcBerth = 2000,
            baseFareShovon = 480,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Indonesian Stainless Steel Broad Gauge",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("CHI", "Chilahati", "চিলাহাটি", "05:30 AM", "06:00 AM", 30, 0, "1", "06:00 AM", StopStatus.PASSED),
                RouteStop("NIL", "Nilphamari", "নীলফামারী", "06:30 AM", "06:35 AM", 5, 22, "1", "06:35 AM", StopStatus.PASSED),
                RouteStop("SAI", "Saidpur", "সৈয়দপুর", "06:55 AM", "07:00 AM", 5, 37, "1", "07:02 AM", StopStatus.PASSED),
                RouteStop("PAR", "Parbatipur", "পার্বতীপুর", "07:20 AM", "07:30 AM", 10, 52, "2", "07:35 AM", StopStatus.PASSED),
                RouteStop("BOG", "Bogura", "বগুড়া", "09:05 AM", "09:10 AM", 5, 142, "1", "09:20 AM", StopStatus.PASSED),
                RouteStop("SAN", "Santahar", "সান্তাহার", "09:50 AM", "10:00 AM", 10, 182, "1", "10:15 AM", StopStatus.CURRENT),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "02:35 PM", "02:40 PM", 5, 477, "2", null, StopStatus.UPCOMING),
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "03:10 PM", "03:10 PM", 0, 492, "3", null, StopStatus.UPCOMING)
            )
        ),
        // Nilsagor Express (765 / 766)
        Train(
            trainNo = "765",
            nameEn = "Nilsagor Express",
            nameBn = "নীলসাগর এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "CHI",
            destStationNameEn = "Chilahati",
            destStationNameBn = "চিলাহাটি",
            departureTime = "06:45 AM",
            arrivalTime = "03:40 PM",
            offDayEn = "Sunday",
            offDayBn = "রবিবার",
            totalDistanceKm = 492,
            baseFareSChair = 580,
            baseFareSnigdha = 1110,
            baseFareAcSeat = 1335,
            baseFareAcBerth = 2000,
            baseFareShovon = 480,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Broad Gauge Air-Brake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "06:15 AM", "06:45 AM", 30, 0, "6", "06:45 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "07:10 AM", "07:15 AM", 5, 15, "1", "07:18 AM", StopStatus.PASSED),
                RouteStop("SAN", "Santahar", "সান্তাহার", "12:15 PM", "12:25 PM", 10, 310, "2", "12:35 PM", StopStatus.PASSED),
                RouteStop("PAR", "Parbatipur", "পার্বতীপুর", "02:10 PM", "02:20 PM", 10, 440, "1", "02:30 PM", StopStatus.CURRENT),
                RouteStop("CHI", "Chilahati", "চিলাহাটি", "03:40 PM", "03:40 PM", 0, 492, "1", null, StopStatus.UPCOMING)
            )
        ),
        Train(
            trainNo = "766",
            nameEn = "Nilsagor Express",
            nameBn = "নীলসাগর এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "CHI",
            originStationNameEn = "Chilahati",
            originStationNameBn = "চিলাহাটি",
            destStationCode = "DA",
            destStationNameEn = "Dhaka (Kamalapur)",
            destStationNameBn = "ঢাকা (কমলাপুর)",
            departureTime = "08:00 PM",
            arrivalTime = "05:00 AM",
            offDayEn = "Sunday",
            offDayBn = "রবিবার",
            totalDistanceKm = 492,
            baseFareSChair = 580,
            baseFareSnigdha = 1110,
            baseFareAcSeat = 1335,
            baseFareAcBerth = 2000,
            baseFareShovon = 480,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Broad Gauge Air-Brake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("CHI", "Chilahati", "চিলাহাটি", "07:30 PM", "08:00 PM", 30, 0, "1", "08:00 PM", StopStatus.PASSED),
                RouteStop("NIL", "Nilphamari", "নীলফামারী", "08:30 PM", "08:35 PM", 5, 22, "1", "08:35 PM", StopStatus.PASSED),
                RouteStop("SAN", "Santahar", "সান্তাহার", "11:45 PM", "11:55 PM", 10, 182, "1", "12:05 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "04:25 AM", "04:30 AM", 5, 477, "2", "04:40 AM", StopStatus.CURRENT),
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "05:00 AM", "05:00 AM", 0, 492, "4", null, StopStatus.UPCOMING)
            )
        ),
        // Suborno Express (701 / 702)
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
        // Cox's Bazar Express (813 / 814)
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
        // Parjotak Express (815 / 816)
        Train(
            trainNo = "815",
            nameEn = "Parjotak Express",
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
        // Sonar Bangla Express (787 / 788)
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
        // Turna Express (741 / 742)
        Train(
            trainNo = "741",
            nameEn = "Turna Express",
            nameBn = "তূর্ণা এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "CTG",
            originStationNameEn = "Chattogram",
            originStationNameBn = "চট্টগ্রাম",
            destStationCode = "DA",
            destStationNameEn = "Dhaka (Kamalapur)",
            destStationNameBn = "ঢাকা (কমলাপুর)",
            departureTime = "11:30 PM",
            arrivalTime = "05:15 AM",
            offDayEn = "None (Daily)",
            offDayBn = "নেই (প্রতিদিন)",
            totalDistanceKm = 320,
            baseFareSChair = 405,
            baseFareSnigdha = 777,
            baseFareAcSeat = 932,
            baseFareAcBerth = 1398,
            baseFareShovon = 340,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Indonesian Night Intercity Rake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("CTG", "Chattogram", "চট্টগ্রাম", "11:00 PM", "11:30 PM", 30, 0, "1", "11:30 PM", StopStatus.PASSED),
                RouteStop("FENI", "Feni", "ফেনী", "12:45 AM", "12:50 AM", 5, 90, "1", "12:55 AM", StopStatus.PASSED),
                RouteStop("CUM", "Cumilla", "কুমিল্লা", "01:30 AM", "01:35 AM", 5, 150, "1", "01:40 AM", StopStatus.PASSED),
                RouteStop("BBA", "Brahmanbaria", "ব্রাহ্মণবাড়িয়া", "02:30 AM", "02:35 AM", 5, 205, "1", "02:45 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "04:45 AM", "04:50 AM", 5, 305, "2", "05:00 AM", StopStatus.CURRENT),
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "05:15 AM", "05:15 AM", 0, 320, "2", null, StopStatus.UPCOMING)
            )
        ),
        // Ekota Express (705 / 706)
        Train(
            trainNo = "705",
            nameEn = "Ekota Express",
            nameBn = "একতা এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "PAN",
            destStationNameEn = "Panchagarh (Sirajul Islam)",
            destStationNameBn = "পঞ্চগড় (সিরাজুল ইসলাম)",
            departureTime = "10:15 AM",
            arrivalTime = "08:10 PM",
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
            rakeType = "Broad Gauge Intercity Rake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "09:45 AM", "10:15 AM", 30, 0, "5", "10:15 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "10:40 AM", "10:45 AM", 5, 15, "1", "10:45 AM", StopStatus.PASSED),
                RouteStop("SAN", "Santahar", "সান্তাহার", "03:45 PM", "03:55 PM", 10, 310, "2", "04:05 PM", StopStatus.PASSED),
                RouteStop("DIN", "Dinajpur", "দিনাজপুর", "06:15 PM", "06:25 PM", 10, 500, "1", "06:30 PM", StopStatus.CURRENT),
                RouteStop("PAN", "Panchagarh", "পঞ্চগড়", "08:10 PM", "08:10 PM", 0, 590, "1", null, StopStatus.UPCOMING)
            )
        ),
        // Drutojan Express (757 / 758)
        Train(
            trainNo = "757",
            nameEn = "Drutojan Express",
            nameBn = "দ্রুতযান এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "PAN",
            destStationNameEn = "Panchagarh (Sirajul Islam)",
            destStationNameBn = "পঞ্চগড় (সিরাজুল ইসলাম)",
            departureTime = "08:00 PM",
            arrivalTime = "06:10 AM",
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
            rakeType = "Broad Gauge Intercity Rake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "07:30 PM", "08:00 PM", 30, 0, "7", "08:00 PM", StopStatus.PASSED),
                RouteStop("SAN", "Santahar", "সান্তাহার", "01:25 AM", "01:35 AM", 10, 310, "1", "01:40 AM", StopStatus.PASSED),
                RouteStop("DIN", "Dinajpur", "দিনাজপুর", "04:10 AM", "04:20 AM", 10, 500, "1", "04:25 AM", StopStatus.CURRENT),
                RouteStop("PAN", "Panchagarh", "পঞ্চগড়", "06:10 AM", "06:10 AM", 0, 590, "1", null, StopStatus.UPCOMING)
            )
        ),
        // Panchagarh Express (793 / 794)
        Train(
            trainNo = "793",
            nameEn = "Panchagarh Express",
            nameBn = "পঞ্চগড় এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "PAN",
            destStationNameEn = "Panchagarh (Sirajul Islam)",
            destStationNameBn = "পঞ্চগড় (সিরাজুল ইসলাম)",
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
        // Kurigram Express (797 / 798)
        Train(
            trainNo = "797",
            nameEn = "Kurigram Express",
            nameBn = "কুড়িগ্রাম এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "KUR",
            destStationNameEn = "Kurigram",
            destStationNameBn = "কুড়িগ্রাম",
            departureTime = "08:45 PM",
            arrivalTime = "06:15 AM",
            offDayEn = "Wednesday",
            offDayBn = "বুধবার",
            totalDistanceKm = 495,
            baseFareSChair = 580,
            baseFareSnigdha = 1110,
            baseFareAcSeat = 1335,
            baseFareAcBerth = 2000,
            baseFareShovon = 480,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Indonesian Stainless Steel Broad Gauge",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "08:15 PM", "08:45 PM", 30, 0, "4", "08:45 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "09:10 PM", "09:15 PM", 5, 15, "1", "09:20 PM", StopStatus.PASSED),
                RouteStop("SAN", "Santahar", "সান্তাহার", "02:15 AM", "02:25 AM", 10, 310, "1", "02:30 AM", StopStatus.PASSED),
                RouteStop("BOG", "Bogura", "বগুড়া", "03:10 AM", "03:15 AM", 5, 350, "1", "03:25 AM", StopStatus.CURRENT),
                RouteStop("RNP", "Rangpur", "রংপুর", "05:00 AM", "05:10 AM", 10, 445, "2", null, StopStatus.UPCOMING),
                RouteStop("KUR", "Kurigram", "কুড়িগ্রাম", "06:15 AM", "06:15 AM", 0, 495, "1", null, StopStatus.UPCOMING)
            )
        ),
        // Rangpur Express (771 / 772)
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
        // Burimari Express (809 / 810)
        Train(
            trainNo = "809",
            nameEn = "Burimari Express",
            nameBn = "বুড়িমারী এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "BUR",
            destStationNameEn = "Burimari",
            destStationNameBn = "বুড়িমারী",
            departureTime = "08:30 AM",
            arrivalTime = "06:40 PM",
            offDayEn = "Tuesday",
            offDayBn = "মঙ্গলবার",
            totalDistanceKm = 520,
            baseFareSChair = 600,
            baseFareSnigdha = 1150,
            baseFareAcSeat = 1380,
            baseFareAcBerth = 2070,
            baseFareShovon = 490,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Broad Gauge Intercity Express",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "08:00 AM", "08:30 AM", 30, 0, "4", "08:30 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "08:55 AM", "09:00 AM", 5, 15, "1", "09:05 AM", StopStatus.PASSED),
                RouteStop("BOG", "Bogura", "বগুড়া", "02:40 PM", "02:45 PM", 5, 350, "1", "03:00 PM", StopStatus.PASSED),
                RouteStop("LAL", "Lalmonirhat", "লালমনিরহাট", "05:10 PM", "05:20 PM", 10, 450, "2", "05:30 PM", StopStatus.CURRENT),
                RouteStop("BUR", "Burimari", "বুড়িমারী", "06:40 PM", "06:40 PM", 0, 520, "1", null, StopStatus.UPCOMING)
            )
        ),
        // Silk City Express (753 / 754)
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
        // Padma Express (759 / 760)
        Train(
            trainNo = "759",
            nameEn = "Padma Express",
            nameBn = "পদ্মা এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "RAJ",
            destStationNameEn = "Rajshahi",
            destStationNameBn = "রাজশাহী",
            departureTime = "11:00 PM",
            arrivalTime = "04:30 AM",
            offDayEn = "Tuesday",
            offDayBn = "মঙ্গলবার",
            totalDistanceKm = 343,
            baseFareSChair = 405,
            baseFareSnigdha = 777,
            baseFareAcSeat = 932,
            baseFareAcBerth = 1398,
            baseFareShovon = 340,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Broad Gauge Intercity Rake",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "10:30 PM", "11:00 PM", 30, 0, "4", "11:00 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "11:25 PM", "11:30 PM", 5, 15, "1", "11:35 PM", StopStatus.PASSED),
                RouteStop("ISD", "Ishwardi", "ঈশ্বরদী", "02:50 AM", "02:55 AM", 5, 260, "2", "03:05 AM", StopStatus.PASSED),
                RouteStop("RAJ", "Rajshahi", "রাজশাহী", "04:30 AM", "04:30 AM", 0, 343, "1", null, StopStatus.UPCOMING)
            )
        ),
        // Banalata Express (791 / 792)
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
            rakeType = "Indonesian High Speed Non-Stop Broad Gauge",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "01:00 PM", "01:30 PM", 30, 0, "8", "01:30 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "01:55 PM", "02:00 PM", 5, 15, "1", "02:00 PM", StopStatus.PASSED),
                RouteStop("RAJ", "Rajshahi", "রাজশাহী", "06:00 PM", "06:00 PM", 0, 343, "1", null, StopStatus.UPCOMING)
            )
        ),
        // Sundarban Express (725 / 726)
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
        // Benapole Express (795 / 796)
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
        // Chitra Express (763 / 764)
        Train(
            trainNo = "763",
            nameEn = "Chitra Express",
            nameBn = "চিত্রা এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "KHL",
            destStationNameEn = "Khulna",
            destStationNameBn = "খুলনা",
            departureTime = "07:00 PM",
            arrivalTime = "03:40 AM",
            offDayEn = "Monday",
            offDayBn = "সোমবার",
            totalDistanceKm = 412,
            baseFareSChair = 505,
            baseFareSnigdha = 970,
            baseFareAcSeat = 1160,
            baseFareAcBerth = 1740,
            baseFareShovon = 420,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Broad Gauge Intercity",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "06:30 PM", "07:00 PM", 30, 0, "3", "07:00 PM", StopStatus.PASSED),
                RouteStop("JAS", "Jashore", "যশোর", "01:50 AM", "01:55 AM", 5, 355, "1", "02:00 AM", StopStatus.PASSED),
                RouteStop("KHL", "Khulna", "খুলনা", "03:40 AM", "03:40 AM", 0, 412, "2", null, StopStatus.UPCOMING)
            )
        ),
        // Parabat Express (709 / 710)
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
        // Upaban Express (739 / 740)
        Train(
            trainNo = "739",
            nameEn = "Upaban Express",
            nameBn = "উপবন এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "SYL",
            destStationNameEn = "Sylhet",
            destStationNameBn = "সিলেট",
            departureTime = "08:30 PM",
            arrivalTime = "05:00 AM",
            offDayEn = "Wednesday",
            offDayBn = "বুধবার",
            totalDistanceKm = 319,
            baseFareSChair = 395,
            baseFareSnigdha = 759,
            baseFareAcSeat = 910,
            baseFareAcBerth = 1365,
            baseFareShovon = 320,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Meter Gauge Night Intercity",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "08:00 PM", "08:30 PM", 30, 0, "5", "08:30 PM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "08:55 PM", "09:00 PM", 5, 15, "1", "09:05 PM", StopStatus.PASSED),
                RouteStop("SRM", "Sreemangal", "শ্রীমঙ্গল", "02:40 AM", "02:45 AM", 5, 230, "1", "02:55 AM", StopStatus.PASSED),
                RouteStop("SYL", "Sylhet", "সিলেট", "05:00 AM", "05:00 AM", 0, 319, "1", null, StopStatus.UPCOMING)
            )
        ),
        // Agnibina Express (735 / 736)
        Train(
            trainNo = "735",
            nameEn = "Agnibina Express",
            nameBn = "অগ্নিবীণা এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "TAR",
            destStationNameEn = "Tarakandi",
            destStationNameBn = "তারাকান্দি",
            departureTime = "11:00 AM",
            arrivalTime = "04:30 PM",
            offDayEn = "None (Daily)",
            offDayBn = "নেই (প্রতিদিন)",
            totalDistanceKm = 235,
            baseFareSChair = 265,
            baseFareSnigdha = 510,
            baseFareAcSeat = 610,
            baseFareAcBerth = 915,
            baseFareShovon = 210,
            totalCoaches = 12,
            hasDiningCar = true,
            rakeType = "Meter Gauge Intercity",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = false),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "10:30 AM", "11:00 AM", 30, 0, "3", "11:00 AM", StopStatus.PASSED),
                RouteStop("DAA", "Dhaka Airport", "ঢাকা বিমানবন্দর", "11:25 AM", "11:30 AM", 5, 15, "1", "11:30 AM", StopStatus.PASSED),
                RouteStop("MYM", "Mymensingh", "ময়মনসিংহ", "01:45 PM", "01:55 PM", 10, 150, "1", "01:55 PM", StopStatus.PASSED),
                RouteStop("JAM", "Jamalpur", "জামালপুর", "03:10 PM", "03:15 PM", 5, 210, "2", "03:20 PM", StopStatus.PASSED),
                RouteStop("TAR", "Tarakandi", "তারাকান্দি", "04:30 PM", "04:30 PM", 0, 235, "1", null, StopStatus.UPCOMING)
            )
        ),
        // Mohanganj Express (789 / 790)
        Train(
            trainNo = "789",
            nameEn = "Mohanganj Express",
            nameBn = "মোহনগঞ্জ এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "DA",
            originStationNameEn = "Dhaka (Kamalapur)",
            originStationNameBn = "ঢাকা (কমলাপুর)",
            destStationCode = "MOH",
            destStationNameEn = "Mohanganj",
            destStationNameBn = "মোহনগঞ্জ",
            departureTime = "01:15 PM",
            arrivalTime = "06:50 PM",
            offDayEn = "Monday",
            offDayBn = "সোমবার",
            totalDistanceKm = 218,
            baseFareSChair = 250,
            baseFareSnigdha = 480,
            baseFareAcSeat = 580,
            baseFareAcBerth = 870,
            baseFareShovon = 200,
            totalCoaches = 12,
            hasDiningCar = true,
            rakeType = "Meter Gauge Intercity",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = false),
            routeStops = listOf(
                RouteStop("DA", "Dhaka (Kamalapur)", "ঢাকা (কমলাপুর)", "12:45 PM", "01:15 PM", 30, 0, "4", "01:15 PM", StopStatus.PASSED),
                RouteStop("MYM", "Mymensingh", "ময়মনসিংহ", "04:00 PM", "04:10 PM", 10, 150, "1", "04:15 PM", StopStatus.PASSED),
                RouteStop("MOH", "Mohanganj", "মোহনগঞ্জ", "06:50 PM", "06:50 PM", 0, 218, "1", null, StopStatus.UPCOMING)
            )
        ),
        // Bijoy Express (785 / 786)
        Train(
            trainNo = "785",
            nameEn = "Bijoy Express",
            nameBn = "বিজয় এক্সপ্রেস",
            type = TrainType.INTERCITY,
            originStationCode = "CTG",
            originStationNameEn = "Chattogram",
            originStationNameBn = "চট্টগ্রাম",
            destStationCode = "MYM",
            destStationNameEn = "Mymensingh",
            destStationNameBn = "ময়মনসিংহ",
            departureTime = "09:00 AM",
            arrivalTime = "06:10 PM",
            offDayEn = "Tuesday",
            offDayBn = "মঙ্গলবার",
            totalDistanceKm = 360,
            baseFareSChair = 430,
            baseFareSnigdha = 825,
            baseFareAcSeat = 990,
            baseFareAcBerth = 1485,
            baseFareShovon = 350,
            totalCoaches = 14,
            hasDiningCar = true,
            rakeType = "Meter Gauge Express",
            coaches = generateCoaches(hasSnigdha = true, hasAcBerth = true),
            routeStops = listOf(
                RouteStop("CTG", "Chattogram", "চট্টগ্রাম", "08:30 AM", "09:00 AM", 30, 0, "2", "09:00 AM", StopStatus.PASSED),
                RouteStop("FENI", "Feni", "ফেনী", "10:15 AM", "10:20 AM", 5, 90, "1", "10:25 AM", StopStatus.PASSED),
                RouteStop("CUM", "Cumilla", "কুমিল্লা", "11:05 AM", "11:10 AM", 5, 150, "1", "11:15 AM", StopStatus.PASSED),
                RouteStop("AKH", "Akhaura", "আখাউড়া", "12:15 PM", "12:20 PM", 5, 200, "1", "12:30 PM", StopStatus.PASSED),
                RouteStop("BHB", "Bhairab Bazar", "ভৈরব বাজার", "01:20 PM", "01:25 PM", 5, 240, "2", "01:35 PM", StopStatus.CURRENT),
                RouteStop("MYM", "Mymensingh", "ময়মনসিংহ", "06:10 PM", "06:10 PM", 0, 360, "1", null, StopStatus.UPCOMING)
            )
        )
    )

    // Crowd reports state flow
    private val _crowdReports = MutableStateFlow(
        listOf(
            CrowdLocationReport("cr-1", "805", "Tanvir Ahmed", "Cha (চ)", "Approaching Santahar Junction outer loop", 72, "Chilahati Express running smooth, clean coach", "2 mins ago"),
            CrowdLocationReport("cr-2", "701", "Sabbir Hossain", "Kha (খ)", "Crossed Feni Junction", 74, "Speed picked up after signal clear", "5 mins ago"),
            CrowdLocationReport("cr-3", "813", "Rahim Uddin", "Ga (গ)", "At Cox's Bazar platform 1", 0, "Boarding in progress, AC chill", "8 mins ago"),
            CrowdLocationReport("cr-4", "765", "Nazmul Hasan", "Gha (ঘ)", "Near Parbatipur bypass", 78, "Nilsagor Express on-time journey", "12 mins ago"),
            CrowdLocationReport("cr-5", "753", "Tareq Mahmud", "Uma (ঙ)", "Crossed Jamuna Bridge loop", 82, "Water and tea available in pantry", "18 mins ago")
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
            "805" -> 14
            "806" -> 8
            "765" -> 18
            "766" -> 10
            "701" -> 17
            "813" -> 20
            "815" -> 12
            "787" -> 5
            "741" -> 7
            "705" -> 22
            "757" -> 15
            "793" -> 28
            "797" -> 19
            "771" -> 35
            "809" -> 25
            "753" -> 13
            "759" -> 10
            "791" -> 8
            "725" -> 16
            "795" -> 12
            "763" -> 14
            "709" -> 23
            "739" -> 15
            "735" -> 9
            "789" -> 11
            "785" -> 20
            else -> 10
        }
        val speed = when (train.trainNo) {
            "805" -> 76
            "765" -> 74
            "701" -> 72
            "813" -> 78
            "815" -> 82
            "787" -> 85
            "741" -> 68
            "791" -> 90
            "753" -> 64
            "793" -> 75
            else -> 65
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
        }.ifEmpty { allTrains.take(4) }
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
                    "805", "Chilahati Express", "চিলাহাটি এক্সপ্রেস", TrainType.INTERCITY,
                    "Dhaka -> Chilahati", "05:00 PM", "05:14 PM", 14, "4", false
                ),
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

package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.SavedTicketEntity
import com.example.data.local.entity.SearchHistoryEntity
import com.example.data.local.entity.TrainAlarmEntity
import com.example.data.model.*
import com.example.data.repository.TrainRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab(val titleEn: String, val titleBn: String) {
    LIVE_TRACK("Live Tracking", "লাইভ ট্র্যাকিং"),
    SCHEDULES("Schedules", "সময়সূচী ও রুট"),
    TICKETING("E-Tickets & Fare", "ই-টিকিট ও ভাড়া"),
    STATION_BOARD("Station Board", "স্টেশন বোর্ড"),
    SYSTEM_DOCS("System Spec & API", "সিস্টেম রোডম্যাপ")
}

data class UiState(
    val currentTab: AppTab = AppTab.LIVE_TRACK,
    val isBengali: Boolean = true,
    // Tracking
    val trackedTrainNo: String = "701",
    val liveStatus: LiveTrainStatus? = null,
    val isTrackingModeGps: Boolean = true, // true = GPS, false = SMS 16318
    val searchTrainQuery: String = "",
    // Schedules
    val searchOriginStation: Station? = null,
    val searchDestStation: Station? = null,
    val journeyDate: String = "Today, 01 Sep",
    val searchResults: List<Train> = emptyList(),
    val selectedTrainDetail: Train? = null,
    // Station Board
    val selectedStationBoardStation: Station? = null,
    val stationBoardFilter: Boolean = true, // true: All/Arrivals, false: Departures
    // Fares & Booking
    val fareOriginStation: Station? = null,
    val fareDestStation: Station? = null,
    val fareSelectedClass: SeatClass = SeatClass.S_CHAIR,
    val farePassengerCount: Int = 1,
    // Dialogs
    val showCrowdReportDialog: Boolean = false,
    val showBookTicketDialog: Boolean = false,
    val showAlarmDialog: Boolean = false,
    val showDetailModal: Boolean = false,
    val bookingSelectedTrain: Train? = null,
    val bookingSelectedClass: SeatClass = SeatClass.S_CHAIR,
    val notificationMessage: String? = null
)

class TrainKothayViewModel(private val repository: TrainRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val allStations = repository.allStations
    val allTrains = repository.allTrains
    val crowdReports = repository.crowdReports

    val savedTickets: StateFlow<List<SavedTicketEntity>> = repository.getSavedTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchHistory: StateFlow<List<SearchHistoryEntity>> = repository.getSearchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alarms: StateFlow<List<TrainAlarmEntity>> = repository.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Initial setup with default stations & train
        val defaultOrigin = allStations.find { it.code == "DA" } ?: allStations[0]
        val defaultDest = allStations.find { it.code == "CTG" } ?: allStations[2]

        _uiState.update {
            it.copy(
                searchOriginStation = defaultOrigin,
                searchDestStation = defaultDest,
                fareOriginStation = defaultOrigin,
                fareDestStation = defaultDest,
                selectedStationBoardStation = defaultOrigin,
                searchResults = repository.searchTrains(defaultOrigin.code, defaultDest.code),
                liveStatus = repository.getLiveStatusForTrain("701")
            )
        }

        // Periodic live simulation speed pulse
        viewModelScope.launch {
            while (true) {
                delay(8000)
                val currentTrainNo = _uiState.value.trackedTrainNo
                val status = repository.getLiveStatusForTrain(currentTrainNo)
                _uiState.update { it.copy(liveStatus = status) }
            }
        }
    }

    fun setTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun toggleLanguage() {
        _uiState.update { it.copy(isBengali = !it.isBengali) }
    }

    fun selectTrainForTracking(trainNo: String) {
        val status = repository.getLiveStatusForTrain(trainNo)
        _uiState.update {
            it.copy(
                trackedTrainNo = trainNo,
                liveStatus = status,
                searchTrainQuery = ""
            )
        }
    }

    fun setTrackingMode(isGps: Boolean) {
        _uiState.update { it.copy(isTrackingModeGps = isGps) }
    }

    fun setSearchOrigin(station: Station) {
        _uiState.update { it.copy(searchOriginStation = station) }
        triggerSearch()
    }

    fun setSearchDest(station: Station) {
        _uiState.update { it.copy(searchDestStation = station) }
        triggerSearch()
    }

    fun swapStations() {
        _uiState.update {
            val origin = it.searchOriginStation
            val dest = it.searchDestStation
            it.copy(
                searchOriginStation = dest,
                searchDestStation = origin
            )
        }
        triggerSearch()
    }

    private fun triggerSearch() {
        val origin = _uiState.value.searchOriginStation?.code ?: "DA"
        val dest = _uiState.value.searchDestStation?.code ?: "CTG"
        val results = repository.searchTrains(origin, dest)
        _uiState.update { it.copy(searchResults = results) }

        viewModelScope.launch {
            _uiState.value.searchOriginStation?.let { orig ->
                _uiState.value.searchDestStation?.let { dst ->
                    repository.saveSearchHistory(
                        SearchHistoryEntity(
                            originStationCode = orig.code,
                            originStationName = orig.nameEn,
                            destStationCode = dst.code,
                            destStationName = dst.nameEn
                        )
                    )
                }
            }
        }
    }

    fun viewTrainDetails(train: Train) {
        _uiState.update {
            it.copy(
                selectedTrainDetail = train,
                showDetailModal = true
            )
        }
    }

    fun closeTrainDetails() {
        _uiState.update { it.copy(showDetailModal = false) }
    }

    fun selectStationBoardStation(station: Station) {
        _uiState.update { it.copy(selectedStationBoardStation = station) }
    }

    fun getStationBoardList(): List<StationArrivalDeparture> {
        val stnCode = _uiState.value.selectedStationBoardStation?.code ?: "DA"
        return repository.getStationBoard(stnCode)
    }

    fun setFareOrigin(station: Station) {
        _uiState.update { it.copy(fareOriginStation = station) }
    }

    fun setFareDest(station: Station) {
        _uiState.update { it.copy(fareDestStation = station) }
    }

    fun setFareClass(seatClass: SeatClass) {
        _uiState.update { it.copy(fareSelectedClass = seatClass) }
    }

    fun setFarePassengers(count: Int) {
        _uiState.update { it.copy(farePassengerCount = count.coerceIn(1, 4)) }
    }

    fun openBookTicketDialog(train: Train, seatClass: SeatClass = SeatClass.S_CHAIR) {
        _uiState.update {
            it.copy(
                showBookTicketDialog = true,
                bookingSelectedTrain = train,
                bookingSelectedClass = seatClass
            )
        }
    }

    fun dismissBookTicketDialog() {
        _uiState.update { it.copy(showBookTicketDialog = false) }
    }

    fun completeTicketBooking(
        passengerName: String,
        coachName: String,
        seatNumbers: String
    ) {
        val train = _uiState.value.bookingSelectedTrain ?: allTrains.first()
        val seatClass = _uiState.value.bookingSelectedClass

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

        val pnr = "BR${(10000000..99999999).random()}"

        viewModelScope.launch {
            repository.saveTicket(
                SavedTicketEntity(
                    pnr = pnr,
                    trainNo = train.trainNo,
                    trainName = train.nameEn,
                    fromStation = train.originStationNameEn,
                    toStation = train.destStationNameEn,
                    journeyDate = "02 Sep 2026",
                    departureTime = train.departureTime,
                    seatClass = seatClass.nameEn,
                    coachName = coachName,
                    seatNumbers = seatNumbers,
                    passengerName = passengerName.ifBlank { "Passenger" },
                    totalFare = total
                )
            )
            _uiState.update {
                it.copy(
                    showBookTicketDialog = false,
                    notificationMessage = "Ticket booked successfully! PNR: $pnr"
                )
            }
        }
    }

    fun deleteTicket(id: Long) {
        viewModelScope.launch {
            repository.deleteTicket(id)
        }
    }

    fun openCrowdReportDialog() {
        _uiState.update { it.copy(showCrowdReportDialog = true) }
    }

    fun dismissCrowdReportDialog() {
        _uiState.update { it.copy(showCrowdReportDialog = false) }
    }

    fun submitCrowdReport(reporter: String, coach: String, note: String, speed: Int, passedStation: String) {
        val trainNo = _uiState.value.trackedTrainNo
        repository.submitCrowdReport(trainNo, reporter, coach, note, speed, passedStation)
        _uiState.update {
            it.copy(
                showCrowdReportDialog = false,
                notificationMessage = "Thank you! Live train radar updated."
            )
        }
    }

    fun openAlarmDialog() {
        _uiState.update { it.copy(showAlarmDialog = true) }
    }

    fun dismissAlarmDialog() {
        _uiState.update { it.copy(showAlarmDialog = false) }
    }

    fun createAlarm(trainNo: String, trainName: String, destStation: String, minutes: Int) {
        viewModelScope.launch {
            repository.addAlarm(
                TrainAlarmEntity(
                    trainNo = trainNo,
                    trainName = trainName,
                    destinationStation = destStation,
                    minutesBeforeArrival = minutes,
                    isEnabled = true
                )
            )
            _uiState.update {
                it.copy(
                    showAlarmDialog = false,
                    notificationMessage = "Wake-up alarm set for $destStation ($minutes mins before arrival)"
                )
            }
        }
    }

    fun toggleAlarm(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAlarm(id, isEnabled)
        }
    }

    fun deleteAlarm(id: Long) {
        viewModelScope.launch {
            repository.deleteAlarm(id)
        }
    }

    fun clearNotification() {
        _uiState.update { it.copy(notificationMessage = null) }
    }
}

class TrainKothayViewModelFactory(private val repository: TrainRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainKothayViewModel::class.java)) {
            return TrainKothayViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

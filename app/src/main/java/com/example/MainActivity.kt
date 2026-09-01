package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.repository.TrainRepository
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.TrainKothayViewModel
import com.example.ui.viewmodel.TrainKothayViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: TrainKothayViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = TrainRepository(database.trainAppDao())
        TrainKothayViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MohaRailTheme {
                TrainKothayApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TrainKothayApp(viewModel: TrainKothayViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedTickets by viewModel.savedTickets.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val crowdReports by viewModel.crowdReports.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppHeader(
                isBengali = uiState.isBengali,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onOpenAlarms = { viewModel.openAlarmDialog() },
                notificationMessage = uiState.notificationMessage,
                onDismissNotification = { viewModel.clearNotification() }
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentTab = uiState.currentTab,
                isBengali = uiState.isBengali,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                AppTab.LIVE_TRACK -> {
                    LiveTrackingScreen(
                        isBengali = uiState.isBengali,
                        trackedTrainNo = uiState.trackedTrainNo,
                        liveStatus = uiState.liveStatus,
                        isGpsMode = uiState.isTrackingModeGps,
                        allTrains = viewModel.allTrains,
                        crowdReports = crowdReports,
                        onSelectTrain = { viewModel.selectTrainForTracking(it) },
                        onSetTrackingMode = { viewModel.setTrackingMode(it) },
                        onOpenCrowdReport = { viewModel.openCrowdReportDialog() },
                        onOpenAlarm = { viewModel.openAlarmDialog() },
                        onViewTrainDetails = { viewModel.viewTrainDetails(it) }
                    )
                }

                AppTab.SCHEDULES -> {
                    SchedulesScreen(
                        isBengali = uiState.isBengali,
                        allStations = viewModel.allStations,
                        searchResults = if (uiState.globalSearchQuery.isNotEmpty() || uiState.selectedTrainTypeFilter != null) uiState.globalSearchResults else uiState.searchResults,
                        searchHistory = searchHistory,
                        originStation = uiState.searchOriginStation,
                        destStation = uiState.searchDestStation,
                        onSetOrigin = { viewModel.setSearchOrigin(it) },
                        onSetDest = { viewModel.setSearchDest(it) },
                        onSwapStations = { viewModel.swapStations() },
                        onTrackTrain = {
                            viewModel.selectTrainForTracking(it)
                            viewModel.setTab(AppTab.LIVE_TRACK)
                        },
                        onViewTrainDetail = { viewModel.viewTrainDetails(it) },
                        onBookTicket = { train, seatClass ->
                            viewModel.openBookTicketDialog(train, seatClass)
                        },
                        onGlobalSearch = { viewModel.onGlobalSearch(it) }
                    )
                }

                AppTab.TICKETING -> {
                    TicketingScreen(
                        isBengali = uiState.isBengali,
                        allStations = viewModel.allStations,
                        allTrains = viewModel.allTrains,
                        savedTickets = savedTickets,
                        originStation = uiState.fareOriginStation,
                        destStation = uiState.fareDestStation,
                        selectedSeatClass = uiState.fareSelectedClass,
                        passengerCount = uiState.farePassengerCount,
                        onSetOrigin = { viewModel.setFareOrigin(it) },
                        onSetDest = { viewModel.setFareDest(it) },
                        onSetSeatClass = { viewModel.setFareClass(it) },
                        onSetPassengers = { viewModel.setFarePassengers(it) },
                        onBookTicket = { train, seatClass ->
                            viewModel.openBookTicketDialog(train, seatClass)
                        },
                        onDeleteTicket = { viewModel.deleteTicket(it) }
                    )
                }

                AppTab.STATION_BOARD -> {
                    StationBoardScreen(
                        isBengali = uiState.isBengali,
                        allStations = viewModel.allStations,
                        selectedStation = uiState.selectedStationBoardStation,
                        stationBoardList = viewModel.getStationBoardList(),
                        onSelectStation = { viewModel.selectStationBoardStation(it) },
                        onTrackTrain = {
                            viewModel.selectTrainForTracking(it)
                            viewModel.setTab(AppTab.LIVE_TRACK)
                        }
                    )
                }

                AppTab.FEEDBACK -> {
                    FeedbackScreen(
                        isBengali = uiState.isBengali,
                        onShowNotification = { msg ->
                            // Show notification message
                        }
                    )
                }
            }

            // Dialogs
            if (uiState.showCrowdReportDialog) {
                CrowdReportDialog(
                    trainNo = uiState.trackedTrainNo,
                    isBengali = uiState.isBengali,
                    onDismiss = { viewModel.dismissCrowdReportDialog() },
                    onSubmit = { reporter, coach, note, speed, station ->
                        viewModel.submitCrowdReport(reporter, coach, note, speed, station)
                    }
                )
            }

            if (uiState.showBookTicketDialog && uiState.bookingSelectedTrain != null) {
                BookTicketDialog(
                    train = uiState.bookingSelectedTrain!!,
                    seatClass = uiState.bookingSelectedClass,
                    isBengali = uiState.isBengali,
                    onDismiss = { viewModel.dismissBookTicketDialog() },
                    onConfirm = { name, coach, seat ->
                        viewModel.completeTicketBooking(name, coach, seat)
                    }
                )
            }

            if (uiState.showAlarmDialog) {
                val currentTr = viewModel.allTrains.find { it.trainNo == uiState.trackedTrainNo } ?: viewModel.allTrains.first()
                AlarmDialog(
                    alarms = alarms,
                    trainNo = currentTr.trainNo,
                    trainName = if (uiState.isBengali) currentTr.nameBn else currentTr.nameEn,
                    isBengali = uiState.isBengali,
                    onDismiss = { viewModel.dismissAlarmDialog() },
                    onCreateAlarm = { tNo, tName, stn, mins ->
                        viewModel.createAlarm(tNo, tName, stn, mins)
                    },
                    onToggleAlarm = { id, enabled -> viewModel.toggleAlarm(id, enabled) },
                    onDeleteAlarm = { id -> viewModel.deleteAlarm(id) }
                )
            }

            if (uiState.showDetailModal && uiState.selectedTrainDetail != null) {
                TrainDetailModal(
                    train = uiState.selectedTrainDetail!!,
                    isBengali = uiState.isBengali,
                    onDismiss = { viewModel.closeTrainDetails() },
                    onTrack = {
                        viewModel.selectTrainForTracking(uiState.selectedTrainDetail!!.trainNo)
                        viewModel.closeTrainDetails()
                        viewModel.setTab(AppTab.LIVE_TRACK)
                    }
                )
            }
        }
    }
}

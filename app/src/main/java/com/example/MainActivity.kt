package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AgriDatabase
import com.example.data.model.UserRole
import com.example.data.repository.AgriRepository
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.ReviewDialog
import com.example.ui.components.RoleSwitcherBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AgriViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

class AgriViewModelFactory(private val repository: AgriRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgriViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgriViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AgriDatabase.getInstance(applicationContext)
        val repository = AgriRepository(database.agriDao())
        val factory = AgriViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[AgriViewModel::class.java]

        setContent {
            MyApplicationTheme {
                AgriApp(viewModel = viewModel, onFinish = { finish() })
            }
        }
    }
}

@Composable
fun AgriApp(viewModel: AgriViewModel, onFinish: () -> Unit) {
    val scope = rememberCoroutineScope()
    val currentRole by viewModel.currentUserRole.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val allExperts by viewModel.allExperts.collectAsStateWithLifecycle()
    val filteredExperts by viewModel.filteredExperts.collectAsStateWithLifecycle()
    val farmerProblems by viewModel.farmerProblems.collectAsStateWithLifecycle()
    val farmerBookings by viewModel.farmerBookings.collectAsStateWithLifecycle()
    val expertBookings by viewModel.expertBookings.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val platformSettings by viewModel.platformSettings.collectAsStateWithLifecycle()
    val notifications by viewModel.userNotifications.collectAsStateWithLifecycle()
    val showNotifications by viewModel.showNotificationsDialog.collectAsStateWithLifecycle()
    val activeReviewBooking by viewModel.activeReviewBooking.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val selectedCrop by viewModel.selectedCropFilter.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()

    BackHandler(enabled = true) {
        val handled = viewModel.navigateBack()
        if (!handled) {
            onFinish()
        }
    }

    if (showNotifications) {
        NotificationsDialog(
            notifications = notifications,
            onDismiss = { viewModel.toggleNotifications(false) }
        )
    }

    if (activeReviewBooking != null) {
        ReviewDialog(
            booking = activeReviewBooking!!,
            onDismiss = { viewModel.dismissReviewModal() },
            onSubmit = { rating, comment ->
                viewModel.submitReview(rating, comment)
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            RoleSwitcherBar(
                currentRole = currentRole,
                currentScreen = currentScreen,
                notificationCount = notifications.count { !it.isRead },
                onRoleSelected = { role -> viewModel.switchRole(role) },
                onLandingClicked = { viewModel.navigateTo(Screen.Landing) },
                onNotificationsClicked = { viewModel.toggleNotifications(true) }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val screen = currentScreen) {
                is Screen.Landing -> {
                    LandingScreen(
                        onAskExpertClicked = {
                            viewModel.switchRole(UserRole.FARMER)
                            viewModel.navigateTo(Screen.ProblemSubmission)
                        },
                        onBrowseExpertsClicked = {
                            viewModel.switchRole(UserRole.FARMER)
                            viewModel.navigateTo(Screen.ExpertList)
                        },
                        onBecomeExpertClicked = {
                            viewModel.switchRole(UserRole.EXPERT)
                        },
                        onExpertSelected = { expert ->
                            viewModel.navigateTo(Screen.ExpertProfile(expert))
                        },
                        featuredExperts = allExperts
                    )
                }

                is Screen.FarmerHome -> {
                    FarmerHomeScreen(
                        farmerName = "Ramesh Kumar",
                        farmerLocation = "Karnal, Haryana",
                        problems = farmerProblems,
                        bookings = farmerBookings,
                        onAskExpertClicked = { viewModel.navigateTo(Screen.ProblemSubmission) },
                        onBrowseExpertsClicked = { viewModel.navigateTo(Screen.ExpertList) },
                        onViewBookingsClicked = { viewModel.navigateTo(Screen.FarmerBookings) },
                        onProblemClicked = { problem ->
                            viewModel.navigateTo(Screen.ExpertMatches(problem))
                        },
                        onChatClicked = { bookingId ->
                            viewModel.navigateTo(Screen.Chat(bookingId))
                        }
                    )
                }

                is Screen.ProblemSubmission -> {
                    ProblemSubmissionScreen(
                        onBack = { viewModel.navigateBack() },
                        onSubmit = { crop, variety, location, category, desc, started, symptoms, treatment, urgency ->
                            viewModel.submitFarmerProblem(
                                crop = crop,
                                variety = variety,
                                location = location,
                                category = category,
                                description = desc,
                                startedWhen = started,
                                symptoms = symptoms,
                                previousTreatment = treatment,
                                urgency = urgency
                            ) { submittedProblem ->
                                viewModel.navigateTo(Screen.ExpertMatches(submittedProblem))
                            }
                        }
                    )
                }

                is Screen.ExpertMatches -> {
                    var matchResults by remember(screen.problem.id) { mutableStateOf<List<com.example.data.model.ExpertMatchResult>>(emptyList()) }
                    LaunchedEffect(screen.problem.id) {
                        matchResults = viewModel.getRecommendationsForProblem(screen.problem)
                    }

                    ExpertMatchingScreen(
                        problem = screen.problem,
                        matchResults = matchResults,
                        onBack = { viewModel.navigateBack() },
                        onBookExpert = { expert ->
                            viewModel.navigateTo(Screen.BookingFlow(expert, screen.problem.id))
                        },
                        onViewProfile = { expert ->
                            viewModel.navigateTo(Screen.ExpertProfile(expert))
                        }
                    )
                }

                is Screen.ExpertList -> {
                    ExpertListScreen(
                        experts = filteredExperts,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        selectedCrop = selectedCrop,
                        sortOption = sortOption,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategoryChange = { viewModel.setCategoryFilter(it) },
                        onCropChange = { viewModel.setCropFilter(it) },
                        onSortChange = { viewModel.setSortOption(it) },
                        onBack = { viewModel.navigateBack() },
                        onExpertSelected = { expert ->
                            viewModel.navigateTo(Screen.ExpertProfile(expert))
                        },
                        onBookExpert = { expert ->
                            viewModel.navigateTo(Screen.BookingFlow(expert))
                        }
                    )
                }

                is Screen.ExpertProfile -> {
                    val reviews by viewModel.getReviewsForExpert(screen.expert.id).collectAsStateWithLifecycle(emptyList())
                    ExpertProfileScreen(
                        expert = screen.expert,
                        reviews = reviews,
                        onBack = { viewModel.navigateBack() },
                        onBookConsultation = {
                            viewModel.navigateTo(Screen.BookingFlow(screen.expert))
                        }
                    )
                }

                is Screen.BookingFlow -> {
                    val slots by viewModel.getSlotsForExpert(screen.expert.id).collectAsStateWithLifecycle(emptyList())
                    BookingScreen(
                        expert = screen.expert,
                        availableSlots = slots,
                        farmerProblems = farmerProblems,
                        farmerFee = platformSettings?.farmerFee ?: 30.0,
                        onBack = { viewModel.navigateBack() },
                        onConfirmBooking = { slotId, slotDate, slotTime, problemId ->
                            viewModel.bookConsultation(
                                problemId = problemId,
                                expertId = screen.expert.id,
                                slotId = slotId,
                                slotDate = slotDate,
                                slotTime = slotTime,
                                price = screen.expert.pricePerConsultation
                            ) { createdBooking ->
                                viewModel.navigateTo(Screen.Chat(createdBooking.id))
                            }
                        }
                    )
                }

                is Screen.FarmerBookings -> {
                    FarmerBookingsScreen(
                        bookings = farmerBookings,
                        onBack = { viewModel.navigateBack() },
                        onChatClicked = { bookingId ->
                            viewModel.navigateTo(Screen.Chat(bookingId))
                        },
                        onRateClicked = { booking ->
                            viewModel.openReviewModal(booking)
                        }
                    )
                }

                is Screen.Chat -> {
                    val currentBooking = allBookings.find { it.id == screen.bookingId } ?: farmerBookings.find { it.id == screen.bookingId }
                    val messages by viewModel.getMessagesForBooking(screen.bookingId).collectAsStateWithLifecycle(emptyList())

                    if (currentBooking != null) {
                        ChatScreen(
                            booking = currentBooking,
                            currentRole = currentRole,
                            messages = messages,
                            onBack = { viewModel.navigateBack() },
                            onSendMessage = { text, attachment ->
                                viewModel.sendChatMessage(screen.bookingId, text, attachment)
                            },
                            onCompleteAndReview = {
                                viewModel.openReviewModal(currentBooking)
                            }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is Screen.ExpertDashboard -> {
                    val myExpertProfile = allExperts.find { it.id == "expert_1" }
                    val expertSlots by viewModel.getAllSlotsForExpert("expert_1").collectAsStateWithLifecycle(emptyList())

                    ExpertDashboardScreen(
                        expert = myExpertProfile,
                        bookings = expertBookings,
                        slots = expertSlots,
                        onOpenChat = { bookingId ->
                            viewModel.navigateTo(Screen.Chat(bookingId))
                        },
                        onAddSlot = { day, date, time ->
                            viewModel.addExpertAvailabilitySlot(day, date, time)
                        },
                        onDeleteSlot = { slotId ->
                            viewModel.deleteAvailabilitySlot(slotId)
                        }
                    )
                }

                is Screen.AdminDashboard -> {
                    AdminDashboardScreen(
                        users = allUsers,
                        experts = allExperts,
                        bookings = allBookings,
                        settings = platformSettings,
                        onToggleVerification = { expertId, isVerified ->
                            viewModel.toggleExpertVerification(expertId, isVerified)
                        },
                        onUpdateFees = { farmerFee, expertFee ->
                            viewModel.updatePlatformFees(farmerFee, expertFee)
                        },
                        onToggleSuspension = { userId, isSuspended ->
                            viewModel.toggleUserSuspension(userId, isSuspended)
                        }
                    )
                }
            }
        }
    }
}

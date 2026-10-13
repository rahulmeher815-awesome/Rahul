package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.repository.AgriRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    data object Landing : Screen()
    data object FarmerHome : Screen()
    data object ProblemSubmission : Screen()
    data class ExpertMatches(val problem: ProblemEntity) : Screen()
    data object ExpertList : Screen()
    data class ExpertProfile(val expert: ExpertDetail) : Screen()
    data class BookingFlow(val expert: ExpertDetail, val problemId: String? = null) : Screen()
    data object FarmerBookings : Screen()
    data class Chat(val bookingId: String) : Screen()
    data object ExpertDashboard : Screen()
    data object AdminDashboard : Screen()
}

enum class ExpertSortOption(val label: String) {
    RECOMMENDED("Recommended"),
    RATING("Highest Rated"),
    PRICE_LOW("Lowest Price"),
    EXPERIENCE("Most Experienced")
}

class AgriViewModel(private val repository: AgriRepository) : ViewModel() {

    // Current Navigation Screen Stack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Landing))
    val currentScreen = _screenStack.map { it.lastOrNull() ?: Screen.Landing }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Screen.Landing
    )

    // Active Role and User
    private val _currentUserRole = MutableStateFlow(UserRole.FARMER)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    private val _currentUserId = MutableStateFlow("farmer_1")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    // Expert Directory Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _selectedCropFilter = MutableStateFlow<String?>(null)
    val selectedCropFilter: StateFlow<String?> = _selectedCropFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(ExpertSortOption.RECOMMENDED)
    val sortOption: StateFlow<ExpertSortOption> = _sortOption.asStateFlow()

    // Notifications Dialog
    private val _showNotificationsDialog = MutableStateFlow(false)
    val showNotificationsDialog: StateFlow<Boolean> = _showNotificationsDialog.asStateFlow()

    // Review Dialog
    private val _activeReviewBooking = MutableStateFlow<BookingEntity?>(null)
    val activeReviewBooking: StateFlow<BookingEntity?> = _activeReviewBooking.asStateFlow()

    // Data Flows
    val allExperts: StateFlow<List<ExpertDetail>> = repository.getAllExpertDetailsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val farmerProblems: StateFlow<List<ProblemEntity>> = _currentUserId.flatMapLatest { id ->
        repository.getProblemsForFarmerFlow(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProblems: StateFlow<List<ProblemEntity>> = repository.getAllProblemsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val farmerBookings: StateFlow<List<BookingEntity>> = _currentUserId.flatMapLatest { id ->
        repository.getBookingsForFarmerFlow(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expertBookings: StateFlow<List<BookingEntity>> = _currentUserId.flatMapLatest { id ->
        repository.getBookingsForExpertFlow(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<BookingEntity>> = repository.getAllBookingsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsersFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val platformSettings: StateFlow<PlatformSettingsEntity?> = repository.getPlatformSettingsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val userNotifications: StateFlow<List<NotificationEntity>> = _currentUserId.flatMapLatest { id ->
        repository.getNotificationsForUserFlow(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Experts
    val filteredExperts: StateFlow<List<ExpertDetail>> = combine(
        allExperts,
        _searchQuery,
        _selectedCategoryFilter,
        _selectedCropFilter,
        _sortOption
    ) { experts, query, category, crop, sort ->
        var list = experts

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.title.lowercase().contains(q) ||
                it.location.lowercase().contains(q) ||
                it.specializedCrops.any { c -> c.lowercase().contains(q) } ||
                it.expertiseCategories.any { e -> e.lowercase().contains(q) }
            }
        }

        if (category != null) {
            list = list.filter { exp ->
                exp.expertiseCategories.any { it.contains(category, ignoreCase = true) }
            }
        }

        if (crop != null) {
            list = list.filter { exp ->
                exp.specializedCrops.any { it.contains(crop, ignoreCase = true) }
            }
        }

        when (sort) {
            ExpertSortOption.RECOMMENDED -> list.sortedWith(compareByDescending<ExpertDetail> { it.isVerified }.thenByDescending { it.rating })
            ExpertSortOption.RATING -> list.sortedByDescending { it.rating }
            ExpertSortOption.PRICE_LOW -> list.sortedBy { it.pricePerConsultation }
            ExpertSortOption.EXPERIENCE -> list.sortedByDescending { it.experienceYears }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        val current = _screenStack.value.toMutableList()
        current.add(screen)
        _screenStack.value = current
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value.toMutableList()
        if (current.size > 1) {
            current.removeAt(current.lastIndex)
            _screenStack.value = current
            return true
        }
        return false
    }

    fun resetToHome() {
        when (_currentUserRole.value) {
            UserRole.FARMER -> _screenStack.value = listOf(Screen.FarmerHome)
            UserRole.EXPERT -> _screenStack.value = listOf(Screen.ExpertDashboard)
            UserRole.ADMIN -> _screenStack.value = listOf(Screen.AdminDashboard)
        }
    }

    // Role Switcher
    fun switchRole(role: UserRole) {
        _currentUserRole.value = role
        when (role) {
            UserRole.FARMER -> {
                _currentUserId.value = "farmer_1"
                _screenStack.value = listOf(Screen.FarmerHome)
            }
            UserRole.EXPERT -> {
                _currentUserId.value = "expert_1" // Dr. Rahul Sharma
                _screenStack.value = listOf(Screen.ExpertDashboard)
            }
            UserRole.ADMIN -> {
                _currentUserId.value = "admin_1"
                _screenStack.value = listOf(Screen.AdminDashboard)
            }
        }
    }

    // Search & Filter setters
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setCategoryFilter(category: String?) { _selectedCategoryFilter.value = category }
    fun setCropFilter(crop: String?) { _selectedCropFilter.value = crop }
    fun setSortOption(option: ExpertSortOption) { _sortOption.value = option }

    // Farmer Operations
    fun submitFarmerProblem(
        crop: String,
        variety: String,
        location: String,
        category: String,
        description: String,
        startedWhen: String,
        symptoms: String,
        previousTreatment: String,
        urgency: String,
        onSubmitted: (ProblemEntity) -> Unit
    ) {
        viewModelScope.launch {
            val problem = repository.submitProblem(
                farmerId = _currentUserId.value,
                crop = crop,
                cropVariety = variety,
                location = location,
                category = category,
                description = description,
                startedWhen = startedWhen,
                symptoms = symptoms,
                previousTreatment = previousTreatment,
                urgency = urgency
            )
            onSubmitted(problem)
        }
    }

    suspend fun getRecommendationsForProblem(problem: ProblemEntity): List<ExpertMatchResult> {
        return repository.matchExpertsForProblem(problem)
    }

    fun bookConsultation(
        problemId: String?,
        expertId: String,
        slotId: String,
        slotDate: String,
        slotTime: String,
        price: Double,
        onSuccess: (BookingEntity) -> Unit
    ) {
        viewModelScope.launch {
            val booking = repository.createBooking(
                problemId = problemId,
                farmerId = _currentUserId.value,
                expertId = expertId,
                slotId = slotId,
                slotDate = slotDate,
                slotTime = slotTime,
                consultationPrice = price
            )
            onSuccess(booking)
        }
    }

    fun sendChatMessage(bookingId: String, text: String, attachment: String? = null) {
        viewModelScope.launch {
            val role = if (_currentUserRole.value == UserRole.EXPERT) "EXPERT" else "FARMER"
            repository.sendMessage(
                bookingId = bookingId,
                senderId = _currentUserId.value,
                senderRole = role,
                text = text,
                attachmentDesc = attachment
            )
        }
    }

    fun openReviewModal(booking: BookingEntity) {
        _activeReviewBooking.value = booking
    }

    fun dismissReviewModal() {
        _activeReviewBooking.value = null
    }

    fun submitReview(rating: Int, comment: String) {
        val booking = _activeReviewBooking.value ?: return
        viewModelScope.launch {
            repository.submitReview(
                bookingId = booking.id,
                expertId = booking.expertId,
                farmerId = booking.farmerId,
                farmerName = "Ramesh Kumar",
                rating = rating,
                reviewText = comment
            )
            _activeReviewBooking.value = null
        }
    }

    // Expert Operations
    fun addExpertAvailabilitySlot(dayLabel: String, dateString: String, timeSlot: String) {
        viewModelScope.launch {
            repository.addAvailabilitySlot(_currentUserId.value, dayLabel, dateString, timeSlot)
        }
    }

    fun deleteAvailabilitySlot(slotId: String) {
        viewModelScope.launch {
            repository.deleteAvailabilitySlot(slotId)
        }
    }

    fun getSlotsForExpert(expertId: String) = repository.getAvailableSlotsForExpertFlow(expertId)

    fun getAllSlotsForExpert(expertId: String) = repository.getAllSlotsForExpertFlow(expertId)

    fun getMessagesForBooking(bookingId: String) = repository.getMessagesForBookingFlow(bookingId)

    fun getReviewsForExpert(expertId: String) = repository.getReviewsForExpertFlow(expertId)

    // Admin Operations
    fun toggleExpertVerification(expertId: String, isVerified: Boolean) {
        viewModelScope.launch {
            repository.setExpertVerification(expertId, isVerified)
        }
    }

    fun updatePlatformFees(farmerFee: Double, expertFee: Double) {
        viewModelScope.launch {
            repository.updatePlatformFees(farmerFee, expertFee)
        }
    }

    fun toggleUserSuspension(userId: String, isSuspended: Boolean) {
        viewModelScope.launch {
            repository.toggleUserSuspension(userId, isSuspended)
        }
    }

    // Notifications Dialog
    fun toggleNotifications(show: Boolean) {
        _showNotificationsDialog.value = show
    }
}

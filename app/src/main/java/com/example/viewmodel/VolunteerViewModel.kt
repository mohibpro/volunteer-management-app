package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.VolunteerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VolunteerUiState(
    val filter: FilterCriteria = FilterCriteria(),
    val opportunities: List<OpportunityWithDetails> = emptyList(),
    val savedOpportunities: List<OpportunityWithDetails> = emptyList(),
    val myApplications: List<ApplicationWithOpportunity> = emptyList(),
    val currentUser: UserEntity? = null,
    val selectedOpportunity: OpportunityWithDetails? = null,
    val isLoading: Boolean = false,
    val applicationSubmitting: Boolean = false,
    val applicationSuccess: Boolean = false,
    val userFeedbackMessage: String? = null,
    val showFilterSheet: Boolean = false
)

class VolunteerViewModel(
    private val repository: VolunteerRepository
) : ViewModel() {

    private val _filterCriteria = MutableStateFlow(FilterCriteria())
    val filterCriteria: StateFlow<FilterCriteria> = _filterCriteria.asStateFlow()

    private val _uiState = MutableStateFlow(VolunteerUiState())
    val uiState: StateFlow<VolunteerUiState> = _uiState.asStateFlow()

    init {
        // Observe current volunteer user
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }

        // Observe opportunities reactive to filter
        viewModelScope.launch {
            _filterCriteria.flatMapLatest { filter ->
                repository.getAllOpportunitiesWithDetails(filter)
            }.collect { opps ->
                _uiState.update { state ->
                    state.copy(
                        opportunities = opps,
                        savedOpportunities = opps.filter { it.isSaved }
                    )
                }
            }
        }

        // Observe applications of current volunteer
        viewModelScope.launch {
            repository.getVolunteerApplications().collect { apps ->
                _uiState.update { it.copy(myApplications = apps) }
            }
        }
    }

    fun setQuery(query: String) {
        _filterCriteria.update { it.copy(query = query) }
    }

    fun selectCause(cause: String?) {
        _filterCriteria.update {
            val newCause = if (it.selectedCause == cause) null else cause
            it.copy(selectedCause = newCause)
        }
    }

    fun selectWorkMode(mode: WorkMode?) {
        _filterCriteria.update { it.copy(selectedWorkMode = mode) }
    }

    fun selectCommitment(comm: CommitmentType?) {
        _filterCriteria.update { it.copy(selectedCommitment = comm) }
    }

    fun selectCity(city: String?) {
        _filterCriteria.update {
            val newCity = if (it.selectedCity == city) null else city
            it.copy(selectedCity = newCity)
        }
    }

    fun setSortBy(sort: SortOption) {
        _filterCriteria.update { it.copy(sortBy = sort) }
    }

    fun setMaxDistance(km: Int) {
        _filterCriteria.update { it.copy(maxDistanceKm = km) }
    }

    fun resetFilters() {
        _filterCriteria.value = FilterCriteria()
    }

    fun toggleFilterSheet(show: Boolean) {
        _uiState.update { it.copy(showFilterSheet = show) }
    }

    fun loadOpportunityDetails(oppId: Long) {
        viewModelScope.launch {
            repository.getOpportunityDetails(oppId).collect { oppDetails ->
                _uiState.update { it.copy(selectedOpportunity = oppDetails) }
            }
        }
    }

    fun toggleSave(oppId: Long) {
        viewModelScope.launch {
            repository.toggleSaveOpportunity(oppId)
        }
    }

    fun applyForOpportunity(oppId: Long, note: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(applicationSubmitting = true, applicationSuccess = false) }
            val result = repository.applyForOpportunity(oppId, note)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        applicationSubmitting = false,
                        applicationSuccess = true,
                        userFeedbackMessage = "Application submitted successfully! The organizer will review your profile."
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        applicationSubmitting = false,
                        applicationSuccess = false,
                        userFeedbackMessage = err.message ?: "Failed to submit application"
                    )
                }
            }
        }
    }

    fun cancelApplication(applicationId: Long) {
        viewModelScope.launch {
            repository.cancelApplication(applicationId)
            _uiState.update { it.copy(userFeedbackMessage = "Application withdrawn") }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(userFeedbackMessage = null, applicationSuccess = false) }
    }

    fun updateProfile(
        fullName: String,
        phone: String,
        city: String,
        bio: String,
        skills: List<String>,
        causes: List<String>,
        radiusKm: Int,
        workMode: WorkMode,
        hoursPerWeek: Int,
        availableDays: List<String>
    ) {
        viewModelScope.launch {
            val current = _uiState.value.currentUser ?: return@launch
            val updated = current.copy(
                fullName = fullName,
                phone = phone,
                city = city,
                bio = bio,
                skills = skills,
                causes = causes,
                searchRadiusKm = radiusKm,
                workModePreference = workMode,
                hoursPerWeek = hoursPerWeek,
                availableDays = availableDays
            )
            repository.updateUserProfile(updated)
            _uiState.update { it.copy(userFeedbackMessage = "Profile updated successfully!") }
        }
    }
}

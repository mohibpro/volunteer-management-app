package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.VolunteerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OrganizerUiState(
    val organization: OrganizationEntity? = null,
    val myOpportunities: List<OpportunityEntity> = emptyList(),
    val selectedOpportunity: OpportunityEntity? = null,
    val applicationsForSelectedOpp: List<ApplicationWithOpportunity> = emptyList(),
    val isCreatingOpportunity: Boolean = false,
    val creationSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class OrganizerViewModel(
    private val repository: VolunteerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrganizerUiState())
    val uiState: StateFlow<OrganizerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentUser.flatMapLatest { user ->
                if (user != null) {
                    repository.getOrganizationForAdmin(user.id)
                } else {
                    flowOf(null)
                }
            }.collect { org ->
                _uiState.update { it.copy(organization = org) }
            }
        }

        viewModelScope.launch {
            repository.currentUser.flatMapLatest { user ->
                if (user != null) {
                    repository.getOpportunitiesByOrganizer(user.id)
                } else {
                    flowOf(emptyList())
                }
            }.collect { opps ->
                _uiState.update { it.copy(myOpportunities = opps) }
            }
        }
    }

    fun selectOpportunity(opportunity: OpportunityEntity) {
        _uiState.update { it.copy(selectedOpportunity = opportunity) }
        viewModelScope.launch {
            repository.getApplicationsForOpportunity(opportunity.id).collect { apps ->
                _uiState.update { it.copy(applicationsForSelectedOpp = apps) }
            }
        }
    }

    fun updateApplicationStatus(
        applicationId: Long,
        newStatus: ApplicationStatus,
        feedback: String = "",
        hours: Double = 0.0
    ) {
        viewModelScope.launch {
            repository.updateApplicationStatus(applicationId, newStatus, feedback, hours)
            _uiState.update {
                it.copy(successMessage = "Application updated to ${newStatus.displayName()}")
            }
        }
    }

    fun createOpportunity(
        title: String,
        description: String,
        cause: String,
        workMode: WorkMode,
        locationName: String,
        startDate: String,
        timeSlot: String,
        commitmentType: CommitmentType,
        dailyHours: Double,
        totalHours: Double,
        capacity: Int,
        requiredSkills: List<String>,
        perks: List<String>,
        contactPerson: String
    ) {
        viewModelScope.launch {
            val org = _uiState.value.organization
            if (org == null) {
                _uiState.update { it.copy(errorMessage = "No active organization profile found") }
                return@launch
            }

            if (title.isBlank() || description.isBlank() || locationName.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Please fill in all required fields") }
                return@launch
            }

            _uiState.update { it.copy(isCreatingOpportunity = true, errorMessage = null) }
            val newOpp = OpportunityEntity(
                orgId = org.id,
                title = title.trim(),
                description = description.trim(),
                cause = cause,
                workMode = workMode,
                locationName = locationName.trim(),
                startDate = startDate.ifBlank { "Upcoming Weekend" },
                timeSlot = timeSlot.ifBlank { "Flexible" },
                commitmentType = commitmentType,
                dailyHours = dailyHours,
                totalEstimatedHours = totalHours,
                capacity = capacity.coerceAtLeast(1),
                requiredSkills = requiredSkills,
                perks = perks,
                contactPerson = contactPerson.ifBlank { org.name }
            )

            val id = repository.createOpportunity(newOpp)
            if (id > 0) {
                _uiState.update {
                    it.copy(
                        isCreatingOpportunity = false,
                        creationSuccess = true,
                        successMessage = "Opportunity published successfully!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isCreatingOpportunity = false,
                        errorMessage = "Failed to publish opportunity"
                    )
                }
            }
        }
    }

    fun updateOrganizationDetails(
        name: String,
        tagline: String,
        description: String,
        website: String,
        phone: String,
        email: String,
        city: String
    ) {
        viewModelScope.launch {
            val current = _uiState.value.organization ?: return@launch
            val updated = current.copy(
                name = name.trim(),
                tagline = tagline.trim(),
                description = description.trim(),
                website = website.trim(),
                phone = phone.trim(),
                email = email.trim(),
                city = city.trim()
            )
            repository.updateOrganization(updated)
            _uiState.update { it.copy(successMessage = "Organization profile updated!") }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null, creationSuccess = false) }
    }
}

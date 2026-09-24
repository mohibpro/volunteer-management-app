package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class VolunteerRepository(
    private val database: AppDatabase
) {
    private val userDao = database.userDao()
    private val orgDao = database.organizationDao()
    private val oppDao = database.opportunityDao()
    private val appDao = database.applicationDao()
    private val savedDao = database.savedOpportunityDao()

    // Active user state flow
    private val _currentUserId = MutableStateFlow(1L) // 1L is Alex (Volunteer), 2L is Sarah (Organizer)
    val currentUserId: StateFlow<Long> = _currentUserId.asStateFlow()

    val currentUser: Flow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        userDao.getUserById(id)
    }

    suspend fun switchUser(userId: Long) {
        _currentUserId.value = userId
    }

    suspend fun quickSwitchRole(role: UserRole) {
        withContext(Dispatchers.IO) {
            val user = userDao.getFirstUserByRole(role)
            if (user != null) {
                _currentUserId.value = user.id
            }
        }
    }

    suspend fun signIn(email: String, password: String):Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email.trim())
        if (user != null) {
            _currentUserId.value = user.id
            Result.success(user)
        } else {
            Result.failure(Exception("Account not found with this email"))
        }
    }

    suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existing = userDao.getUserByEmail(email.trim())
        if (existing != null) {
            return@withContext Result.failure(Exception("An account with this email already exists"))
        }
        val newUser = UserEntity(
            email = email.trim(),
            fullName = fullName.trim(),
            role = role,
            passwordHash = password,
            skills = if (role == UserRole.VOLUNTEER) listOf("Event Management") else emptyList(),
            causes = if (role == UserRole.VOLUNTEER) listOf("Environment", "Education") else emptyList()
        )
        val id = userDao.insertUser(newUser)
        val created = newUser.copy(id = id)

        if (role == UserRole.ORGANIZER) {
            orgDao.insertOrganization(
                OrganizationEntity(
                    adminUserId = id,
                    name = "$fullName's Initiative",
                    description = "Community impact organization focused on local engagement and outreach.",
                    email = email.trim(),
                    city = "Karachi"
                )
            )
        }

        _currentUserId.value = id
        Result.success(created)
    }

    fun getAllOpportunitiesWithDetails(filter: FilterCriteria): Flow<List<OpportunityWithDetails>> {
        return combine(
            oppDao.getAllOpportunities(),
            orgDao.getAllOrganizations(),
            currentUser,
            _currentUserId.flatMapLatest { savedDao.getSavedOpportunityIds(it) },
            _currentUserId.flatMapLatest { appDao.getApplicationsByVolunteer(it) }
        ) { opportunities, orgs, user, savedIds, applications ->
            val orgMap = orgs.associateBy { it.id }
            val appMap = applications.associateBy { it.opportunityId }
            val volunteer = user ?: SeedData.defaultVolunteer

            opportunities
                .filter { opp ->
                    val matchesQuery = filter.query.isBlank() ||
                            opp.title.contains(filter.query, ignoreCase = true) ||
                            opp.description.contains(filter.query, ignoreCase = true) ||
                            opp.cause.contains(filter.query, ignoreCase = true) ||
                            opp.locationName.contains(filter.query, ignoreCase = true) ||
                            (orgMap[opp.orgId]?.name?.contains(filter.query, ignoreCase = true) == true)

                    val matchesCause = filter.selectedCause == null || opp.cause.equals(filter.selectedCause, ignoreCase = true)
                    val matchesMode = filter.selectedWorkMode == null || opp.workMode == filter.selectedWorkMode
                    val matchesCommitment = filter.selectedCommitment == null || opp.commitmentType == filter.selectedCommitment
                    val matchesCity = filter.selectedCity == null ||
                            opp.locationName.contains(filter.selectedCity, ignoreCase = true) ||
                            (orgMap[opp.orgId]?.city?.contains(filter.selectedCity, ignoreCase = true) == true)
                    val matchesOpen = !filter.onlyOpen || opp.isOpen

                    matchesQuery && matchesCause && matchesMode && matchesCommitment && matchesCity && matchesOpen
                }
                .map { opp ->
                    val org = orgMap[opp.orgId]
                    val matchResult = MatchingEngine.calculateMatch(volunteer, opp)
                    OpportunityWithDetails(
                        opportunity = opp,
                        organization = org,
                        matchResult = matchResult,
                        isSaved = savedIds.contains(opp.id),
                        userApplication = appMap[opp.id]
                    )
                }
                .let { list ->
                    when (filter.sortBy) {
                        SortOption.SMART_MATCH -> list.sortedByDescending { it.matchResult.scorePercentage }
                        SortOption.NEAREST -> list // In a full GIS system, sort by distance
                        SortOption.SOONEST -> list.sortedBy { it.opportunity.startDate }
                        SortOption.HOURS_LOW_TO_HIGH -> list.sortedBy { it.opportunity.totalEstimatedHours }
                    }
                }
        }
    }

    fun getOpportunityDetails(oppId: Long): Flow<OpportunityWithDetails?> {
        return combine(
            oppDao.getOpportunityById(oppId),
            orgDao.getAllOrganizations(),
            currentUser,
            _currentUserId.flatMapLatest { savedDao.getSavedOpportunityIds(it) },
            _currentUserId.flatMapLatest { appDao.getApplication(oppId, it) }
        ) { opp, orgs, user, savedIds, userApp ->
            if (opp == null) return@combine null
            val org = orgs.firstOrNull { it.id == opp.orgId }
            val volunteer = user ?: SeedData.defaultVolunteer
            val match = MatchingEngine.calculateMatch(volunteer, opp)
            OpportunityWithDetails(
                opportunity = opp,
                organization = org,
                matchResult = match,
                isSaved = savedIds.contains(opp.id),
                userApplication = userApp
            )
        }
    }

    fun getVolunteerApplications(): Flow<List<ApplicationWithOpportunity>> {
        return combine(
            _currentUserId.flatMapLatest { appDao.getApplicationsByVolunteer(it) },
            oppDao.getAllOpportunities(),
            orgDao.getAllOrganizations()
        ) { apps, opps, orgs ->
            val oppMap = opps.associateBy { it.id }
            val orgMap = orgs.associateBy { it.id }
            apps.mapNotNull { app ->
                val opp = oppMap[app.opportunityId] ?: return@mapNotNull null
                val org = orgMap[opp.orgId]
                ApplicationWithOpportunity(
                    application = app,
                    opportunity = opp,
                    organization = org
                )
            }
        }
    }

    fun getApplicationsForOpportunity(oppId: Long): Flow<List<ApplicationWithOpportunity>> {
        return combine(
            appDao.getApplicationsByOpportunity(oppId),
            oppDao.getOpportunityById(oppId),
            userDao.getAllUsers()
        ) { apps, opp, users ->
            if (opp == null) return@combine emptyList()
            val userMap = users.associateBy { it.id }
            apps.map { app ->
                ApplicationWithOpportunity(
                    application = app,
                    opportunity = opp,
                    organization = null,
                    volunteerUser = userMap[app.volunteerUserId]
                )
            }
        }
    }

    fun getOpportunitiesByOrganizer(adminUserId: Long): Flow<List<OpportunityEntity>> {
        return orgDao.getOrganizationByAdminId(adminUserId).flatMapLatest { org ->
            if (org != null) {
                oppDao.getOpportunitiesByOrg(org.id)
            } else {
                flowOf(emptyList())
            }
        }
    }

    fun getOrganizationForAdmin(adminUserId: Long): Flow<OrganizationEntity?> {
        return orgDao.getOrganizationByAdminId(adminUserId)
    }

    suspend fun applyForOpportunity(oppId: Long, note: String): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = _currentUserId.value
        val existing = appDao.getApplication(oppId, userId).firstOrNull()
        if (existing != null) {
            return@withContext Result.failure(Exception("Already applied for this opportunity"))
        }

        appDao.insertApplication(
            ApplicationEntity(
                opportunityId = oppId,
                volunteerUserId = userId,
                status = ApplicationStatus.APPLIED,
                applicationNote = note,
                appliedAt = System.currentTimeMillis()
            )
        )
        Result.success(Unit)
    }

    suspend fun cancelApplication(applicationId: Long) = withContext(Dispatchers.IO) {
        appDao.deleteApplication(applicationId)
    }

    suspend fun toggleSaveOpportunity(oppId: Long) = withContext(Dispatchers.IO) {
        val userId = _currentUserId.value
        val savedIds = savedDao.getSavedOpportunityIds(userId).firstOrNull() ?: emptyList()
        if (savedIds.contains(oppId)) {
            savedDao.removeSavedOpportunity(userId, oppId)
        } else {
            savedDao.saveOpportunity(SavedOpportunityEntity(volunteerUserId = userId, opportunityId = oppId))
        }
    }

    suspend fun updateApplicationStatus(
        applicationId: Long,
        newStatus: ApplicationStatus,
        feedback: String = "",
        hours: Double = 0.0
    ) = withContext(Dispatchers.IO) {
        val app = appDao.getApplicationById(applicationId).firstOrNull() ?: return@withContext
        appDao.updateStatus(applicationId, newStatus, System.currentTimeMillis(), hours)

        if (newStatus == ApplicationStatus.ACCEPTED) {
            oppDao.incrementAcceptedCount(app.opportunityId)
        } else if (newStatus == ApplicationStatus.COMPLETED && hours > 0) {
            userDao.addCompletedHours(app.volunteerUserId, hours)
        }
    }

    suspend fun createOpportunity(opp: OpportunityEntity): Long = withContext(Dispatchers.IO) {
        oppDao.insertOpportunity(opp)
    }

    suspend fun updateOpportunity(opp: OpportunityEntity) = withContext(Dispatchers.IO) {
        oppDao.updateOpportunity(opp)
    }

    suspend fun updateUserProfile(updatedUser: UserEntity) = withContext(Dispatchers.IO) {
        userDao.updateUser(updatedUser)
    }

    suspend fun updateOrganization(org: OrganizationEntity) = withContext(Dispatchers.IO) {
        orgDao.updateOrganization(org)
    }
}

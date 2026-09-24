package com.example.data.model

data class OpportunityWithDetails(
    val opportunity: OpportunityEntity,
    val organization: OrganizationEntity?,
    val matchResult: MatchResult,
    val isSaved: Boolean = false,
    val userApplication: ApplicationEntity? = null
)

data class ApplicationWithOpportunity(
    val application: ApplicationEntity,
    val opportunity: OpportunityEntity,
    val organization: OrganizationEntity?,
    val volunteerUser: UserEntity? = null
)

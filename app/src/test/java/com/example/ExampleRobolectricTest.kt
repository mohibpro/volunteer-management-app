package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SeedData
import com.example.data.model.MatchingEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ServeSync", appName)
  }

  @Test
  fun `verify matching engine score for volunteer and opportunity`() {
    val volunteer = SeedData.defaultVolunteer
    val treePlantationOpp = SeedData.sampleOpportunities[0]
    val matchResult = MatchingEngine.calculateMatch(volunteer, treePlantationOpp, distanceKm = 10.0)

    // Bilal has Environment cause and Logistics & Driving skill
    assertTrue(matchResult.scorePercentage >= 70)
    assertTrue(matchResult.causeMatched)
    assertTrue(matchResult.matchReasons.isNotEmpty())
  }

  @Test
  fun `verify Pakistani organizations and cities in mock data`() {
    val orgNames = SeedData.sampleOrganizations.map { it.name }
    assertTrue(orgNames.any { it.contains("Alkhidmat", ignoreCase = true) })
    assertTrue(orgNames.any { it.contains("Edhi", ignoreCase = true) })
    assertTrue(orgNames.any { it.contains("Saylani", ignoreCase = true) })
    assertTrue(orgNames.any { it.contains("Indus Hospital", ignoreCase = true) })
    assertTrue(orgNames.any { it.contains("TCF", ignoreCase = true) })
    assertTrue(orgNames.any { it.contains("Chhipa", ignoreCase = true) })

    val cities = MatchingEngine.PAKISTANI_CITIES
    assertTrue(cities.contains("Karachi"))
    assertTrue(cities.contains("Lahore"))
    assertTrue(cities.contains("Islamabad"))
    assertTrue(cities.contains("Rawalpindi"))
    assertTrue(cities.contains("Peshawar"))
    assertTrue(cities.contains("Multan"))
    assertTrue(cities.contains("Quetta"))

    // Verify volunteer phone number has Pakistani country code
    assertTrue(SeedData.defaultVolunteer.phone.startsWith("+92"))
  }
}

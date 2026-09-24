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
    val coastalOpp = SeedData.sampleOpportunities[0]
    val matchResult = MatchingEngine.calculateMatch(volunteer, coastalOpp, distanceKm = 10.0)

    // Alex has Environment cause and Logistics & Driving skill
    assertTrue(matchResult.scorePercentage >= 70)
    assertTrue(matchResult.causeMatched)
    assertTrue(matchResult.matchReasons.isNotEmpty())
  }
}

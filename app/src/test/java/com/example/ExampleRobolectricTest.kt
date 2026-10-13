package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AgriDatabase
import com.example.data.local.ProblemEntity
import com.example.data.repository.AgriRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: AgriDatabase
    private lateinit var repository: AgriRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = AgriDatabase.getInstance(context)
        repository = AgriRepository(database.agriDao())
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AgriConnect", appName)
    }

    @Test
    fun `test fee calculation matches marketplace specifications`() = runBlocking {
        repository.seedDatabaseIfEmpty()
        val fee = repository.calculateFees(300.0)

        // Consultation: ₹300, Farmer fee: ₹30 -> Farmer pays: ₹330
        assertEquals(300.0, fee.consultationPrice, 0.01)
        assertEquals(30.0, fee.farmerFee, 0.01)
        assertEquals(330.0, fee.totalFarmerPaid, 0.01)

        // Expert fee: ₹30 -> Expert receives: ₹270
        assertEquals(30.0, fee.expertFee, 0.01)
        assertEquals(270.0, fee.expertEarnings, 0.01)

        // Platform revenue: ₹60
        assertEquals(60.0, fee.platformRevenue, 0.01)
    }

    @Test
    fun `test matching algorithm recommends plant pathologist for paddy blast`() = runBlocking {
        repository.seedDatabaseIfEmpty()
        val problem = ProblemEntity(
            id = "test_prob",
            farmerId = "farmer_1",
            crop = "Paddy",
            cropVariety = "Basmati",
            location = "Ludhiana, Punjab",
            category = "Plant Pathology (Diseases)",
            description = "Spindle lesions on leaves",
            startedWhen = "2 days ago",
            symptoms = "Yellowing, lesions",
            previousTreatment = "None",
            urgency = "High",
            status = "OPEN"
        )

        val matches = repository.matchExpertsForProblem(problem)
        assertTrue("Matches should not be empty", matches.isNotEmpty())

        val topMatch = matches.first()
        // Top match should be Dr. Rahul Sharma (Plant Pathology & Paddy specialist in Punjab)
        assertEquals("expert_1", topMatch.expert.id)
        assertTrue("Match score should be high (>85%)", topMatch.matchScore >= 85)
        assertTrue("Match reasons should include category or crop", topMatch.matchReasons.isNotEmpty())
    }
}

package com.qibla.compass.ui

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.qibla.compass.data.AccuracyLevel
import com.qibla.compass.data.LocationData
import com.qibla.compass.data.QiblaResult
import com.qibla.compass.data.QiblaState
import com.qibla.compass.domain.QiblaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class QiblaScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var mockRepository: QiblaRepository
    private lateinit var qiblaStateFlow: MutableStateFlow<QiblaState>
    private lateinit var azimuthFlow: MutableStateFlow<Float>

    private fun setupMocks(initialState: QiblaState = QiblaState.Loading) {
        mockRepository = mockk(relaxed = true)
        qiblaStateFlow = MutableStateFlow(initialState)
        azimuthFlow = MutableStateFlow(0f)

        coEvery { mockRepository.qiblaFlow } returns qiblaStateFlow.asStateFlow()
        coEvery { mockRepository.deviceAzimuthFlow } returns azimuthFlow.asStateFlow()
        coEvery { mockRepository.hasLocationPermission() } returns true
    }

    private fun launchScreen() {
        composeRule.setContent {
            QiblaScreen(viewModel = QiblaViewModel(mockRepository))
        }
    }

    @Test
    fun showsLoadingIndicatorWhenStateIsLoading() {
        setupMocks()
        launchScreen()

        composeRule.onNodeWithText("جاري تحديد الموقع…").assertExists()
    }

    @Test
    fun showsQiblaDirectionWhenStateIsSuccess() {
        val location = LocationData(
            latitude = 24.7136,
            longitude = 46.6753,
            accuracy = 10f,
            timestamp = System.currentTimeMillis(),
            provider = "gps"
        )

        val result = QiblaResult(
            direction = 270.0,
            distanceKm = 750.0,
            userLocation = location
        )

        setupMocks(QiblaState.Success(result))
        launchScreen()

        composeRule.onNodeWithText("غرب").assertExists()
        composeRule.onNodeWithText("270.0°").assertExists()
        composeRule.onNodeWithText("750.0 كم").assertExists()
        composeRule.onNodeWithText("HIGH").assertExists()
    }

    @Test
    fun showsRefreshAndCalibrateButtons() {
        val location = LocationData(0.0, 0.0, 10f, 0, "gps")
        val result = QiblaResult(0.0, 0.0, location)
        setupMocks(QiblaState.Success(result))

        launchScreen()

        composeRule.onNodeWithText("تحديث الموقع").assertExists()
        composeRule.onNodeWithText("معايرة البوصلة").assertExists()
    }

    @Test
    fun showsDistanceAndAccuracyInfo() {
        val location = LocationData(
            latitude = 30.0444,
            longitude = 31.2357,
            accuracy = 30f,
            timestamp = System.currentTimeMillis(),
            provider = "network"
        )

        val result = QiblaResult(
            direction = 135.0,
            distanceKm = 1200.0,
            userLocation = location
        )

        setupMocks(QiblaState.Success(result))
        launchScreen()

        composeRule.onNodeWithText("1200.0 كم").assertExists()
        composeRule.onNodeWithText("MEDIUM").assertExists()
        composeRule.onNodeWithText("جنوب شرق").assertExists()
    }
}

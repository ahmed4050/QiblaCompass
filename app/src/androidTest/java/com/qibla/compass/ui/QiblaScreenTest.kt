package com.qibla.compass.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertDoesNotExist
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
import org.junit.rules.TestRule

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

        coEvery { mockRepository.getQiblaFlow() } returns qiblaStateFlow.asStateFlow()
        coEvery { mockRepository.getDeviceAzimuthFlow() } returns azimuthFlow.asStateFlow()
        coEvery { mockRepository.hasLocationPermission() } returns true
    }

    @Test
    fun `shows loading indicator when state is loading`() = composeRule.runOnCompose {
        setupMocks()
        composeRule.setContent {
            QiblaScreen(viewModel = QiblaViewModel(mockRepository))
        }

        composeRule.onNodeWithText("جاري تحديد الموقع…").assertExists()
    }

    @Test
    fun `shows qibla direction when state is success`() = composeRule.runOnCompose {
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
        composeRule.setContent {
            QiblaScreen(viewModel = QiblaViewModel(mockRepository))
        }

        composeRule.onNodeWithText("غرب").assertExists()
        composeRule.onNodeWithText("270.0°").assertExists()
        composeRule.onNodeWithText("750 كم").assertExists()
        composeRule.onNodeWithText("عالية").assertExists()
    }

    @Test
    fun `shows error message when state is error`() = composeRule.runOnCompose {
        setupMocks(QiblaState.Error("تعذر الحصول على الموقع", null))
        composeRule.setContent {
            QiblaScreen(viewModel = QiblaViewModel(mockRepository))
        }

        composeRule.onNodeWithText("تعذر الحصول على الموقع").assertExists()
        composeRule.onNodeWithText("إعادة المحاولة").assertExists()
    }

    @Test
    fun `shows permission denied state`() = composeRule.runOnCompose {
        setupMocks(QiblaState.PermissionDenied)
        composeRule.setContent {
            QiblaScreen(viewModel = QiblaViewModel(mockRepository))
        }

        // يتحقق من وجود رسالة خطأ الأذونات
        composeRule.onNodeWithText("تم رفض إذن الموقع").assertExists()
    }

    @Test
    fun `shows refresh and calibrate buttons`() = composeRule.runOnCompose {
        val location = LocationData(0.0, 0.0, 10f, 0, "gps")
        val result = QiblaResult(0.0, 0.0, location)
        setupMocks(QiblaState.Success(result))

        composeRule.setContent {
            QiblaScreen(viewModel = QiblaViewModel(mockRepository))
        }

        composeRule.onNodeWithText("تحديث الموقع").assertExists()
        composeRule.onNodeWithText("معايرة البوصلة").assertExists()
    }

    @Test
    fun `shows distance and accuracy info`() = composeRule.runOnCompose {
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
        composeRule.setContent {
            QiblaScreen(viewModel = QiblaViewModel(mockRepository))
        }

        composeRule.onNodeWithText("1200 كم").assertExists()
        composeRule.onNodeWithText("متوسطة").assertExists()
        composeRule.onNodeWithText("جنوب شرق").assertExists()
    }

    @Test
    fun `shows coordinates in location card`() = composeRule.runOnCompose {
        val location = LocationData(
            latitude = 21.422508,
            longitude = 39.826184,
            accuracy = 5f,
            timestamp = System.currentTimeMillis(),
            provider = "gps"
        )

        val result = QiblaResult(
            direction = 0.0,
            distanceKm = 0.5,
            userLocation = location
        )

        setupMocks(QiblaState.Success(result))
        composeRule.setContent {
            QiblaScreen(viewModel = QiblaViewModel(mockRepository))
        }

        composeRule.onNodeWithText("21.422508°").assertExists()
        composeRule.onNodeWithText("39.826184°").assertExists()
    }
}
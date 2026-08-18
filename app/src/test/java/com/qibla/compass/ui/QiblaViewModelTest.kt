package com.qibla.compass.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.qibla.compass.data.AccuracyLevel
import com.qibla.compass.data.LocationData
import com.qibla.compass.data.QiblaResult
import com.qibla.compass.data.QiblaState
import com.qibla.compass.domain.QiblaRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class QiblaViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockRepository: QiblaRepository
    private lateinit var viewModel: QiblaViewModel
    private lateinit var qiblaStateFlow: MutableStateFlow<QiblaState>
    private lateinit var azimuthFlow: MutableStateFlow<Float>

    @Before
    fun setup() {
        mockRepository = mockk(relaxed = true)

        qiblaStateFlow = MutableStateFlow(QiblaState.Loading)
        azimuthFlow = MutableStateFlow(0f)

        coEvery { mockRepository.getQiblaFlow() } returns qiblaStateFlow.asStateFlow()
        coEvery { mockRepository.getDeviceAzimuthFlow() } returns azimuthFlow.asStateFlow()
        coEvery { mockRepository.hasLocationPermission() } returns true

        viewModel = QiblaViewModel(mockRepository)
    }

    @Test
    fun `initial state should be loading`() = runBlockingTest {
        assertEquals(QiblaState.Loading, viewModel.qiblaState)
        assertEquals(0f, viewModel.deviceAzimuth, 0.001f)
    }

    @Test
    fun `should update qiblaDirection when repository emits success`() = runBlockingTest {
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

        qiblaStateFlow.value = QiblaState.Success(result)

        assertEquals(270.0, viewModel.qiblaDirection, 0.001)
        assertEquals(750.0, viewModel.distanceToKaaba, 0.001)
        assertEquals("غرب", viewModel.directionText)
        assertEquals(AccuracyLevel.HIGH, viewModel.accuracyLevel)
        assertEquals(location, viewModel.userLocation)
    }

    @Test
    fun `should update deviceAzimuth when repository emits azimuth`() = runBlockingTest {
        azimuthFlow.value = 90f

        assertEquals(90f, viewModel.deviceAzimuth, 0.001f)
    }

    @Test
    fun `should calculate relativeQiblaAngle correctly`() = runBlockingTest {
        val location = LocationData(0.0, 0.0, 10f, 0, "gps")
        val result = QiblaResult(direction = 180.0, distanceKm = 100.0, userLocation = location)

        qiblaStateFlow.value = QiblaState.Success(result)
        azimuthFlow.value = 90f // الجهاز يشير شرقاً

        // القبلة 180 (جنوب) - الجهاز 90 (شرق) = 90 درجة (يمين)
        val relative = viewModel.relativeQiblaAngle
        assertEquals(90f, relative, 1.0f)
    }

    @Test
    fun `should handle wrap around for relative angle`() = runBlockingTest {
        val location = LocationData(0.0, 0.0, 10f, 0, "gps")
        val result = QiblaResult(direction = 10.0, distanceKm = 100.0, userLocation = location)

        qiblaStateFlow.value = QiblaState.Success(result)
        azimuthFlow.value = 350f // الجهاز يشير شمال غرب

        // 10 - 350 = -340 -> +360 = 20 درجة
        val relative = viewModel.relativeQiblaAngle
        assertEquals(20f, relative, 1.0f)
    }

    @Test
    fun `should call repository.requestLocationUpdate on refreshLocation`() = runBlockingTest {
        viewModel.refreshLocation()

        verify { mockRepository.requestLocationUpdate() }
    }

    @Test
    fun `should call repository.startCompassUpdates on init`() = runBlockingTest {
        verify { mockRepository.startCompassUpdates() }
    }

    @Test
    fun `should call repository.stopCompassUpdates on cleared`() = runBlockingTest {
        viewModel.onCleared()

        verify { mockRepository.stopCompassUpdates() }
    }

    @Test
    fun `should set isCompassCalibrated to true on calibration`() = runBlockingTest {
        viewModel.onCalibrationComplete()

        assertTrue(viewModel.isCompassCalibrated)
    }

    @Test
    fun `should show error state when repository emits error`() = runBlockingTest {
        qiblaStateFlow.value = QiblaState.Error("Location unavailable", null)

        assertTrue(viewModel.hasError)
        assertEquals("Location unavailable", viewModel.errorMessage)
    }

    @Test
    fun `should show permission denied state`() = runBlockingTest {
        qiblaStateFlow.value = QiblaState.PermissionDenied

        assertTrue(viewModel.isPermissionDenied)
    }

    @Test
    fun `should show location unavailable state`() = runBlockingTest {
        qiblaStateFlow.value = QiblaState.LocationUnavailable

        assertTrue(viewModel.isLocationUnavailable)
    }

    @Test
    fun `isLoading should be true only for Loading state`() = runBlockingTest {
        assertTrue(viewModel.isLoading)

        qiblaStateFlow.value = QiblaState.Error("error", null)
        assertFalse(viewModel.isLoading)

        val location = LocationData(0.0, 0.0, 10f, 0, "gps")
        qiblaStateFlow.value = QiblaState.Success(QiblaResult(0.0, 0.0, location))
        assertFalse(viewModel.isLoading)
    }
}
package com.qibla.compass.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qibla.compass.data.AccuracyLevel
import com.qibla.compass.data.LocationData
import com.qibla.compass.data.QiblaResult
import com.qibla.compass.data.QiblaState
import com.qibla.compass.domain.QiblaRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val repository: QiblaRepository
) : ViewModel() {

    // حالة واجهة المستخدم
    var qiblaState by mutableStateOf<QiblaState>(QiblaState.Loading)
        private set

    var deviceAzimuth by mutableStateOf(0f)
        private set

    var isCompassCalibrated by mutableStateOf(false)
        private set

    init {
        observeQibla()
        observeDeviceAzimuth()
        repository.startCompassUpdates()
        requestLocation()
    }

    private fun observeQibla() {
        viewModelScope.launch {
            repository.getQiblaFlow().collect { state ->
                qiblaState = state
            }
        }
    }

    private fun observeDeviceAzimuth() {
        viewModelScope.launch {
            repository.getDeviceAzimuthFlow().collect { azimuth ->
                deviceAzimuth = azimuth
            }
        }
    }

    fun requestLocation() {
        viewModelScope.launch {
            repository.requestLocationUpdate()
        }
    }

    fun refreshLocation() {
        requestLocation()
    }

    fun onCalibrationComplete() {
        isCompassCalibrated = true
    }

    // خصائص مساعدة للـ UI
    val qiblaDirection: Double
        get() = (qiblaState as? QiblaState.Success)?.result?.direction ?: 0.0

    val distanceToKaaba: Double
        get() = (qiblaState as? QiblaState.Success)?.result?.distanceKm ?: 0.0

    val userLocation: LocationData?
        get() = (qiblaState as? QiblaState.Success)?.result?.userLocation

    val accuracyLevel: AccuracyLevel?
        get() = (qiblaState as? QiblaState.Success)?.result?.accuracyLevel

    val directionText: String
        get() = (qiblaState as? QiblaState.Success)?.result?.directionText ?: "---"

    val errorMessage: String?
        get() = (qiblaState as? QiblaState.Error)?.message

    val isLoading: Boolean
        get() = qiblaState == QiblaState.Loading

    val hasError: Boolean
        get() = qiblaState is QiblaState.Error

    val isPermissionDenied: Boolean
        get() = qiblaState == QiblaState.PermissionDenied

    val isLocationUnavailable: Boolean
        get() = qiblaState == QiblaState.LocationUnavailable

    // حساب الزاوية النسبية (القبلة - اتجاه الجهاز)
    val relativeQiblaAngle: Float
        get() {
            val qibla = qiblaDirection.toFloat()
            var diff = qibla - deviceAzimuth
            diff = (diff + 360) % 360
            return diff
        }

    override fun onCleared() {
        super.onCleared()
        repository.stopCompassUpdates()
    }
}
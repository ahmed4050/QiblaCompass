package com.qibla.compass.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.qibla.compass.data.QiblaCalculator
import com.qibla.compass.data.QiblaResult
import com.qibla.compass.data.QiblaState
import com.qibla.compass.domain.QiblaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sharingStarted
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QiblaRepositoryImpl @Inject constructor(
    private val context: Context,
    private val scope: CoroutineScope
) : QiblaRepository, SensorEventListener, LocationListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val azimuthChannel = Channel<Float>(capacity = 1)
    private val _qiblaState = MutableStateFlow<QiblaState>(QiblaState.Loading)
    private var lastLocation: LocationData? = null
    private var lastAzimuth: Float = 0f
    private var isCompassActive = false

    override val getQiblaFlow = _qiblaState
        .asStateFlow()

    override val getDeviceAzimuthFlow = azimuthChannel
        .receiveAsFlow()
        .stateIn(scope, sharingStarted.WhileSubscribed(), 0f)

    override suspend fun requestLocationUpdate() {
        if (hasLocationPermission()) {
            try {
                locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER,
                    this,
                    null
                )
                locationManager.requestSingleUpdate(
                    LocationManager.NETWORK_PROVIDER,
                    this,
                    null
                )
            } catch (e: SecurityException) {
                _qiblaState.value = QiblaState.PermissionDenied
            }
        } else {
            _qiblaState.value = QiblaState.PermissionDenied
        }
    }

    override suspend fun getLastKnownLocation(): LocationData? {
        return lastLocation
    }

    override fun hasLocationPermission(): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestLocationPermissions(): Boolean {
        // يتم التعامل مع الأذونات في الـ Activity
        return hasLocationPermission()
    }

    override fun startCompassUpdates() {
        if (!isCompassActive) {
            val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

            if (accelerometerSensor != null && magnetometerSensor != null) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
                sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_UI)
                isCompassActive = true
            }
        }
    }

    override fun stopCompassUpdates() {
        if (isCompassActive) {
            sensorManager.unregisterListener(this)
            isCompassActive = false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values.clone()
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetometerValues = event.values.clone()
        }

        if (accelerometerValues != null && magnetometerValues != null) {
            val rotationMatrix = FloatArray(9)
            val inclinationMatrix = FloatArray(9)
            val success = SensorManager.getRotationMatrix(
                rotationMatrix, inclinationMatrix,
                accelerometerValues, magnetometerValues
            )

            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)
                val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                val normalizedAzimuth = (azimuth + 360) % 360

                lastAzimuth = normalizedAzimuth
                scope.launch { azimuthChannel.send(normalizedAzimuth) }

                // تحديث القبلة إذا كان لدينا موقع
                lastLocation?.let { location ->
                    updateQiblaDirection(location, normalizedAzimuth)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // يمكن التعامل مع تغيير دقة المستشعر هنا
    }

    override fun onLocationChanged(location: Location) {
        val locationData = LocationData(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy,
            timestamp = location.time,
            provider = location.provider
        )
        lastLocation = locationData

        updateQiblaDirection(locationData, lastAzimuth)
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: android.os.Bundle?) {}

    private var accelerometerValues: FloatArray? = null
    private var magnetometerValues: FloatArray? = null

    private fun updateQiblaDirection(location: LocationData, deviceAzimuth: Float) {
        val qiblaDirection = QiblaCalculator.calculateQiblaDirection(
            location.latitude, location.longitude
        )

        val distance = QiblaCalculator.calculateDistanceToKaaba(
            location.latitude, location.longitude
        )

        val result = QiblaResult(
            direction = qiblaDirection,
            distanceKm = distance,
            userLocation = location
        )

        _qiblaState.value = QiblaState.Success(result)
    }

    fun shutdown() {
        stopCompassUpdates()
        azimuthChannel.close()
    }
}
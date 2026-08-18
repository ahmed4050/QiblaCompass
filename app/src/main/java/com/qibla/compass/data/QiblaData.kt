package com.qibla.compass.data

import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long,
    val provider: String
)

@Serializable
data class QiblaResult(
    val direction: Double,           // زاوية القبلة بالدرجات (0-360)
    val distanceKm: Double,          // المسافة للكعبة بالكيلومترات
    val userLocation: LocationData,  // موقع المستخدم
    val kaabaLatitude: Double = 21.422508,
    val kaabaLongitude: Double = 39.826184,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * نص اتجاه القبلة للعرض
     */
    val directionText: String
        get() = when {
            direction < 22.5 || direction >= 337.5 -> "شمال"
            direction < 67.5 -> "شمال شرق"
            direction < 112.5 -> "شرق"
            direction < 157.5 -> "جنوب شرق"
            direction < 202.5 -> "جنوب"
            direction < 247.5 -> "جنوب غرب"
            direction < 292.5 -> "غرب"
            else -> "شمال غرب"
        }

    /**
     * دقة التوجيه
     */
    val accuracyLevel: AccuracyLevel
        get() = when {
            userLocation.accuracy <= 10f -> AccuracyLevel.HIGH
            userLocation.accuracy <= 50f -> AccuracyLevel.MEDIUM
            else -> AccuracyLevel.LOW
        }
}

enum class AccuracyLevel {
    HIGH, MEDIUM, LOW
}

sealed interface QiblaState {
    data class Success(val result: QiblaResult) : QiblaState
    data class Error(val message: String, val throwable: Throwable? = null) : QiblaState
    object Loading : QiblaState
    object PermissionDenied : QiblaState
    object LocationUnavailable : QiblaState
}
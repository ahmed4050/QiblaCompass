package com.qibla.compass.domain

import com.qibla.compass.data.LocationData
import com.qibla.compass.data.QiblaResult
import com.qibla.compass.data.QiblaState
import kotlinx.coroutines.flow.Flow

interface QiblaRepository {
    /**
     * الحصول على تدفق حالة القبلة
     */
    val qiblaFlow: Flow<QiblaState>

    /**
     * طلب تحديث الموقع يدوياً
     */
    suspend fun requestLocationUpdate()

    /**
     * الحصول على آخر موقع معروف
     */
    suspend fun getLastKnownLocation(): LocationData?

    /**
     * التحقق من توفر الأذونات
     */
    fun hasLocationPermission(): Boolean

    /**
     * طلب أذونات الموقع
     */
    suspend fun requestLocationPermissions(): Boolean

    /**
     * بدء تتبع البوصلة
     */
    fun startCompassUpdates()

    /**
     * إيقاف تتبع البوصلة
     */
    fun stopCompassUpdates()

    /**
     * الحصول على تدفق اتجاه الجهاز (azimuth)
     */
    val deviceAzimuthFlow: Flow<Float>
}
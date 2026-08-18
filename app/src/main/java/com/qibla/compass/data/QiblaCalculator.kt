package com.qibla.compass.data

import kotlin.math.*

/**
 * حاسبة اتجاه القبلة باستخدام صيغة الدائرة العظمى (Great Circle Formula)
 * تحسب الزاوية من الموقع الحالي إلى الكعبة المشرفة في مكة المكرمة
 */
object QiblaCalculator {

    // إحداثيات الكعبة المشرفة
    private const val KAABA_LATITUDE = 21.422508
    private const val KAABA_LONGITUDE = 39.826184

    /**
     * حساب اتجاه القبلة من إحداثيات معينة
     * @param userLatitude خط عرض المستخدم
     * @param userLongitude خط طول المستخدم
     * @return الزاوية بالدرجات (0-360) حيث 0 = الشمال
     */
    fun calculateQiblaDirection(
        userLatitude: Double,
        userLongitude: Double
    ): Double {
        val lat1 = Math.toRadians(userLatitude)
        val lon1 = Math.toRadians(userLongitude)
        val lat2 = Math.toRadians(KAABA_LATITUDE)
        val lon2 = Math.toRadians(KAABA_LONGITUDE)

        val deltaLon = lon2 - lon1

        val y = sin(deltaLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLon)

        var bearing = Math.toDegrees(atan2(y, x))

        // تحويل إلى 0-360 درجة
        bearing = (bearing + 360) % 360

        return bearing
    }

    /**
     * حساب المسافة إلى الكعبة (بالكيلومترات)
     */
    fun calculateDistanceToKaaba(
        userLatitude: Double,
        userLongitude: Double
    ): Double {
        val R = 6371.0 // نصف قطر الأرض بالكيلومترات

        val lat1 = Math.toRadians(userLatitude)
        val lon1 = Math.toRadians(userLongitude)
        val lat2 = Math.toRadians(KAABA_LATITUDE)
        val lon2 = Math.toRadians(KAABA_LONGITUDE)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    /**
     * الحصول على إحداثيات الكعبة
     */
    fun getKaabaCoordinates(): Pair<Double, Double> {
        return KAABA_LATITUDE to KAABA_LONGITUDE
    }
}
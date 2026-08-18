package com.qibla.compass.data

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class QiblaCalculatorTest {

    private const val EPSILON = 0.01

    @Test
    fun `calculateQiblaDirection from Mecca should be 0 (or 360)`() {
        // الكعبة في مكة - الاتجاه يجب أن يكون غير محدد أو 0
        val direction = QiblaCalculator.calculateQiblaDirection(
            userLatitude = 21.422508,
            userLongitude = 39.826184
        )

        // في نفس الموقع، الاتجاه غير معرف، لكن الخوارزمية تعطي قيمة
        assertTrue("Direction should be valid", direction >= 0.0 && direction <= 360.0)
    }

    @Test
    fun `calculateQiblaDirection from Jeddah (west of Mecca) should be East`() {
        // جدة غرب مكة - القبلة يجب أن تكون شرقية (~90 درجة)
        val direction = QiblaCalculator.calculateQiblaDirection(
            userLatitude = 21.4858,
            userLongitude = 39.1925
        )

        assertEquals(90.0, direction, 5.0) // تقريباً شرق
    }

    @Test
    fun `calculateQiblaDirection from Medina (north of Mecca) should be South`() {
        // المدينة شمال مكة - القبلة يجب أن تكون جنوبية (~180 درجة)
        val direction = QiblaCalculator.calculateQiblaDirection(
            userLatitude = 24.5247,
            userLongitude = 39.5692
        )

        assertEquals(180.0, direction, 5.0) // تقريباً جنوب
    }

    @Test
    fun `calculateQiblaDirection from Riyadh (east of Mecca) should be West`() {
        // الرياض شرق مكة - القبلة يجب أن تكون غربية (~270 درجة)
        val direction = QiblaCalculator.calculateQiblaDirection(
            userLatitude = 24.7136,
            userLongitude = 46.6753
        )

        assertEquals(270.0, direction, 5.0) // تقريباً غرب
    }

    @Test
    fun `calculateQiblaDirection from Cairo (north-west of Mecca) should be South-East`() {
        // القاهرة شمال غرب مكة - القبلة يجب أن تكون جنوب شرق (~135 درجة)
        val direction = QiblaCalculator.calculateQiblaDirection(
            userLatitude = 30.0444,
            userLongitude = 31.2357
        )

        assertEquals(135.0, direction, 5.0)
    }

    @Test
    fun `calculateQiblaDirection from Istanbul (north of Mecca) should be South`() {
        // إسطنبول شمال مكة - القبلة جنوبية
        val direction = QiblaCalculator.calculateQiblaDirection(
            userLatitude = 41.0082,
            userLongitude = 28.9784
        )

        assertEquals(180.0, direction, 10.0)
    }

    @Test
    fun `calculateQiblaDirection from Jakarta (south-east of Mecca) should be North-West`() {
        // جاكرتا جنوب شرق مكة - القبلة شمال غرب (~315 درجة)
        val direction = QiblaCalculator.calculateQiblaDirection(
            userLatitude = -6.2088,
            userLongitude = 106.8456
        )

        assertEquals(315.0, direction, 10.0)
    }

    @Test
    fun `calculateQiblaDirection from London (north-west of Mecca) should be South-East`() {
        // لندن شمال غرب مكة
        val direction = QiblaCalculator.calculateQiblaDirection(
            userLatitude = 51.5074,
            userLongitude = -0.1278
        )

        assertEquals(120.0, direction, 10.0)
    }

    @Test
    fun `calculateQiblaDirection from New York (north-west of Mecca) should be North-East`() {
        // نيويورك شمال غرب مكة - القبلة شمال شرق (~55 درجة)
        val direction = QiblaCalculator.calculateQiblaDirection(
            userLatitude = 40.7128,
            userLongitude = -74.0060
        )

        assertEquals(55.0, direction, 10.0)
    }

    @Test
    fun `calculateDistanceToKaaba from Mecca should be near zero`() {
        val distance = QiblaCalculator.calculateDistanceToKaaba(
            userLatitude = 21.422508,
            userLongitude = 39.826184
        )

        assertTrue("Distance should be near zero", distance < 1.0)
    }

    @Test
    fun `calculateDistanceToKaaba from Jeddah should be approx 70km`() {
        val distance = QiblaCalculator.calculateDistanceToKaaba(
            userLatitude = 21.4858,
            userLongitude = 39.1925
        )

        assertEquals(70.0, distance, 10.0)
    }

    @Test
    fun `calculateDistanceToKaaba from Riyadh should be approx 750km`() {
        val distance = QiblaCalculator.calculateDistanceToKaaba(
            userLatitude = 24.7136,
            userLongitude = 46.6753
        )

        assertEquals(750.0, distance, 50.0)
    }

    @Test
    fun `calculateDistanceToKaaba from Cairo should be approx 1200km`() {
        val distance = QiblaCalculator.calculateDistanceToKaaba(
            userLatitude = 30.0444,
            userLongitude = 31.2357
        )

        assertEquals(1200.0, distance, 100.0)
    }

    @Test
    fun `getKaabaCoordinates should return correct values`() {
        val (lat, lon) = QiblaCalculator.getKaabaCoordinates()

        assertEquals(21.422508, lat, 0.000001)
        assertEquals(39.826184, lon, 0.000001)
    }

    @Test
    fun `direction should always be normalized to 0-360`() {
        val testLocations = listOf(
            21.422508 to 39.826184,  // مكة
            24.7136 to 46.6753,      // الرياض
            30.0444 to 31.2357,      // القاهرة
            41.0082 to 28.9784,      // إسطنبول
            -6.2088 to 106.8456,     // جاكرتا
            51.5074 to -0.1278,      // لندن
            40.7128 to -74.0060      // نيويورك
        )

        testLocations.forEach { (lat, lon) ->
            val direction = QiblaCalculator.calculateQiblaDirection(lat, lon)
            assertTrue("Direction $direction for ($lat, $lon) should be 0-360",
                direction >= 0.0 && direction < 360.0)
        }
    }

    @Test
    fun `QiblaResult directionText should match cardinal directions`() {
        val location = LocationData(
            latitude = 24.7136,
            longitude = 46.6753,
            accuracy = 10f,
            timestamp = System.currentTimeMillis(),
            provider = "gps"
        )

        // اختبار الاتجاهات النصية
        val resultNorth = QiblaResult(
            direction = 0.0,
            distanceKm = 100.0,
            userLocation = location
        )
        assertEquals("شمال", resultNorth.directionText)

        val resultNorthEast = QiblaResult(
            direction = 45.0,
            distanceKm = 100.0,
            userLocation = location
        )
        assertEquals("شمال شرق", resultNorthEast.directionText)

        val resultEast = QiblaResult(
            direction = 90.0,
            distanceKm = 100.0,
            userLocation = location
        )
        assertEquals("شرق", resultEast.directionText)

        val resultSouthEast = QiblaResult(
            direction = 135.0,
            distanceKm = 100.0,
            userLocation = location
        )
        assertEquals("جنوب شرق", resultSouthEast.directionText)

        val resultSouth = QiblaResult(
            direction = 180.0,
            distanceKm = 100.0,
            userLocation = location
        )
        assertEquals("جنوب", resultSouth.directionText)

        val resultSouthWest = QiblaResult(
            direction = 225.0,
            distanceKm = 100.0,
            userLocation = location
        )
        assertEquals("جنوب غرب", resultSouthWest.directionText)

        val resultWest = QiblaResult(
            direction = 270.0,
            distanceKm = 100.0,
            userLocation = location
        )
        assertEquals("غرب", resultWest.directionText)

        val resultNorthWest = QiblaResult(
            direction = 315.0,
            distanceKm = 100.0,
            userLocation = location
        )
        assertEquals("شمال غرب", resultNorthWest.directionText)
    }

    @Test
    fun `QiblaResult accuracyLevel should be correct`() {
        val locationHigh = LocationData(0.0, 0.0, 5f, 0, "gps")
        val locationMedium = LocationData(0.0, 0.0, 30f, 0, "network")
        val locationLow = LocationData(0.0, 0.0, 100f, 0, "network")

        val resultHigh = QiblaResult(0.0, 0.0, locationHigh)
        val resultMedium = QiblaResult(0.0, 0.0, locationMedium)
        val resultLow = QiblaResult(0.0, 0.0, locationLow)

        assertEquals(AccuracyLevel.HIGH, resultHigh.accuracyLevel)
        assertEquals(AccuracyLevel.MEDIUM, resultMedium.accuracyLevel)
        assertEquals(AccuracyLevel.LOW, resultLow.accuracyLevel)
    }
}
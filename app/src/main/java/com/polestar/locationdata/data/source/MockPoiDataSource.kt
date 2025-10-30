package com.polestar.locationdata.data.source

import com.polestar.locationdata.domain.model.*
import com.polestar.locationdata.domain.repository.PoiDataSource
import kotlin.random.Random

/**
 * Mock implementation of POI data source
 *
 * In real implementation, this would call external APIs:
 * - Google Places API
 * - OpenStreetMap Overpass API
 * - HERE Places API
 * - Custom Polestar backend
 */
class MockPoiDataSource : PoiDataSource {

    override fun fetchNearbyPois(
        location: Location,
        radiusMeters: Double,
        types: List<PoiType>
    ): List<PointOfInterest> {

        // TODO: Replace with real API call
        // Example implementation with Google Places API:
        /*
        val apiKey = "YOUR_API_KEY"
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
        val params = mapOf(
            "location" to "${location.latitude},${location.longitude}",
            "radius" to radiusMeters.toString(),
            "type" to types.joinToString("|") { it.name.lowercase() },
            "key" to apiKey
        )

        val response = httpClient.get(url) {
            params.forEach { (key, value) ->
                parameter(key, value)
            }
        }

        return response.body<PlacesApiResponse>().results.map { place ->
            PointOfInterest(
                id = place.place_id,
                name = place.name,
                type = mapToPoiType(place.types),
                location = Location(place.geometry.location.lat, place.geometry.location.lng),
                distance = calculateDistance(location, place.geometry.location),
                rating = place.rating,
                additionalInfo = mapOf(
                    "address" to place.vicinity,
                    "open_now" to place.opening_hours?.open_now.toString()
                )
            )
        }
        */

        // Mock data for demonstration
        return types.flatMap { type ->
            generateMockPois(location, type, radiusMeters)
        }
    }

    /**
     * Generate mock POIs for testing
     */
    private fun generateMockPois(
        location: Location,
        type: PoiType,
        radiusMeters: Double
    ): List<PointOfInterest> {
        val count = Random.nextInt(2, 6)

        return (1..count).map { i ->
            val mockLocation = generateNearbyLocation(location, radiusMeters)
            val distance = calculateDistance(location, mockLocation)

            PointOfInterest(
                name = generateMockName(type, i),
                type = type,
                location = mockLocation,
                distance = distance,
                rating = Random.nextDouble(3.0, 5.0),
                additionalInfo = generateMockInfo(type, i)
            )
        }
    }

    private fun generateMockName(type: PoiType, index: Int): String {
        return when (type) {
            PoiType.RESTAURANT -> listOf("Restaurant Bella", "The Gourmet Place", "Nordic Kitchen", "Fusion Bistro")[index % 4]
            PoiType.CHARGING_STATION -> "Polestar Charging Station $index"
            PoiType.PARKING -> "Parking Garage $index"
            PoiType.HOTEL -> listOf("Grand Hotel", "Comfort Inn", "Boutique Stay")[index % 3]
            PoiType.SHOPPING -> listOf("Shopping Mall", "Outlet Center", "Department Store")[index % 3]
            else -> "${type.name.replace('_', ' ')} $index"
        }
    }

    private fun generateMockInfo(type: PoiType, index: Int): Map<String, String> {
        return when (type) {
            PoiType.CHARGING_STATION -> mapOf(
                "power" to "${Random.nextInt(50, 350)}kW",
                "available_spots" to "${Random.nextInt(0, 8)}",
                "connector_types" to "CCS, Type 2"
            )
            PoiType.RESTAURANT -> mapOf(
                "cuisine" to listOf("Italian", "Asian", "Nordic", "American")[index % 4],
                "price_level" to "${"$".repeat(Random.nextInt(1, 4))}",
                "phone" to "+46 ${Random.nextInt(10, 99)} ${Random.nextInt(100, 999)} ${Random.nextInt(1000, 9999)}"
            )
            else -> mapOf(
                "address" to "Mock Street ${Random.nextInt(1, 100)}",
                "phone" to "+46 ${Random.nextInt(10, 99)} ${Random.nextInt(100, 999)} ${Random.nextInt(1000, 9999)}"
            )
        }
    }

    private fun generateNearbyLocation(center: Location, radiusMeters: Double): Location {
        // Rough conversion: 1 degree ≈ 111km at equator
        val offset = radiusMeters / 111000.0
        return Location(
            latitude = center.latitude + Random.nextDouble(-offset, offset),
            longitude = center.longitude + Random.nextDouble(-offset, offset)
        )
    }

    /**
     * Calculate distance between two locations using Haversine formula
     */
    private fun calculateDistance(loc1: Location, loc2: Location): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(loc2.latitude - loc1.latitude)
        val dLon = Math.toRadians(loc2.longitude - loc1.longitude)

        val lat1 = Math.toRadians(loc1.latitude)
        val lat2 = Math.toRadians(loc2.latitude)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadiusKm * c * 1000 // Convert to meters
    }
}

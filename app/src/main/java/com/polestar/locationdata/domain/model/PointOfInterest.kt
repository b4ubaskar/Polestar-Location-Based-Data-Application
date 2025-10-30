package com.polestar.locationdata.domain.model

import java.util.UUID

/**
 * Types of points of interest
 */
enum class PoiType {
    RESTAURANT,
    CHARGING_STATION,
    PARKING,
    HOTEL,
    SHOPPING,
    GAS_STATION,
    CAFE,
    ENTERTAINMENT
}

/**
 * Point of Interest near the car
 */
data class PointOfInterest(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: PoiType,
    val location: Location,
    val distance: Double, // in meters
    val rating: Double? = null,
    val additionalInfo: Map<String, String> = emptyMap()
) {
    init {
        require(name.isNotBlank()) { "POI name cannot be blank" }
        require(distance >= 0) { "Distance cannot be negative" }
        rating?.let { require(it in 0.0..5.0) { "Rating must be between 0 and 5" } }
    }
}

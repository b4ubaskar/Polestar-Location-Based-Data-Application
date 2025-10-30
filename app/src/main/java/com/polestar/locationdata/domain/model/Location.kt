package com.polestar.locationdata.domain.model

import java.time.LocalDateTime

/**
 * Represents a geographical location with timestamp
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90" }
        require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180" }
    }
}
package com.polestar.locationdata.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a historical session of location tracking
 * Contains all processed locations and POIs found during the session
 */
data class LocationSession(
    val sessionId: String = UUID.randomUUID().toString(),
    val carModel: CarModel,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val processedLocations: List<ProcessedLocationData> = emptyList(),
    val poisFound: List<PointOfInterest> = emptyList()
) {
    /**
     * Check if session is currently active
     */
    fun isActive(): Boolean = endTime == null

    /**
     * Get duration of session if ended
     */
    fun getDurationMinutes(): Long? {
        return endTime?.let {
            java.time.Duration.between(startTime, it).toMinutes()
        }
    }
}

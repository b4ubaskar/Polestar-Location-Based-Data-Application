package com.polestar.locationdata.domain.model

import java.time.LocalDateTime

/**
 * Filtered and processed location data after algorithm processing
 * Higher confidence = more reliable location
 */
data class ProcessedLocationData(
    val location: Location,
    val confidence: Double, // 0.0 to 1.0
    val carModel: CarModel,
    val processingTimestamp: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(confidence in 0.0..1.0) { "Confidence must be between 0.0 and 1.0" }
    }
}

package com.polestar.locationdata.domain.model

/**
 * Raw location data received from car sensors
 * This data needs to be filtered/processed before use
 */
data class RawLocationData(
    val location: Location,
    val accuracy: Double, // in meters
    val speed: Double? = null, // km/h
    val heading: Double? = null // degrees (0-360)
) {
    init {
        require(accuracy >= 0) { "Accuracy cannot be negative" }
        speed?.let { require(it >= 0) { "Speed cannot be negative" } }
        heading?.let { require(it in 0.0..360.0) { "Heading must be between 0 and 360" } }
    }
}

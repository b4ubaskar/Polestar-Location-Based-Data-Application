package com.polestar.locationdata.domain.algorithm

import com.polestar.locationdata.domain.model.*

/**
 * Interface for location filtering algorithms
 * Different implementations for different car models
 *
 * Will be implemented by the neighbouring team
 * Each car model may have different sensor characteristics requiring
 * specialized filtering approaches (Kalman, Particle Filter, etc.)
 */
interface LocationFilteringAlgorithm {
    /**
     * Process raw location data and return filtered result
     *
     * @param rawData The raw, unfiltered location data from car sensors
     * @return Processed location data with confidence score
     */
    fun filter(rawData: RawLocationData): ProcessedLocationData

    /**
     * Get the car model this algorithm is designed for
     *
     * @return The supported car model
     */
    fun getSupportedCarModel(): CarModel
}

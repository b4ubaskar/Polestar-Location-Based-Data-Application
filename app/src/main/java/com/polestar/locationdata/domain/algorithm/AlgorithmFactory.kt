package com.polestar.locationdata.domain.algorithm

import com.polestar.locationdata.domain.model.CarModel

/**
 * Factory to get the appropriate filtering algorithm for a car model
 * Implements the Strategy pattern
 */
interface AlgorithmFactory {
    /**
     * Get the filtering algorithm for the specified car model
     *
     * @param carModel The car model to get algorithm for
     * @return The appropriate filtering algorithm
     * @throws IllegalArgumentException if no algorithm exists for the model
     */
    fun getAlgorithm(carModel: CarModel): LocationFilteringAlgorithm
}
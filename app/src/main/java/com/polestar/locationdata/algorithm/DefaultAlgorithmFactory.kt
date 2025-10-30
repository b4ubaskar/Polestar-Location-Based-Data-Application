package com.polestar.locationdata.algorithm

import com.polestar.locationdata.domain.algorithm.AlgorithmFactory
import com.polestar.locationdata.domain.algorithm.LocationFilteringAlgorithm
import com.polestar.locationdata.domain.model.CarModel

/**
 * Default implementation of AlgorithmFactory
 * Maps car models to their specific filtering algorithms
 */
class DefaultAlgorithmFactory : AlgorithmFactory {

    private val algorithms: Map<CarModel, LocationFilteringAlgorithm> = mapOf(
        CarModel.POLESTAR_2 to Polestar2FilteringAlgorithm(),
        CarModel.POLESTAR_3 to Polestar3FilteringAlgorithm(),
        // For models without specific algorithms yet, reuse existing ones
        CarModel.POLESTAR_4 to Polestar2FilteringAlgorithm(),
        CarModel.POLESTAR_5 to Polestar3FilteringAlgorithm()
    )

    override fun getAlgorithm(carModel: CarModel): LocationFilteringAlgorithm {
        return algorithms[carModel]
            ?: throw IllegalArgumentException("No algorithm available for car model: $carModel")
    }

    /**
     * Get all supported car models
     */
    fun getSupportedCarModels(): Set<CarModel> = algorithms.keys
}

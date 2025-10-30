package com.polestar.locationdata

import com.polestar.locationdata.algorithm.DefaultAlgorithmFactory
import com.polestar.locationdata.data.repository.InMemoryLocationDataRepository
import com.polestar.locationdata.data.source.MockPoiDataSource
import com.polestar.locationdata.domain.model.*
import com.polestar.locationdata.service.LocationDataService

/**
 * Main application interface
 * This represents the contract that the UI layer would interact with
 *
 * No UI implementation is provided - only the interface and business logic
 */
interface LocationDataApplication {
    /**
     * User starts the application for their specific car model
     *
     * @param carModel The car model being used
     */
    fun start(carModel: CarModel)

    /**
     * User stops the application
     * Ends the current session and saves historical data
     */
    fun stop()

    /**
     * Update with new raw location data from car sensors
     * This would typically be called continuously while driving
     *
     * @param rawLocationData Raw sensor data from the car
     */
    fun updateLocation(rawLocationData: RawLocationData)

    /**
     * Request nearby POIs for current or specified location
     *
     * @param location Optional specific location (uses last known if null)
     * @param radiusMeters Search radius in meters
     * @param types Types of POIs to search for
     * @return List of nearby POIs
     */
    fun requestNearbyPois(
        location: Location? = null,
        radiusMeters: Double = 5000.0,
        types: List<PoiType> = listOf(PoiType.RESTAURANT, PoiType.CHARGING_STATION)
    ): List<PointOfInterest>

    /**
     * Get current session information
     *
     * @return Current active session or null
     */
    fun getCurrentSession(): LocationSession?

    /**
     * View all historical session data
     *
     * @return List of all past sessions
     */
    fun getHistory(): List<LocationSession>

    /**
     * Get a specific historical session by ID
     *
     * @param sessionId The session ID to retrieve
     * @return The session or null if not found
     */
    fun getHistoricalSession(sessionId: String): LocationSession?

    /**
     * Check if application is currently running
     *
     * @return true if an active session exists
     */
    fun isRunning(): Boolean
}

/**
 * Implementation of the LocationDataApplication interface
 * Wires together all the components and provides the main application logic
 */
class LocationDataApplicationImpl : LocationDataApplication {

    // Initialize all dependencies
    private val repository = InMemoryLocationDataRepository()
    private val algorithmFactory = DefaultAlgorithmFactory()
    private val poiDataSource = MockPoiDataSource()
    private val service = LocationDataService(repository, algorithmFactory, poiDataSource)

    // Track last known location for convenience
    private var lastKnownLocation: Location? = null

    override fun start(carModel: CarModel) {
        service.startApplication(carModel)
        println("✓ Application started for $carModel")
        println("  Session ID: ${service.getCurrentSessionData()?.sessionId}")
    }

    override fun stop() {
        val session = service.getCurrentSessionData()
        service.stopApplication()

        session?.let {
            println("✓ Application stopped")
            println("  Session duration: ${it.getDurationMinutes()} minutes")
            println("  Locations tracked: ${it.processedLocations.size}")
            println("  POIs found: ${it.poisFound.size}")
        }

        lastKnownLocation = null
    }

    override fun updateLocation(rawLocationData: RawLocationData) {
        val processedData = service.processLocationData(rawLocationData)
        lastKnownLocation = processedData.location

        println("📍 Location updated:")
        println("  Position: ${processedData.location.latitude}, ${processedData.location.longitude}")
        println("  Confidence: ${(processedData.confidence * 100).toInt()}%")
        println("  Accuracy: ${rawLocationData.accuracy}m")
    }

    override fun requestNearbyPois(
        location: Location?,
        radiusMeters: Double,
        types: List<PoiType>
    ): List<PointOfInterest> {
        val searchLocation = location ?: lastKnownLocation
        ?: throw IllegalStateException("No location available. Update location first.")

        val pois = service.fetchNearbyPois(searchLocation, radiusMeters, types)

        println("\n🔍 Found ${pois.size} nearby POIs within ${radiusMeters.toInt()}m:")
        pois.groupBy { it.type }.forEach { (type, poisOfType) ->
            println("  ${type.name}: ${poisOfType.size} locations")
        }

        return pois
    }

    override fun getCurrentSession(): LocationSession? {
        return service.getCurrentSessionData()
    }

    override fun getHistory(): List<LocationSession> {
        return service.getHistoricalSessions()
    }

    override fun getHistoricalSession(sessionId: String): LocationSession? {
        return service.getHistoricalSession(sessionId)
    }

    override fun isRunning(): Boolean {
        return service.isRunning()
    }
}

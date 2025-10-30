package com.polestar.locationdata.service

import com.polestar.locationdata.domain.algorithm.AlgorithmFactory
import com.polestar.locationdata.domain.model.*
import com.polestar.locationdata.domain.repository.LocationDataRepository
import com.polestar.locationdata.domain.repository.PoiDataSource

/**
 * Main application service coordinating location data processing
 *
 * This service orchestrates:
 * - Session management
 * - Location filtering using appropriate algorithms
 * - POI discovery and storage
 * - Historical data access
 */
class LocationDataService(
    private val repository: LocationDataRepository,
    private val algorithmFactory: AlgorithmFactory,
    private val poiDataSource: PoiDataSource
) {

    /**
     * Start the application for a specific car model
     * Creates a new session and ends any existing active session
     *
     * @param carModel The car model to start session for
     * @return The newly created session
     */
    fun startApplication(carModel: CarModel): LocationSession {
        return repository.startSession(carModel)
    }

    /**
     * Stop the application and end current session
     * Marks the session as complete with end timestamp
     */
    fun stopApplication() {
        repository.endSession()
    }

    /**
     * Process raw location data from car sensors
     * Applies the appropriate filtering algorithm for the car model
     *
     * @param rawData Raw location data from car sensors
     * @return Processed location data with confidence score
     * @throws IllegalStateException if no active session exists
     */
    fun processLocationData(rawData: RawLocationData): ProcessedLocationData {
        val session = repository.getCurrentSession()
            ?: throw IllegalStateException("No active session. Start application first.")

        // Get appropriate algorithm for the car model
        val algorithm = algorithmFactory.getAlgorithm(session.carModel)

        // Filter the raw data using the algorithm
        val processedData = algorithm.filter(rawData)

        // Save the processed data to repository
        repository.saveProcessedLocation(processedData)

        return processedData
    }

    /**
     * Fetch and store nearby points of interest
     *
     * @param location The center location to search from
     * @param radiusMeters Search radius in meters (default 5km)
     * @param types Types of POIs to search for
     * @return List of nearby POIs found
     * @throws IllegalStateException if no active session exists
     */
    fun fetchNearbyPois(
        location: Location,
        radiusMeters: Double = 5000.0,
        types: List<PoiType> = listOf(PoiType.RESTAURANT, PoiType.CHARGING_STATION)
    ): List<PointOfInterest> {
        val session = repository.getCurrentSession()
            ?: throw IllegalStateException("No active session. Start application first.")

        // Fetch POIs from external data source
        val pois = poiDataSource.fetchNearbyPois(location, radiusMeters, types)

        // Save each POI to the repository
        pois.forEach { repository.savePointOfInterest(it) }

        return pois
    }

    /**
     * Get current active session data
     *
     * @return Current session or null if no active session
     */
    fun getCurrentSessionData(): LocationSession? {
        return repository.getCurrentSession()
    }

    /**
     * Get all historical sessions for viewing past data
     * Sessions are sorted by start time (most recent first)
     *
     * @return List of all historical sessions
     */
    fun getHistoricalSessions(): List<LocationSession> {
        return repository.getAllSessions()
    }

    /**
     * Get a specific historical session by ID
     *
     * @param sessionId The session ID to retrieve
     * @return The session or null if not found
     */
    fun getHistoricalSession(sessionId: String): LocationSession? {
        return repository.getSessionById(sessionId)
    }

    /**
     * Check if application is currently running (has active session)
     *
     * @return true if application has an active session
     */
    fun isRunning(): Boolean {
        return repository.getCurrentSession() != null
    }
}

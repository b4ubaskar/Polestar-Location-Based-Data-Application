package com.polestar.locationdata.domain.repository

import com.polestar.locationdata.domain.model.*

/**
 * Repository for managing location data and sessions
 * Data persists only while application is running
 */
interface LocationDataRepository {
    /**
     * Save processed location data for current session
     *
     * @param data The processed location data to save
     * @throws IllegalStateException if no active session exists
     */
    fun saveProcessedLocation(data: ProcessedLocationData)

    /**
     * Save point of interest to current session
     *
     * @param poi The point of interest to save
     * @throws IllegalStateException if no active session exists
     */
    fun savePointOfInterest(poi: PointOfInterest)

    /**
     * Get the current active session
     *
     * @return Current session or null if no active session
     */
    fun getCurrentSession(): LocationSession?

    /**
     * Start a new session for the specified car model
     * Ends any existing active session
     *
     * @param carModel The car model for this session
     * @return The newly created session
     */
    fun startSession(carModel: CarModel): LocationSession

    /**
     * End the current active session
     * Does nothing if no active session exists
     */
    fun endSession()

    /**
     * Get all historical sessions sorted by start time (most recent first)
     *
     * @return List of all sessions
     */
    fun getAllSessions(): List<LocationSession>

    /**
     * Get a specific session by ID
     *
     * @param sessionId The session ID to retrieve
     * @return The session or null if not found
     */
    fun getSessionById(sessionId: String): LocationSession?
}

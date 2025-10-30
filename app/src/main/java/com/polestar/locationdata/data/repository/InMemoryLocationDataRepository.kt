package com.polestar.locationdata.data.repository

import com.polestar.locationdata.domain.model.*
import com.polestar.locationdata.domain.repository.LocationDataRepository
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory implementation of LocationDataRepository
 *
 * Thread-safe implementation using ConcurrentHashMap
 * Data persists only while the application is running
 * All data is lost when application stops
 */
class InMemoryLocationDataRepository : LocationDataRepository {

    // Thread-safe storage for sessions
    private val sessions = ConcurrentHashMap<String, LocationSession>()

    // Current active session (volatile for thread visibility)
    @Volatile
    private var currentSession: LocationSession? = null

    override fun saveProcessedLocation(data: ProcessedLocationData) {
        val session = currentSession
            ?: throw IllegalStateException("No active session. Start a session first.")

        // Create updated session with new location
        val updatedLocations = session.processedLocations + data
        val updatedSession = session.copy(processedLocations = updatedLocations)

        // Update storage
        sessions[session.sessionId] = updatedSession
        currentSession = updatedSession
    }

    override fun savePointOfInterest(poi: PointOfInterest) {
        val session = currentSession
            ?: throw IllegalStateException("No active session. Start a session first.")

        // Create updated session with new POI
        val updatedPois = session.poisFound + poi
        val updatedSession = session.copy(poisFound = updatedPois)

        // Update storage
        sessions[session.sessionId] = updatedSession
        currentSession = updatedSession
    }

    override fun getCurrentSession(): LocationSession? = currentSession

    override fun startSession(carModel: CarModel): LocationSession {
        // End current session if it exists
        currentSession?.let { endSession() }

        // Create new session
        val newSession = LocationSession(
            carModel = carModel,
            startTime = LocalDateTime.now(),
            endTime = null,
            processedLocations = emptyList(),
            poisFound = emptyList()
        )

        // Store and set as current
        sessions[newSession.sessionId] = newSession
        currentSession = newSession

        return newSession
    }

    override fun endSession() {
        currentSession?.let { session ->
            // Mark session as ended
            val endedSession = session.copy(endTime = LocalDateTime.now())
            sessions[session.sessionId] = endedSession
            currentSession = null
        }
    }

    override fun getAllSessions(): List<LocationSession> {
        return sessions.values
            .sortedByDescending { it.startTime }
            .toList()
    }

    override fun getSessionById(sessionId: String): LocationSession? {
        return sessions[sessionId]
    }

    /**
     * Clear all stored data (useful for testing or reset)
     */
    fun clearAll() {
        sessions.clear()
        currentSession = null
    }

    /**
     * Get statistics about stored data
     */
    fun getStatistics(): RepositoryStatistics {
        val allSessions = sessions.values
        return RepositoryStatistics(
            totalSessions = allSessions.size,
            activeSessions = if (currentSession != null) 1 else 0,
            totalLocations = allSessions.sumOf { it.processedLocations.size },
            totalPois = allSessions.sumOf { it.poisFound.size }
        )
    }

    data class RepositoryStatistics(
        val totalSessions: Int,
        val activeSessions: Int,
        val totalLocations: Int,
        val totalPois: Int
    )
}

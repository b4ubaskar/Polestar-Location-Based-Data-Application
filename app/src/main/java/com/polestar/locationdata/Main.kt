package com.polestar.locationdata

import com.polestar.locationdata.domain.model.*
import kotlin.random.Random

/**
 * Main entry point demonstrating the application usage
 * This simulates typical user interactions and data flow
 */
fun main() {
    println("═══════════════════════════════════════════════════════")
    println("  POLESTAR LOCATION-BASED DATA APPLICATION")
    println("═══════════════════════════════════════════════════════\n")

    val app: LocationDataApplication = LocationDataApplicationImpl()

    // Scenario 1: User starts application with Polestar 2
    println("SCENARIO 1: Starting application")
    println("─────────────────────────────────────────────────────")
    app.start(CarModel.POLESTAR_2)

    // Scenario 2: Simulate driving - multiple location updates
    println("\nSCENARIO 2: Simulating driving with location updates")
    println("─────────────────────────────────────────────────────")

    val startLocation = Location(
        latitude = 57.7089,  // Gothenburg, Sweden
        longitude = 11.9746
    )

    simulateDriving(app, startLocation, 5)

    // Scenario 3: Request nearby POIs
    println("\nSCENARIO 3: Requesting nearby points of interest")
    println("─────────────────────────────────────────────────────")

    val pois = app.requestNearbyPois(
        radiusMeters = 3000.0,
        types = listOf(PoiType.CHARGING_STATION, PoiType.RESTAURANT, PoiType.PARKING)
    )

    // Display some POI details
    println("\nDetailed POI Information:")
    pois.take(3).forEach { poi ->
        println("  • ${poi.name}")
        println("    Type: ${poi.type}")
        println("    Distance: ${poi.distance.toInt()}m")
        println("    Rating: ${poi.rating?.let { "%.1f★".format(it) } ?: "N/A"}")
        if (poi.additionalInfo.isNotEmpty()) {
            println("    Info: ${poi.additionalInfo.entries.joinToString(", ") { "${it.key}: ${it.value}" }}")
        }
        println()
    }

    // Scenario 4: Check current session
    println("\nSCENARIO 4: Current session status")
    println("─────────────────────────────────────────────────────")

    val currentSession = app.getCurrentSession()
    currentSession?.let {
        println("Session Information:")
        println("  ID: ${it.sessionId}")
        println("  Car Model: ${it.carModel}")
        println("  Started: ${it.startTime}")
        println("  Locations tracked: ${it.processedLocations.size}")
        println("  POIs discovered: ${it.poisFound.size}")
        println("  Status: ${if (it.isActive()) "ACTIVE" else "ENDED"}")
    }

    // Scenario 5: Stop application
    println("\nSCENARIO 5: Stopping application")
    println("─────────────────────────────────────────────────────")
    app.stop()

    // Scenario 6: Start new session with different car model
    println("\nSCENARIO 6: Starting new session with Polestar 3")
    println("─────────────────────────────────────────────────────")
    app.start(CarModel.POLESTAR_3)

    val location2 = Location(
        latitude = 59.3293,  // Stockholm, Sweden
        longitude = 18.0686
    )

    simulateDriving(app, location2, 3)
    app.requestNearbyPois(radiusMeters = 2000.0)
    app.stop()

    // Scenario 7: View historical data
    println("\nSCENARIO 7: Viewing historical data")
    println("─────────────────────────────────────────────────────")

    val history = app.getHistory()
    println("Total historical sessions: ${history.size}\n")

    history.forEachIndexed { index, session ->
        println("Session ${index + 1}:")
        println("  ID: ${session.sessionId}")
        println("  Car: ${session.carModel}")
        println("  Duration: ${session.getDurationMinutes()} minutes")
        println("  Started: ${session.startTime}")
        println("  Ended: ${session.endTime}")
        println("  Locations: ${session.processedLocations.size}")
        println("  POIs: ${session.poisFound.size}")
        println()
    }

    println("═══════════════════════════════════════════════════════")
    println("  APPLICATION DEMONSTRATION COMPLETED")
    println("═══════════════════════════════════════════════════════")
}

/**
 * Helper function to simulate driving with location updates
 */
private fun simulateDriving(
    app: LocationDataApplication,
    startLocation: Location,
    updateCount: Int
) {
    var currentLat = startLocation.latitude
    var currentLon = startLocation.longitude
    var speed = 50.0

    repeat(updateCount) { i ->
        // Simulate movement (roughly 100-200m per update)
        currentLat += Random.nextDouble(-0.001, 0.001)
        currentLon += Random.nextDouble(-0.001, 0.001)
        speed = Random.nextDouble(30.0, 100.0)

        val rawData = RawLocationData(
            location = Location(currentLat, currentLon),
            accuracy = Random.nextDouble(5.0, 25.0),
            speed = speed,
            heading = Random.nextDouble(0.0, 360.0)
        )

        app.updateLocation(rawData)

        // Simulate time passing
        Thread.sleep(500)
    }
}

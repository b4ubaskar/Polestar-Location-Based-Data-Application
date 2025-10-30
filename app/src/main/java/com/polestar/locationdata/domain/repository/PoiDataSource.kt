package com.polestar.locationdata.domain.repository

import com.polestar.locationdata.domain.model.*

/**
 * Data source for fetching Points of Interest
 * Abstracts external API calls
 */
interface PoiDataSource {
    /**
     * Fetch points of interest near a location
     *
     * In real implementation, this would call external APIs such as:
     * - Google Places API
     * - OpenStreetMap Overpass API
     * - HERE Places API
     * - Custom Polestar backend API
     *
     * @param location The center location to search from
     * @param radiusMeters The search radius in meters
     * @param types The types of POIs to search for
     * @return List of nearby POIs
     */
    fun fetchNearbyPois(
        location: Location,
        radiusMeters: Double,
        types: List<PoiType>
    ): List<PointOfInterest>
}

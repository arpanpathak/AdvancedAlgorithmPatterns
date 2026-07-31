package geo.quadtree

fun main() {
    // Create a quad tree covering a 20x20 km area centered at coordinates (0,0)
    val mapCenter = Point(0.0, 0.0)
    val locationTree = LocationQuadTree(mapCenter, 20.0, 20.0, capacity = 4)

    // Generate sample places
    val samplePlaces = generateSamplePlaces()

    // Add places to the quad tree
    samplePlaces.forEach { place ->
        locationTree.addPlace(place)
    }

    println("=== Google Maps-like Location Services Demo ===\n")

    // Test user location
    val userLocation = Point(0.5, 0.3)
    println("User location: (${userLocation.x}, ${userLocation.y})")

    // Find nearest restaurants
    println("\n--- Nearest Restaurants ---")
    val nearestRestaurants = locationTree.findNearestByType(userLocation, PlaceType.RESTAURANT, 3)
    nearestRestaurants.forEachIndexed { index, place ->
        val distance = place.location.distanceTo(userLocation)
        println("${index + 1}. ${place.name} - ${"%.2f".format(distance)} km away")
    }

    // Find nearest gas stations
    println("\n--- Nearest Gas Stations ---")
    val nearestGasStations = locationTree.findNearestByType(userLocation, PlaceType.GAS_STATION, 2)
    nearestGasStations.forEachIndexed { index, place ->
        val distance = place.location.distanceTo(userLocation)
        println("${index + 1}. ${place.name} - ${"%.2f".format(distance)} km away")
    }

    // Find all places within 2km radius
    println("\n--- All Places within 2km Radius ---")
    val nearbyRange = BoundingBox(userLocation, 2.0, 2.0)
    val nearbyPlaces = locationTree.findInRange(nearbyRange)
    nearbyPlaces.forEach { place ->
        val distance = place.location.distanceTo(userLocation)
        println("${place.type}: ${place.name} - ${"%.2f".format(distance)} km")
    }

    // K-nearest neighbors of any type
    println("\n--- 5 Nearest Places (Any Type) ---")
    val nearestPlaces = locationTree.findNearestNeighbors(userLocation, 5)
    nearestPlaces.forEachIndexed { index, place ->
        val distance = place.location.distanceTo(userLocation)
        println("${index + 1}. ${place.type}: ${place.name} - ${"%.2f".format(distance)} km")
    }
}

private fun generateSamplePlaces(): List<Place> {
    return listOf(
        // Restaurants
        Place("r1", "Pizza Palace", Point(0.8, 0.4), PlaceType.RESTAURANT),
        Place("r2", "Burger Barn", Point(1.2, -0.3), PlaceType.RESTAURANT),
        Place("r3", "Sushi Spot", Point(-0.5, 0.7), PlaceType.RESTAURANT),
        Place("r4", "Taco Town", Point(0.3, 1.1), PlaceType.RESTAURANT),

        // Gas Stations
        Place("g1", "Shell Station", Point(1.5, 0.6), PlaceType.GAS_STATION),
        Place("g2", "BP Gas", Point(-0.8, -0.2), PlaceType.GAS_STATION),
        Place("g3", "Exxon Mobile", Point(0.9, -0.9), PlaceType.GAS_STATION),

        // Hotels
        Place("h1", "Grand Hotel", Point(-1.2, 0.9), PlaceType.HOTEL),
        Place("h2", "Comfort Inn", Point(0.6, -1.3), PlaceType.HOTEL),

        // Hospitals
        Place("med1", "City Hospital", Point(-0.3, -0.8), PlaceType.HOSPITAL),

        // Parks
        Place("p1", "Central Park", Point(-1.5, -1.2), PlaceType.PARK)
    )
}

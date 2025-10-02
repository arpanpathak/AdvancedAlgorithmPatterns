package geo.quadtree

import kotlin.math.*

data class Point(val x: Double, val y: Double) {
    fun distanceTo(other: Point): Double {
        val dx = x - other.x
        val dy = y - other.y
        return sqrt(dx * dx + dy * dy)
    }
}

data class Place(
    val id: String,
    val name: String,
    val location: Point,
    val type: PlaceType
)

enum class PlaceType {
    RESTAURANT, GAS_STATION, HOTEL, HOSPITAL, PARK
}

class BoundingBox(val center: Point, val halfWidth: Double, val halfHeight: Double) {
    val x1: Double get() = center.x - halfWidth
    val x2: Double get() = center.x + halfWidth
    val y1: Double get() = center.y - halfHeight
    val y2: Double get() = center.y + halfHeight

    fun contains(point: Point): Boolean {
        return point.x in x1..x2 && point.y in y1..y2
    }

    fun intersects(other: BoundingBox): Boolean {
        return !(other.x1 > x2 || other.x2 < x1 || other.y1 > y2 || other.y2 < y1)
    }
}

class QuadNode(
    val boundary: BoundingBox,
    private val capacity: Int = 4
) {
    private val places = mutableListOf<Place>()
    private var divided = false

    private var northwest: QuadNode? = null
    private var northeast: QuadNode? = null
    private var southwest: QuadNode? = null
    private var southeast: QuadNode? = null

    fun insert(place: Place): Boolean {
        // Ignore if not in boundary
        if (!boundary.contains(place.location)) return false

        // If there's space and not divided, add here
        if (places.size < capacity && !divided) {
            places.add(place)
            return true
        }

        // Subdivide if necessary
        if (!divided) {
            subdivide()
        }

        // Try to insert into appropriate quadrant
        return when {
            northwest!!.insert(place) -> true
            northeast!!.insert(place) -> true
            southwest!!.insert(place) -> true
            southeast!!.insert(place) -> true
            else -> false
        }
    }

    private fun subdivide() {
        val halfW = boundary.halfWidth / 2
        val halfH = boundary.halfHeight / 2
        val center = boundary.center

        northwest = QuadNode(BoundingBox(
            Point(center.x - halfW, center.y - halfH),
            halfW, halfH
        ), capacity)

        northeast = QuadNode(BoundingBox(
            Point(center.x + halfW, center.y - halfH),
            halfW, halfH
        ), capacity)

        southwest = QuadNode(BoundingBox(
            Point(center.x - halfW, center.y + halfH),
            halfW, halfH
        ), capacity)

        southeast = QuadNode(BoundingBox(
            Point(center.x + halfW, center.y + halfH),
            halfW, halfH
        ), capacity)

        divided = true

        // Redistribute existing places
        places.forEach { place ->
            northwest!!.insert(place) || northeast!!.insert(place) ||
                    southwest!!.insert(place) || southeast!!.insert(place)
        }
        places.clear()
    }

    fun query(range: BoundingBox, found: MutableList<Place> = mutableListOf()): List<Place> {
        if (!boundary.intersects(range)) return found

        if (divided) {
            northwest?.query(range, found)
            northeast?.query(range, found)
            southwest?.query(range, found)
            southeast?.query(range, found)
        } else {
            places.filter { range.contains(it.location) }.forEach { found.add(it) }
        }

        return found
    }
}

class LocationQuadTree(
    center: Point,
    width: Double,
    height: Double,
    private val capacity: Int = 4
) {
    private val root = QuadNode(BoundingBox(center, width / 2, height / 2), capacity)

    fun addPlace(place: Place): Boolean = root.insert(place)

    fun findInRange(range: BoundingBox): List<Place> = root.query(range)

    fun findNearestNeighbors(location: Point, k: Int, filter: (Place) -> Boolean = { true }): List<Place> {
        val results = mutableListOf<Place>()
        var searchRadius = 0.1 // Start with small radius

        // Keep expanding search radius until we find at least k places
        while (results.size < k && searchRadius < 10.0) {
            val range = BoundingBox(location, searchRadius, searchRadius)
            val candidates = root.query(range).filter(filter)

            results.clear()
            results.addAll(candidates)
            searchRadius *= 2
        }

        // Sort by distance and return top k
        return results
            .sortedBy { it.location.distanceTo(location) }
            .take(k)
    }

    fun findNearestByType(location: Point, type: PlaceType, k: Int = 5): List<Place> {
        return findNearestNeighbors(location, k) { it.type == type }
    }
}

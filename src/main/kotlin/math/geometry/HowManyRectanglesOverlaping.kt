package math.geometry.another

data class Rectangle(
    val bottomX: Int,
    val bottomY: Int,
    val topX: Int,
    val topY: Int
)

fun overlaps(r1: Rectangle, r2: Rectangle): Boolean {
    return !(r1.topX <= r2.bottomX ||  // R1 is to the left of R2
            r1.bottomX >= r2.topX ||   // R1 is to the right of R2
            r1.topY <= r2.bottomY ||   // R1 is below R2
            r1.bottomY >= r2.topY)     // R1 is above R2
}

// Count all unique pairs that overlap
fun countOverlappingPairs(rectangles: List<Rectangle>): Int {
    return rectangles.indices.sumOf { i ->
        (i + 1 until rectangles.size).count { j ->
            overlaps(rectangles[i], rectangles[j])
        }
    }
}

// Check adjacent rectangles only
fun countAdjacentOverlaps(rectangles: List<Rectangle>): Int {
    return rectangles.windowed(2).count { (first, second) ->
        overlaps(first, second)
    }
}

fun main() {
    val rects = listOf(
        Rectangle(0, 5, 5, 10),
        Rectangle(3, 3, 7, 7),
        Rectangle(6, 2, 10, 6),
        Rectangle(15, 10, 20, 15)  // Isolated rectangle
    )

    println("Overlapping pairs: ${countOverlappingPairs(rects)}") // Output: 2
    println("Adjacent overlaps: ${countAdjacentOverlaps(rects)}") // Output: 1
}
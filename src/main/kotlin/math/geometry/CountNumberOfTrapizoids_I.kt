package math.geometry

fun countTrapezoids(points: Array<IntArray>): Int {
    val MOD = 1_000_000_007
    val yCounts = mutableMapOf<Int, Int>().apply {
        points.forEach { put(it[1], getOrDefault(it[1], 0) + 1) }
    }

    var result = 0L
    var prefixSum = 0L

    for (count in yCounts.values) {
        if (count >= 2) {
            val comb = count.toLong() * (count - 1) / 2
            result = (result + prefixSum * comb) % MOD
            prefixSum = (prefixSum + comb) % MOD
        }
    }

    return result.toInt()
}

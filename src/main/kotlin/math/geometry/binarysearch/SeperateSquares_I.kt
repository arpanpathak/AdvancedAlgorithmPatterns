package math.geometry.binarysearch

class SeperateSquares_I {
    fun separateSquares(squares: Array<IntArray>): Double {
        var (low, high, totalArea) = listOf(Double.MAX_VALUE, Double.MAX_VALUE, 0.0)

        for ((_, y, l) in squares) {
            val side = l.toDouble()
            low = minOf(low, y.toDouble())
            high = maxOf(high, y.toDouble())
            totalArea += side * side
        }

        fun areaBelow(yLine: Double): Double {
            var area = 0.0
            for ((_,y,l) in squares) {
                val side = l.toDouble()
                if (y + side <= yLine)
                    area += side * side
                else if(y < yLine)
                    area += side * (yLine - y)
            }

            return area
        }

        while (high - low > 1e-5) {
            val mid = (low + high) / 2
            when {
                areaBelow(mid) * 2 < totalArea -> low = mid
                else -> high = mid
            }
        }

        return low
    }
}
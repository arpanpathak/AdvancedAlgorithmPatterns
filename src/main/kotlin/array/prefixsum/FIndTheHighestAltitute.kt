package array.prefixsum

class FIndTheHighestAltitute {
    fun largestAltitude(gain: IntArray): Int {
        var (currentAltitude, highestAltitude) = Pair(0, 0)

        for (i in 0 until gain.size) {
            currentAltitude += gain[i]

            highestAltitude = maxOf(currentAltitude, highestAltitude)
        }

        return highestAltitude
    }
}
package binarysearch

class KokoEatingBanana {
    fun minEatingSpeed(piles: IntArray, h: Int): Int {
        // Left is the slowest speed, right is the fastest speed
        var left = 1
        var right = piles.maxOrNull()!!

        // Binary search to find the minimum valid eating speed
        while (left < right) {
            val mid = left + (right - left) / 2
            if (canEatAllBananas(piles, h, mid)) {
                // If Koko can eat all bananas at speed `mid`, try slower speeds
                right = mid
            } else {
                // Otherwise, try faster speeds
                left = mid + 1
            }
        }

        return left
    }

    // Helper function to check if Koko can finish all bananas in `h` hours at speed `k`
    private fun canEatAllBananas(piles: IntArray, h: Int, k: Int): Boolean {
        var totalHours = 0
        for (pile in piles) {
            // Add the number of hours it takes to finish this pile at speed `k`
            totalHours += (pile + k - 1) / k // Same as Math.ceil(pile / k)
        }
        return totalHours <= h
    }
}

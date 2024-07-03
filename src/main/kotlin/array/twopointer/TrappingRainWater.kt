package array.twopointer

class TrappingRainWater {
    /**
     * Using Dynamic Programming.
     */
    fun trap(height: IntArray): Int {
        val h = height.size
        if (h == 0) return 0

        val leftMax = IntArray(h).apply {
            this[0] = height[0]
            for (i in 1 until h) {
                this[i] = maxOf(this[i - 1], height[i])
            }
        }

        val rightMax = IntArray(h).also {
            it[h - 1] = height[h - 1]
            for (i in h - 2 downTo 0) {
                it[i] = maxOf(it[i + 1], height[i])
            }
        }

        return (0 until h).sumOf { i ->
            val minLeftRight = minOf(leftMax[i], rightMax[i])
            when {
                minLeftRight > height[i] -> minLeftRight - height[i]
                else -> 0
            }
        }

        // Another way
//        return (0 until n).sumOf { index ->
//            maxOf(minOf(leftMax[index], rightMax[index]) - height[index], 0)
//        }
    }

    // Two pointer method.
    fun trapConstantSpace(height: IntArray): Int {
        var (left, right, leftMax, rightMax, waterTrapped) = listOf(0, height.lastIndex, 0, 0, 0)

        while (left <= right) {
            if (height[left] <= height[right]) {
                waterTrapped += (leftMax - height[left]).coerceAtLeast(0)
                leftMax = maxOf(leftMax, height[left])
                left++
            } else {
                waterTrapped += (rightMax - height[right]).coerceAtLeast(0)
                rightMax = maxOf(rightMax, height[right])
                right--
            }
        }

        return waterTrapped
    }
}
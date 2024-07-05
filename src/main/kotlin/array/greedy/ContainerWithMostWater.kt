package array.greedy

class ContainerWithMostWater {
    fun maxArea(height: IntArray): Int {
        var (start, end) = Pair(0, height.lastIndex)
        var maxWater = 0
        while (start < end) {
            maxWater = maxOf(maxWater, minOf(height[start], height[end]) * (end - start))

            if (height[start] <= height[end]) {
                start++
            } else {
                end--
            }
        }

        return maxWater
    }
}
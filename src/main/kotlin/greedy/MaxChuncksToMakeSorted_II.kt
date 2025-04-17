package greedy

class MaxChuncksToMakeSorted_II {
    class Solution {
        fun maxChunksToSorted(arr: IntArray): Int {
            val n = arr.size

            // Step 1: Compute left_max
            val leftMax = IntArray(n)
            leftMax[0] = arr[0]
            for (i in 1 until n) {
                leftMax[i] = maxOf(leftMax[i-1], arr[i])
            }

            // Step 2: Compute right_min
            val rightMin = IntArray(n)
            rightMin[n-1] = arr[n-1]
            for (i in n-2 downTo 0) {
                rightMin[i] = minOf(rightMin[i+1], arr[i])
            }

            // Step 3: Count the number of chunks
            var chunks = 0
            for (i in 0 until n - 1) {
                if (leftMax[i] <= rightMin[i+1]) {
                    chunks++
                }
            }

            return chunks + 1 // +1 because the last chunk is counted implicitly
        }
    }

}
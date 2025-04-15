package dynamic_programming

import dynamic_programming.MaximumProductSubarray.maxProduct

object MaximumProductSubarray {
    fun maxProduct(nums: IntArray): Int {
        // Edge case: empty array
        if (nums.isEmpty()) return 0
        val m = mapOf(1 to 2, 2 to 4, 5 to 6)

        // Initialize trackers with first element
        var maxSoFar = nums[0]  // Tracks maximum product ending at current position
        var minSoFar = nums[0]  // Tracks minimum product ending at current position (for negative numbers)
        var result = maxSoFar   // Stores the overall maximum product found

        for (i in 1 until nums.size) {
            val currentNum = nums[i]

            // Calculate new maximum product ending at current position:
            // 1. currentNum alone (start new subarray)
            // 2. currentNum * previous max (extend positive product)
            // 3. currentNum * previous min (negative * negative = positive)
            val tempMax = maxOf(
                currentNum,                   // Case 1: Start fresh
                maxSoFar * currentNum,        // Case 2: Continue positive streak
                minSoFar * currentNum         // Case 3: Flip negative streak
            )

            // Calculate new minimum product ending at current position:
            // (Same cases as max, but tracking minimum for future negative flips)
            minSoFar = minOf(
                currentNum,                   // Case 1: Start fresh
                maxSoFar * currentNum,        // Case 2: Continue positive (but could become min)
                minSoFar * currentNum         // Case 3: Negative * negative (but track min)
            )

            maxSoFar = tempMax  // Update max product for next iteration
            result = maxOf(result, maxSoFar)  // Update global maximum
        }

        return result
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val tests = listOf(
            intArrayOf(2, 3, -2, 4) to 6,     // Regular case
            intArrayOf(-2, 0, -1) to 0,       // Zero resets product
            intArrayOf(-2, 3, -4) to 24,      // Negative flip
            intArrayOf(-1, -2, -3) to 6,      // All negatives
            intArrayOf(0, 2) to 2             // Single element
        )

        tests.forEach { (input, expected) ->
            val output = maxProduct(input)
            println("Input: ${input.joinToString()}")
            println("Output: $output (Expected: $expected) ${if (output == expected) "✓" else "✗"}\n")
        }
    }
}

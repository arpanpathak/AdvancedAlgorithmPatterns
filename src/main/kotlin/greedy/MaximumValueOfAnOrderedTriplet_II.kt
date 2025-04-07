package greedy

class MaximumValueOfAnOrderedTriplet_II {
    fun maximumTripletValue(nums: IntArray): Long {
        val n = nums.size
        var maxTripletValue: Long = 0
        var maxValueSoFar: Long = 0
        var maxDifference: Long = 0

        // Loop through the array to compute the maximum triplet value
        for (k in 0 until n) {
            // Calculate the triplet value by using the current maxDifference and nums[k]
            maxTripletValue = maxOf(maxTripletValue, maxDifference * nums[k].toLong())

            // Update maxDifference with the best possible difference (maxValueSoFar - nums[k])
            maxDifference = maxOf(maxDifference, maxValueSoFar - nums[k].toLong())

            // Update maxValueSoFar with the maximum value found so far
            maxValueSoFar = maxOf(maxValueSoFar, nums[k].toLong())
        }

        return maxTripletValue
    }
}
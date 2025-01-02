package array.Combinatorics

class Subsets {
    fun subsets(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        val currentSubset = mutableListOf<Int>()

        // Backtracking helper function
        fun backtrack(start: Int) {
            result.add(ArrayList(currentSubset))  // Add the current subset to the result

            for (i in start until nums.size) {
                currentSubset.add(nums[i])         // Include nums[i] in the current subset
                backtrack(i + 1)                   // Recurse for the next elements
                currentSubset.removeAt(currentSubset.size - 1)  // Backtrack: remove last added element
            }
        }

        // Start the backtracking process from index 0
        backtrack(0)

        return result
    }
}
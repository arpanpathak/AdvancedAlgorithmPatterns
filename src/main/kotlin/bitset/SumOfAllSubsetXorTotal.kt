package bitset

class SumOfAllSubsetXorTotal {
    fun subsetXORSum(nums: IntArray): Int {
        var result = 0
        // Capture each bit that is set in any of the elements
        for (num in nums) {
            result = result or num // Update the result by OR-ing each number
        }
        // Multiply by the number of subset XOR totals that will have each bit set
        return result shl (nums.size - 1) // Shift by (n-1) to account for all subsets
    }
}

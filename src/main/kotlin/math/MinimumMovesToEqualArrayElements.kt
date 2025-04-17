package math

class MinimumMovesToEqualArrayElements {
    fun minMoves(nums: IntArray): Int {
        val min = nums.minOrNull() ?: 0
        return nums.sumOf { it - min }
    }
}

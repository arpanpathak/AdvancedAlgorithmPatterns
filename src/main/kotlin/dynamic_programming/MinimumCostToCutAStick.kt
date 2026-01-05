package dynamic_programming

class MinimumCostToCutAStick {
    class Solution {
        fun minCost(n: Int, cuts: IntArray): Int {
            val points = (intArrayOf(0) + cuts.sortedArray() + n)
            val cache = Array(points.size) { IntArray(points.size) { -1 } }

            fun solve(left: Int, right: Int): Int = when {
                cache[left][right] != -1 -> cache[left][right]
                left + 1 >= right -> 0
                else -> (left + 1 until right).minOf { k ->
                    (points[right] - points[left]) + solve(left, k) + solve(k, right)
                }.also { cache[left][right] = it }
            }

            return solve(0, points.size - 1)
        }
    }
}
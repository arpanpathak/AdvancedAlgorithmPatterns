package array.dp

typealias State = Pair<Int, Int>
class BurstBallonsClean {
    class Solution {
        fun maxCoins(nums: IntArray): Int {
            val ballons = intArrayOf(1) + nums + 1

            val cache = mutableMapOf<State, Int>()

            fun solve(left: Int, right: Int): Int = cache.getOrPut(left to right) {
                when {
                    left > right -> 0
                    else -> (left..right).maxOf { k ->
                        ballons[left - 1] * ballons[k] * ballons[right + 1] +
                                solve(left, k - 1) + solve(k + 1, right)
                    }
                }
            }

            return solve(1, ballons.size - 2)
        }
    }
}
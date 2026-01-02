package dynamic_programming

class FrogJumpTopDown {
    data class State(val pos: Int, val k: Int)

    fun canCross(stones: IntArray): Boolean {
        val stoneSet = stones.toSet()
        val cache = mutableMapOf<State, Boolean>()

        fun solve(pos: Int, k: Int): Boolean =
            cache.getOrPut(State(pos, k)) {
                pos == stones.last() || (k - 1..k + 1).any { nextJump ->
                    nextJump > 0 && (pos + nextJump) in stoneSet && solve(pos + nextJump, nextJump)
                }
            }

        return solve(0, 0)
    }
}

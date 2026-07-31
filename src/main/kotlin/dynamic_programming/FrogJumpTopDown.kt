package dynamic_programming

class FrogJumpTopDown {
    data class State(val pos: Int, val k: Int)

    fun canCross(stones: IntArray): Boolean {
        val stoneSet = stones.toSet()
        val cache = mutableMapOf<State, Boolean>()
        fun isValidJump(pos: Int, nextJump: Int) = nextJump > 0 && (pos + nextJump) in stoneSet

        fun solve(pos: Int, k: Int): Boolean =
            cache.getOrPut(State(pos, k)) {
                pos == stones.last() || (k - 1..k + 1).any { nextJump ->
                    isValidJump(pos, nextJump) && solve(pos + nextJump, nextJump)
                }
            }

        return solve(0, 0)
    }
}

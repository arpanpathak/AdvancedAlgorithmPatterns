package dynamic_programming

class FrogJump {
    fun canCross(stones: IntArray): Boolean {
        if (stones[1] != 1) return false

        // Map: stone position -> set of possible jump sizes 'k' that can reach this stone
        val stoneMap = mutableMapOf<Int, MutableSet<Int>>()
        stones.forEach { stone ->
            stoneMap[stone] = mutableSetOf()
        }
        stoneMap[0]?.add(0) // Initial state

        for (stone in stones) {
            for (k in stoneMap[stone]!!) {
                for (step in k - 1..k + 1) {
                    if (step > 0) {
                        val nextStone = stone + step
                        // O(1) average time check for next stone existence
                        if (stoneMap.containsKey(nextStone)) {
                            stoneMap[nextStone]?.add(step)
                        }
                    }
                }
            }
        }

        return stoneMap[stones.last()]?.isNotEmpty() ?: false
    }
}

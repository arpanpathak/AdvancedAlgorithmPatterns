package google

fun countWays(n: Int, k: Int, m: Int): Int {
    val mod = 1_000_000_007
    data class State(val idx: Int, val k: Int, val rem: Int)

    val _cache = mutableMapOf<State, Int>()
    fun solve(idx: Int, k: Int, rem: Int): Int =
        _cache.getOrPut(State(idx, k, rem)) {
            when {
                k == 0 -> if (rem == 0) 1 else 0
                // Pruning: if coins remaining (n - idx) < coins needed (k), stop
                (n - idx) < k || idx == n -> 0
                else -> {
                    val skip = solve(idx + 1, k, rem)
                    val pick = solve(idx + 1, k - 1, (rem + (idx % m)) % m)
                    (skip + pick) % mod
                }
            }
        }

    return solve(0, k, 0)
}
/**
 * coins = [0, 1, 2, 3]
 */
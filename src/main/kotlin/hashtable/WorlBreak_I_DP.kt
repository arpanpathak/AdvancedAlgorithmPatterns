package hashtable

class WorlBreak_I_DP {
    enum class State { UNVISITED, FOUND, NOT_FOUND }

    fun wordBreak(s: String, wordDict: List<String>): Boolean {
        val set = wordDict.toHashSet()
        val dp = Array(s.length + 1) { State.UNVISITED }

        fun canPartition(i: Int): State {
            val currentState = dp[i]
            when  {
                i == s.length -> return State.FOUND
                currentState in listOf(State.FOUND, State.NOT_FOUND) -> return currentState
            }

            for (j in i until s.length) {
                val prefix = s.substring(i, j + 1)
                if (set.contains(prefix) && canPartition(j + 1) == State.FOUND) {
                    // Use 'also' for the side effect, but return the correct state
                    return State.FOUND.also { dp[i] = it }
                }
            }

            // Mark as NOT_FOUND and return the state
            return State.NOT_FOUND.also { dp[i] = it }
        }

        return canPartition(0) == State.FOUND
    }
}

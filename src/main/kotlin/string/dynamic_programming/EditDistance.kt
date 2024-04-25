package string.dynamic_programming

class EditDistance {
    private var dp = mutableMapOf<String,Int>()

    fun minDistance(word1: String, word2: String): Int {
        return minDistance(word1, word2, word1.length, word2.length)
    }

    fun minDistance(word1: String, word2: String, m: Int, n: Int): Int {
        val state = "$m.$n"

        return when {
            dp.contains(state) -> dp[state]!!
            m == 0 -> n
            n == 0 -> m
            word1[m - 1] == word2[n - 1] -> minDistance(word1, word2, m - 1, n - 1)
            else -> {
                val insert = minDistance(word1, word2, m, n - 1)
                val remove = minDistance(word1, word2, m - 1, n)
                val replace = minDistance(word1, word2, m - 1, n - 1)
                1 + minOf(insert, remove, replace)
            }
        }.also { dp[state] = it }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
        }
    }
}
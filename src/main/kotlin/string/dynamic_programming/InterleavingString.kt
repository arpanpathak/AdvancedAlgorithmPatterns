package string.dynamic_programming

class InterleavingString {
    fun isInterleave(s1: String, s2: String, s3: String): Boolean {
        if (s1.length + s2.length != s3.length) return false

        val memo = mutableMapOf<Pair<Int, Int>, Boolean>()

        fun dfs(i: Int, j: Int): Boolean {
            if (i == s1.length && j == s2.length) return true
            if (memo.containsKey(i to j)) return memo[i to j]!!

            val k = i + j
            var result = false

            if (i < s1.length && s1[i] == s3[k] && dfs(i + 1, j)) {
                result = true
            }
            if (j < s2.length && s2[j] == s3[k] && dfs(i, j + 1)) {
                result = true
            }

            memo[i to j] = result
            return result
        }

        return dfs(0, 0)
    }
}

package string.dynamic_programming

class RegularExpressionMatching {
    val dp = mutableMapOf<String, Boolean>()

    fun isMatch(s: String, p: String): Boolean {
        return dfs(s, p, 0, 0)
    }

    fun dfs(s: String, p: String, i: Int, j: Int ): Boolean {
        val state = "$i.$j"
        return when {
            dp.contains(state) -> dp[state]!!
            i >= s.length && j >= p.length -> true
            j >= p.length -> false
            else -> {
                val match = (i < s.length) &&(s[i] == p[j] || p[j] == '.')
                when {
                    j + 1 < p.length && p[j + 1] == '*'
                        -> dfs(s, p, i, j + 2) || (match && dfs(s, p, i + 1, j))
                    match -> dfs(s, p, i + 1, j + 1)
                    else -> false
                }
            }
        }.also { dp[state] = it }
    }
}
package string.dynamic_programming

class ShortestCommonSupersequence {
    fun shortestCommonSupersequence(X: String, Y: String): String? {
        val (m, n) = X.length to Y.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 1..m) for (j in 1..n) {
            dp[i][j] = if (X[i - 1] == Y[j - 1]) dp[i - 1][j - 1] + 1 else maxOf(dp[i - 1][j], dp[i][j - 1])
        }

        val scs = StringBuilder()
        var (i, j) = m to n

        while (i > 0 && j > 0) {
            when {
                X[i - 1] == Y[j - 1] -> { scs.append(X[i - 1]); i--; j-- }
                dp[i - 1][j] > dp[i][j - 1] -> { scs.append(X[i - 1]); i-- }
                else -> { scs.append(Y[j - 1]); j-- }
            }
        }

        // Add remaining characters from X or Y
        while (i > 0) { scs.append(X[i - 1]); i-- }
        while (j > 0) { scs.append(Y[j - 1]); j-- }

        // Reverse the result and return as string
        return scs.reverse().toString()
    }
}

// abc, axyzabc, yzmnbc

// yzmnbcaxyzabc

// a -> b -> c
// a -> x -> y -> x -> (a -> b -> c)

// y -> z -> m -> n -> b -> c



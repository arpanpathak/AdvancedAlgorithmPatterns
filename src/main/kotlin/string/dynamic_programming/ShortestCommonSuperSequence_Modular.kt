package string.dynamic_programming

fun buildLongestCommonSubsequenceDP(str1: String, str2: String): Array<IntArray> {
    // 1. Build DP Table for LCS Lengths
    // dp[i][j] stores the length of the LCS of str1[0..i-1] and str2[0..j-1]
    val dp = Array(str1.length + 1) { IntArray(str2.length + 1) }

    for (i in 1..str1.length) {
        for (j in 1..str2.length) {
            if (str1[i - 1] == str2[j - 1]) {
                dp[i][j] = 1 + dp[i - 1][j - 1]
            } else {
                dp[i][j] = maxOf(dp[i - 1][j], dp[i][j - 1])
            }
        }
    }

    return dp
}

fun shortestCommonSupersequence(str1: String, str2: String): String {
    val m = str1.length
    val n = str2.length

    val dp = buildLongestCommonSubsequenceDP(str1, str2)

    // 2. Backtrack to reconstruct the SCS
    val result = StringBuilder()
    var i = m
    var j = n

    while (i > 0 || j > 0) {
        when {
            // Case 1: str1 is exhausted, append remaining characters of str2
            i == 0 -> {
                result.append(str2[j - 1])
                j--
            }
            // Case 2: str2 is exhausted, append remaining characters of str1
            j == 0 -> {
                result.append(str1[i - 1])
                i--
            }
            // Case 3: Characters match (part of LCS), append once and move diagonally
            str1[i - 1] == str2[j - 1] -> {
                result.append(str1[i - 1])
                i--
                j--
            }
            // Case 4: Characters don't match, move to the path that created the LONGER LCS (which leads to the SHORTEST SCS)
            // If the LCS length came from dp[i-1][j], it means str1[i-1] was NOT part of the LCS, so we must add it.
            dp[i - 1][j] > dp[i][j - 1] -> {
                result.append(str1[i - 1])
                i--
            }
            // Otherwise, str2[j-1] was not part of the LCS, so we must add it.
            else -> {
                result.append(str2[j - 1])
                j--
            }
        }
    }

    // The string was built backwards, so reverse it
    return result.reverse().toString()
}

fun main() {
    val s1 = "abac"
    val s2 = "cab"
    val scs = shortestCommonSupersequence(s1, s2)
    println("Shortest Common Supersequence of \"$s1\" and \"$s2\": \"$scs\"") // Output: cabac
}
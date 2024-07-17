package string.dynamic_programming

class LongestCommonSubsequence {
    companion object {
        private lateinit var M: Array<IntArray>

        fun lcs(str1: String, str2: String): Int {
            M = Array(str1.length + 1) { IntArray(str2.length + 1) }
            for (i in 1..str1.length) {
                for (j in 1..str2.length) {
                    if (str1[i - 1] == str2[j - 1]) {
                        M[i][j] = M[i - 1][j - 1] + 1
                    } else {
                        M[i][j] = maxOf(M[i - 1][j], M[i][j - 1])
                    }
                }
            }
            return M[str1.length][str2.length]
        }

        fun printLCS(str: String, i: Int, j: Int) {
            if (i <= 0 || j <= 0) return
            if (M[i][j] != M[i][j - 1] && M[i][j] != M[i - 1][j] && M[i][j] == M[i - 1][j - 1] + 1) {
                print(str[j - 1])
                printLCS(str, i - 1, j - 1)
            } else if (M[i][j - 1] > M[i - 1][j]) {
                printLCS(str, i, j - 1)
            } else {
                printLCS(str, i - 1, j)
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            println(lcs("abcxgzggmn", "acttvvmnvt"))
            printLCS("acttvvmnvt", "abcxgzggmn".length, "acttvvmnvt".length) // Print in reverse order
        }
    }
}

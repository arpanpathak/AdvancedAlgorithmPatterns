package graph.euler.circuit

class CrackingTheSafe {
    fun crackSafe(n: Int, k: Int): String {
        val visited = mutableSetOf<String>()
        val result = StringBuilder()

        fun dfs(currentPrefix: String) {
            for (i in 0 until k) {
                val digit = i.toString()
                val nextPassword = currentPrefix + digit

                if (nextPassword !in visited) {
                    visited.add(nextPassword)

                    val nextPrefix = nextPassword.substring(1)

                    dfs(nextPrefix)

                    result.append(digit)
                }
            }
        }

        val startNode = "0".repeat(n - 1)

        dfs(startNode)
        return result.append(startNode).toString()
    }
    // n = 3
    // 000
    // 001
    // 010
    // 011
    // 100
    // 101
    // 110
    // 111
}
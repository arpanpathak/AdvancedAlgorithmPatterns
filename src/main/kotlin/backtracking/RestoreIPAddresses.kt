package backtracking

class RestoreIPAddresses {
    fun restoreIpAddresses(s: String): List<String> {
        val result = mutableListOf<String>()

        fun dfs(i: Int, path: MutableList<String>) {
            if (path.size == 4) {
                if (i == s.length) {
                    result.add(path.joinToString("."))
                }
                return
            }

            for (len in 1..3) {
                if (i + len <= s.length) {
                    val segment = s.substring(i, i + len)
                    // Skip invalid segments
                    if ((segment.length > 1 && segment[0] == '0') || segment.toInt() > 255) continue
                    path.add(segment)
                    dfs(i + len, path)
                    path.removeAt(path.size - 1)
                }
            }
        }

        dfs(0, mutableListOf())
        return result
    }
}
package backtracking

class PalindromePartitioning {
    fun partition(s: String): List<List<String>> {
        val result = mutableListOf<List<String>>()

        fun isPalindrome(s: String, left: Int, right: Int): Boolean {
            var (l, r) = left to right
            while (l < r) {
                if (s[l++] != s[r--]) return false
            }
            return true
        }

        fun dfs(start: Int, path: MutableList<String>) {
            if (start == s.length) {
                result.add(ArrayList(path))
                return
            }
            for (end in start until s.length) {
                if (isPalindrome(s, start, end)) {
                    path.add(s.substring(start, end + 1))
                    dfs(end + 1, path)
                    path.removeAt(path.size - 1)
                }
            }
        }

        dfs(0, mutableListOf())
        return result
    }
}

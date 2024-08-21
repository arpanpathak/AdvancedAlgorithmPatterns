package backtracking

class ExpressionAndAddOperatorsOptimized {
    fun addOperators(num: String, target: Int): List<String> {
        val res = ArrayList<String>()

        fun partition(start: Int, path: StringBuilder, sum: Long, prev: Long) {
            if (start == num.length) {
                if (sum == target.toLong()) {
                    res.add(path.toString())
                }
                return
            }

            for (i in start until num.length) {
                val s = num.substring(start..i)
                val current = s.toLong()

                // Skip invalid numbers with leading zeros
                if (s.length > 1 && s[0] == '0') break

                val len = path.length // Remember the length of the path before modification

                if (path.isEmpty()) {
                    path.append(s)
                    partition(i + 1, path, current, current)
                    path.setLength(len) // Revert the path to its previous state
                } else {
                    // Try adding '+' operator
                    path.append("+").append(s)
                    partition(i + 1, path, sum + current, current)
                    path.setLength(len)

                    // Try adding '-' operator
                    path.append("-").append(s)
                    partition(i + 1, path, sum - current, -current)
                    path.setLength(len)

                    // Try adding '*' operator
                    path.append("*").append(s)
                    partition(i + 1, path, sum - prev + prev * current, prev * current)
                    path.setLength(len)
                }
            }
        }

        partition(0, StringBuilder(), 0L, 0L)
        return res
    }
}
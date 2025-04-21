package greedy

class MinimumDeletionsToMakeStringBalanced {
    fun minimumDeletions(s: String): Int {
        val n = s.length
        var deletions = 0
        var bCount = 0

        for (ch in s) {
            if (ch == 'b') {
                bCount++
            } else {
                // ch == 'a'
                // Either delete this 'a' or delete one of the previous 'b's
                deletions = minOf(deletions + 1, bCount)
            }
        }

        return deletions
    }
}

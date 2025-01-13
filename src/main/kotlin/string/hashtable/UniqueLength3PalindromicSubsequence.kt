package string.hashtable

class UniqueLength3PalindromicSubsequence {
    fun countPalindromicSubsequence(s: String): Int {
        val charPositions = mutableMapOf<Char, MutableList<Int>>()

        // Step 1: Record positions of each character
        s.forEachIndexed { index, char ->
            charPositions.computeIfAbsent(char) { mutableListOf() }.add(index)
        }

        var count = 0

        // Step 2: Process each character's occurrences
        for ((char, positions) in charPositions) {
            val start = positions.first()
            val end = positions.last()

            // Skip if there's no room between the first and last occurrence
            if (end - start <= 1) continue

            val distinctChars = mutableSetOf<Char>()

            // Count distinct characters between first and last occurrence
            for (i in start + 1 until end) {
                distinctChars.add(s[i])
            }

            count += distinctChars.size
        }

        return count
    }
}
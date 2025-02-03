package string

class LongestCommonPrefix {
    fun longestCommonPrefix(strs: Array<String>): String {
        if (strs.isEmpty()) return ""

        // Use the first string as the reference
        for (i in strs[0].indices) {
            val char = strs[0][i]
            // Compare this character with the corresponding character in all other strings
            for (j in 1 until strs.size) {
                // If the current string is shorter or the characters don't match, return the prefix so far
                if (i >= strs[j].length || strs[j][i] != char) {
                    return strs[0].substring(0, i)
                }
            }
        }
        // If no mismatch is found, the entire first string is the common prefix
        return strs[0]
    }
}
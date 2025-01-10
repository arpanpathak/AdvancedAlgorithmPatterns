package string.pattern_matching

class `FindTheIndexofTheFirstOccurrenceIna String` {
    fun strStr(haystack: String, needle: String): Int {
        // If the needle is empty, return 0
        if (needle.isEmpty()) return 0

        // Use 'to' to assign both m and n in one line
        val (m, n) = needle.length to haystack.length

        // Step 1: Build the LPS array for the needle
        val lps = buildLPS(needle)
        println(needle)
        println(lps.contentToString())
        // Step 2: Search for the needle in the haystack using KMP algorithm
        var (i, j) = 0 to 0  // Pairing to initialize indices for haystack and needle

        while (i < n) {
            when {
                haystack[i] == needle[j] -> {  // Characters match
                    i++
                    j++
                }
                j > 0 -> j = lps[j - 1]  // Mismatch after some matches, use LPS array to skip ahead
                else -> i++  // No matches yet, move to the next character in the haystack
            }

            // If we've matched the entire needle, return the start index
            if (j == m) return i - j
        }

        return -1  // If no match found
    }

    // Function to build the LPS (Longest Prefix Suffix) array for the needle
    fun buildLPS(needle: String): IntArray {
        val lps = IntArray(needle.length)
        var len = 0  // Length of the previous longest prefix suffix
        var i = 1  // Start from the second character in the needle
        
        while (i < needle.length) {
            when {
                needle[i] == needle[len] -> {
                    len++
                    lps[i] = len
                    i++
                }
                len > 0 -> len = lps[len - 1]  // Use previously computed LPS values to skip
                else -> {
                    lps[i] = 0
                    i++
                }
            }
        }

        return lps
    }
}
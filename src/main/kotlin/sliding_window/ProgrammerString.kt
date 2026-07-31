package sliding_window

fun findNonProgrammerStringSections(t: String): Int {
    val targetFreq = "programmer".groupingBy { it }.eachCount()
    val windowFreq = mutableMapOf<Char, Int>()

    var windowStart = 0
    var sum = 0
    for (i in 0 until t.length) {
        if (t[i] !in targetFreq)
            continue

        // Update the frequency map of the current window
        windowFreq[t[i]] = (windowFreq[t[i]] ?: 0) + 1

        var minWindowLength = Int.MAX_VALUE

        // Shrink the window if the target string is matched
        while (windowStart <= i && isMatched(targetFreq, windowFreq)) {
            minWindowLength = minOf(i - windowStart + 1, minWindowLength)

            // Shrink the window from the left
            val startChar = t[windowStart]
            if (startChar in windowFreq) { // Check if the character exists in the map
                windowFreq[startChar] = windowFreq[startChar]!! - 1
                if (windowFreq[startChar] == 0) {
                    windowFreq.remove(startChar)
                }
            }

            windowStart++
        }

        // Add the length of the minimum window to the sum if a valid match was found
        if (minWindowLength != Int.MAX_VALUE) {
            sum += minWindowLength
            windowFreq.clear()
            windowStart = i + 1
        }
    }

    // Return the total number of non-matching characters
    return t.length - sum
}

fun isMatched(target: Map<Char, Int>, window: Map<Char, Int>): Boolean {
    for ((char, count) in target) {
        if (char !in window.keys || window[char]!! < count) {
            return false
        }
    }
    return true
}

fun main() {
    // Test case: expected output 8 (3 + 3 + 2 = 8)
    println(findNonProgrammerStringSections("xyzprogrammexxrabcprogrammermn"))
    println(findNonProgrammerStringSections("abprogrammmertc"))
}

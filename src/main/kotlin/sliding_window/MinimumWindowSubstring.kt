package sliding_window

fun minWindow(s: String, t: String): String {
    if (s.length < t.length) return ""

    val targetMap = t.groupingBy { it }.eachCount()
    val windowMap = mutableMapOf<Char, Int>()

    var left = 0
    var formed = 0
    var minLen = Int.MAX_VALUE
    var bestRange = 0..-1 // Using a Range for clean substring extraction

    for (right in s.indices) {
        val char = s[right]
        windowMap[char] = windowMap.getOrDefault(char, 0) + 1

        // Only increment 'formed' when frequency exactly matches target
        if (windowMap[char] == targetMap[char]) {
            formed++
        }

        // Shrink from left: Expand until valid, then shrink until invalid
        while (formed == targetMap.size) {
            if (right - left + 1 < minLen) {
                minLen = right - left + 1
                bestRange = left..right
            }

            val leftChar = s[left]
            // If the char we are removing was essential, decrement formed
            if (windowMap[leftChar] == targetMap[leftChar]) {
                formed--
            }
            windowMap[leftChar] = windowMap[leftChar]!! - 1
            left++
        }
    }

    return s.substring(bestRange)
}

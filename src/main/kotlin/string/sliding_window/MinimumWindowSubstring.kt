package string.sliding_window

class MinimumWindowSubstring {
    fun minWindow(s: String, t: String): String {
        // Return early if either string is empty
        if (s.isEmpty() || t.isEmpty()) return ""

        // Frequency map for characters in t (required characters)
        val required = mutableMapOf<Char, Int>()
        t.forEach { required[it] = required.getOrDefault(it, 0) + 1 }

        // Frequency map for the current window in s
        val window = mutableMapOf<Char, Int>()

        // Track the number of characters matched from the required set
        var matched = 0
        var (left, right) = 0 to 0  // Destructuring initialization for left and right pointers

        // Initialize minLength and minStart in a single line
        var (minLength, minStart) = Int.MAX_VALUE to 0

        // Sliding window logic
        while (right < s.length) {
            // Expand the window by moving the right pointer
            val rightChar = s[right]
            window[rightChar] = window.getOrDefault(rightChar, 0) + 1

            // Check if current window has all required characters in needed quantity
            when {
                required.containsKey(rightChar) && window[rightChar] == required[rightChar] -> matched++
            }

            // Shrink the window when all required characters are matched
            while (matched == required.size) {
                val currentLength = right - left + 1
                if (currentLength < minLength) {
                    minLength = currentLength
                    minStart = left
                }

                // Move left pointer to shrink the window
                val leftChar = s[left]
                window[leftChar] = window[leftChar]!! - 1

                when {
                    required.containsKey(leftChar) && window[leftChar]!! < required[leftChar]!! -> matched--
                }

                left++
            }

            // Move right pointer to expand the window
            right++
        }

        // Return the result
        return if (minLength == Int.MAX_VALUE) "" else s.substring(minStart, minStart + minLength)
    }
}

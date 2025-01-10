package string

class ValidNumber {
    fun isNumber(s: String): Boolean {
        // Initialize variables
        var (hasNum, hasDot, hasE, hasDigitsAfterE) = listOf(false, false, false, false)

        val str = s.trim() // Remove leading/trailing spaces
        for (i in str.indices) {
            val c = str[i]

            when {
                c in '0'..'9' -> {
                    hasNum = true
                    if (hasE) hasDigitsAfterE = true // Ensure digits appear after 'e'
                }
                c == '.' -> {
                    if (hasDot || hasE) return false // Can't have multiple dots or dots after 'e'
                    hasDot = true
                }
                c == 'e' || c == 'E' -> {
                    if (hasE || !hasNum) return false // Can't have multiple 'e' or 'e' without preceding number
                    hasE = true
                    hasDigitsAfterE = false // Reset this to ensure we validate digits after 'e'
                }
                c == '+' || c == '-' -> {
                    if (i != 0 && str[i - 1] != 'e' && str[i - 1] != 'E') return false // Sign only valid at start or after 'e'
                }
                else -> return false // Invalid character
            }
        }
        // Valid only if we have digits and valid 'e' part
        return hasNum && (!hasE || hasDigitsAfterE)
    }
}
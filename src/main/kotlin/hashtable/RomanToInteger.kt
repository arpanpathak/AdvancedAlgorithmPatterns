package hashtable

class RomanToInteger {
    fun romanToInt(s: String): Int {
        val romanMap = mapOf(
            'I' to 1,
            'V' to 5,
            'X' to 10,
            'L' to 50,
            'C' to 100,
            'D' to 500,
            'M' to 1000
        )

        var sum = 0
        var prevVal = 0
        for (i in s.length -1 downTo 0) {
            val current = romanMap[s[i]]!!
            when {
                current >= prevVal ->  sum += current
                else -> sum -= current
            }

            prevVal = current
        }

        return sum
    }
}
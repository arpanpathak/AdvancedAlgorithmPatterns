package string

class GreatestCommonDivisorOfStrings {
    fun gcd(a: Int, b: Int): Int {
        return when {
            a == b -> a
            a > b -> gcd(a - b , b)
            else -> gcd(a, b - a)
        }
    }

    fun gcdOfStrings(str1: String, str2: String): String {
        // Dhoyasha Foggy I didn't understand.
        if ((str1 + str2) != (str2 + str1))
            return ""

        val gcdLength = gcd(str1.length, str2.length)

        return str1.substring(0, gcdLength)
    }
}

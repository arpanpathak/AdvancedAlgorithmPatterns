package string

class IsomorphicString {
    fun encode(s: String): String {
        val map = mutableMapOf<Char, Int>()
        val sb = StringBuilder()
        var code = 0

        for (c in s) {
            if (c !in map) map[c] = code++
            sb.append(map[c]).append(" ")
        }

        return sb.toString()
    }

    fun isIsomorphic(s: String, t: String): Boolean {
        return encode(s) == encode(t)
    }
}

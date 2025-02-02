package string.stack

class DecodeString {
    private var index = 0

    fun decodeString(s: String): String {
        val result = StringBuilder()

        while (index < s.length && s[index] != ']') {
            val ch = s[index]
            when {
                !ch.isDigit() -> {
                    result.append(ch)
                    index++
                }
                else -> {
                    var k = 0
                    while (index < s.length && s[index].isDigit()) {
                        k = k * 10 + (s[index++] - '0')
                    }
                    index++ // Skip the opening bracket '['
                    val nestedDecodedString = decodeString(s)
                    index++ // Skip the closing bracket ']'

                    repeat(k) {
                        result.append(nestedDecodedString)
                    }
                }
            }
        }

        return result.toString()
    }

    // Short code
    fun decodeString1(s: String): String {
        var index = 0
        fun decode(): String {
            val result = StringBuilder()
            var num = 0
            while (index < s.length) {
                when (val char = s[index]) {
                    in '0'..'9' -> num = num * 10 + (char - '0')
                    '[' -> {
                        index++
                        val decodedString = decode()
                        result.append(decodedString.repeat(num))
                        num = 0
                    }
                    ']' -> return result.toString()
                    else -> result.append(char)
                }
                index++
            }
            return result.toString()
        }
        return decode()
    }
}
package string

class CountAndSay {
    fun countAndSay(n: Int): String {
        var result = "1"
        repeat(n - 1) {
            val nextSequence = buildString {
                var count = 1
                for (i in 1 until result.length) {
                    if (result[i] == result[i - 1]) {
                        count++
                    } else {
                        append(count).append(result[i - 1])
                        count = 1
                    }
                }
                append(count).append(result.last())
            }
            result = nextSequence
        }
        return result
    }
}
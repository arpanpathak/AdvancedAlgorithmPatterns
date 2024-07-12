package string

// Data Compression Algorithm

class StringCompression {
    fun compress(chars: CharArray): Int {
        var count = 0
        val sb = StringBuilder()

        for (i in chars.indices) {
            count++

            if (i  == chars.lastIndex || chars[i] != chars[i+1]) {
                sb.append(chars[i])

                if (count > 1) {
                    sb.append(count)
                }
                count = 0
            }
        }

        for (i in 0 until sb.length) chars[i] = sb.get(i)

        return sb.length
    }

    fun string_compression_inline(chars: CharArray): Int {
        var partitionLength = 0
        var i = 0

        while ( i < chars.size) {
            val currentChar = chars[i]
            var count = 0
            while ( i < chars.size && chars[i] == currentChar) {
                count++
                i++
            }
            chars[partitionLength++] = currentChar

            if (count > 1) {
                count.toString().forEach { ch -> chars[partitionLength++] = ch }
            }
        }

        return partitionLength

    }
}
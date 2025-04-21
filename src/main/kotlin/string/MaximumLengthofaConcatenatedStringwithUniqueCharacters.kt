package string

class MaximumLengthofaConcatenatedStringwithUniqueCharacters {
    fun maxLength(arr: List<String>): Int {
        var maxLen = 0
        val uniqueStrings = arr.filter { it.toCharArray().toSet().size == it.length }

        fun backtrack(index: Int, currentMask: Int, currentLength: Int) {
            if (index == uniqueStrings.size) {
                if (currentLength > maxLen) {
                    maxLen = currentLength
                }
                return
            }

            val str = uniqueStrings[index]
            var newMask = currentMask
            var canAdd = true

            for (c in str) {
                val bit = 1 shl (c - 'a')
                if (newMask and bit != 0) {
                    canAdd = false
                    break
                }
                newMask = newMask or bit
            }

            if (canAdd) {
                backtrack(index + 1, newMask, currentLength + str.length)
            }
            backtrack(index + 1, currentMask, currentLength)
        }

        backtrack(0, 0, 0)
        return maxLen
    }
}

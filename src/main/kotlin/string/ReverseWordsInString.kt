package string

class ReverseWordsInString {
    fun reverseWords(s: String): String {
        val words = s.split(" ").filter { it.isNotEmpty() }.toMutableList()

        // Two pointers
        var (start, end) = Pair(0, words.lastIndex)

        while (start < end) {
            words[end] = words[start].also { words[start] = words[end] }
            start++
            end--
        }

        return words.joinToString(separator = " ").trim()

    }

    fun swap(nums: MutableList<String>, i: Int, j: Int) {
        nums[j] = nums[i].also { nums[i] = nums[j] }
    }
}

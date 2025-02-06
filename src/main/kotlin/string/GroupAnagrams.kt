package string

class GroupAnagrams {
    fun groupAnagrams(strs: Array<String>): List<List<String>> {
        val anagramMap = mutableMapOf<List<Int>, MutableList<String>>()  // Using List<Int> as the key
        for (word in strs) {
            val count = IntArray(26)
            // Count the frequency of each character
            word.forEach { count[it - 'a']++ }
            anagramMap.getOrPut(count.toList()) { mutableListOf() }.add(word)
        }

        // Return the list of grouped anagrams
        return anagramMap.values.toList()
    }
}

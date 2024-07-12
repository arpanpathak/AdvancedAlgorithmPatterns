package array.hashtable

class UniqueNumberOfOccurences {
    fun uniqueOccurrences(arr: IntArray): Boolean {
        val map = mutableMapOf<Int, Int>()

        for (num in arr) {
            map[num] = map.getOrPut(num){1} + 1
        }

        return map.values.toSet().size == map.size
    }
}
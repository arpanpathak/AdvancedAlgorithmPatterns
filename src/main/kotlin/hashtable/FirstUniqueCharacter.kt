package hashtable

class FirstUniqueCharacter {
    fun firstUniqChar(s: String): Int {
        val count = s.groupingBy { it }.eachCount()
        return s.indexOfFirst { count[it] == 1 }
    }
}

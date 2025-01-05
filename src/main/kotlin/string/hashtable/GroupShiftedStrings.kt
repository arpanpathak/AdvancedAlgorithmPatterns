package string.hashtable

class GroupShiftedStrings {
    fun groupStrings(strings: Array<String>): List<List<String>> {
        val map = mutableMapOf<String, MutableList<String>>()

        for (str in strings) {
            val key = getKey(str)
            map.computeIfAbsent(key) { mutableListOf() }.add(str)
        }

        return map.values.toList()
    }

    private fun getKey(str: String): String {
        return buildString {
            for (i in 1 until str.length) {
                append( ((str[i] - str[i - 1] + 26) % 26).toString() )
            }
        }
    }
}
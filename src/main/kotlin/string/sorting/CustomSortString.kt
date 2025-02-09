package string.sorting

class CustomSortString {
    fun customSortString(order: String, s: String): String {
        val orderMap = mutableMapOf<Char, Int>()
        order.forEachIndexed{ index, ch -> orderMap[ch] = index }

        return s.toCharArray().sortedBy{ ch -> orderMap[ch] ?: Int.MAX_VALUE }
            .joinToString("")
    }
}
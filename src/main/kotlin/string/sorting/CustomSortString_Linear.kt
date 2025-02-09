package string.sorting

class CustomSortString_Linear {
    fun customSortString(order: String, s: String): String {
        // Count frequency of each character
        val countMap = s.groupingBy { it }.eachCount().toMutableMap()

        val result = StringBuilder()
        order.forEach { ch ->
            countMap[ch]?.let {
                result.append(ch.toString().repeat(it))
                countMap.remove(ch)
            }
        }

        // Append Remaining characters which are not in order
        countMap.forEach { (ch, count) -> result.append(ch.toString().repeat(count)) }
        return result.toString()
    }
}
package sliding_window

class PartitionLabels {

    fun partitionLabels(s: String): List<Int> {
        val partitionLengths = mutableListOf<Int>()
        val lastSeenAt = mutableMapOf<Char, Int> ().apply {
            s.forEachIndexed{index, char -> this[char] = index }
        }
        var (startIndex, maxLastIndex) = Pair(0,0)
        s.forEachIndexed{ index, char ->
           // Find the furthest point ( max index ) the current partition needs to reach
            maxLastIndex =  maxOf(maxLastIndex, lastSeenAt[char]!!)

            // If the current index is the max index of the current partition
            if (index == maxLastIndex) {
                partitionLengths.add(index - startIndex + 1)
                startIndex = index + 1
            }
        }
        return partitionLengths
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
        }
    }
}
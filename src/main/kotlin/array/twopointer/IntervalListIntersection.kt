package array.twopointer

class IntervalListIntersection {
    fun intervalIntersection(firstList: Array<IntArray>, secondList: Array<IntArray>): Array<IntArray> {
        val result = mutableListOf<IntArray>()

        var (i, j) = 0 to 0

        while (i < firstList.size && j < secondList.size) {
            val (start1, end1) = firstList[i]
            val (start2, end2) = secondList[j]

            val startMax = maxOf(start1, start2)
            val endMin = minOf(end1, end2)

            if (startMax <= endMin) {
                result.add(intArrayOf(startMax, endMin))
            }

            if (end1 < end2) i++ else j++
        }

        return result.toTypedArray()
    }
}
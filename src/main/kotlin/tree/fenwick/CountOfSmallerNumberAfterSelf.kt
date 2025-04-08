package tree.fenwick

class CountOfSmallerNumberAfterSelf {
    fun countSmaller(nums: IntArray): List<Int> {
        if (nums.isEmpty()) return emptyList()

        // Step 1: Coordinate compression
        val sorted = nums.toTypedArray().sorted()
        val rankMap = HashMap<Int, Int>()
        var rank = 1
        for (num in sorted) {
            if (num !in rankMap) {
                rankMap[num] = rank++
            }
        }

        val bit = FenwickTree(rank)
        val res = IntArray(nums.size)

        // Step 2: Traverse from right to left
        for (i in nums.lastIndex downTo 0) {
            val r = rankMap[nums[i]]!!
            res[i] = bit.query(r - 1) // count of smaller numbers
            bit.update(r, 1)          // add current number
        }

        return res.toList()
    }

    class FenwickTree(size: Int) {
        private val tree = IntArray(size + 2)

        fun update(index: Int, value: Int) {
            var i = index
            while (i < tree.size) {
                tree[i] += value
                i += i and -i
            }
        }

        fun query(index: Int): Int {
            var sum = 0
            var i = index
            while (i > 0) {
                sum += tree[i]
                i -= i and -i
            }
            return sum
        }
    }
}

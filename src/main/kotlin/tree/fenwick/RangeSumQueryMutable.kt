package tree.fenwick

class RangeSumQueryMutable(nums: IntArray) {
    private val tree = IntArray(nums.size + 1)

    init {
        for (i in nums.indices) update(i, nums[i])
    }

    private fun updateBIT(i: Int, delta: Int) {
        var index = i + 1
        while (index <= tree.size - 1) {
            tree[index] += delta
            index += index and -index
        }
    }

    fun update(index: Int, `val`: Int) {
        val current = sumRange(index, index)
        updateBIT(index, `val` - current)
    }

    private fun sum(index: Int): Int {
        var i = index + 1
        var result = 0
        while (i > 0) {
            result += tree[i]
            i -= i and -i
        }
        return result
    }

    fun sumRange(left: Int, right: Int): Int {
        return sum(right) - sum(left - 1)
    }
}

/**
 * Your NumArray object will be instantiated and called as such:
 * var obj = NumArray(nums)
 * obj.update(index,`val`)
 * var param_2 = obj.sumRange(left,right)
 */
package bitset

class SingleNumber {
    fun singleNumber(nums: IntArray): Int {
        var xorSum = 0
        nums.forEach { xorSum = xorSum xor it}
        return xorSum
    }
}
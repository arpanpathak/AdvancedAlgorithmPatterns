package bitset

class SingleNumber3 {
    fun singleNumber(nums: IntArray): IntArray {
        var xorSum = 0
        for (num in nums) {
            xorSum = xorSum.xor(num)
        }

        val rightmostSetBit = xorSum and xorSum
        var (a, b) = listOf(0, 0)

        for (num in nums) {
            if (num and rightmostSetBit !=0) {
                a = a.xor(num)
            } else {
                b = b.xor(num)
            }
        }

        return intArrayOf(a, b)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val test = SingleNumber3()

            println("Single numbers are ${test.singleNumber(intArrayOf(1,2,1,3,2,5))}")
        }
    }
}